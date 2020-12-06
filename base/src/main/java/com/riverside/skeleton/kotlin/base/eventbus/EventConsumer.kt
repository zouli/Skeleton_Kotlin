package com.riverside.skeleton.kotlin.base.eventbus

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

/**
 * EventConsumer    1.0
 *
 * b_e      2020/12/05
 */
@ExperimentalCoroutinesApi
data class EventConsumer<T>(
    val coroutineScope: CoroutineScope, val dispatcher: CoroutineDispatcher,
    val next: (T) -> Unit, val exception: ((Throwable) -> Unit)? = null
) {
    private val channel = Channel<T>()

    init {
        coroutineScope.launch {
            channel.consumeEach { o ->
                launch(dispatcher) {
                    exception?.let {
                        try {
                            next(o)
                        } catch (e: Exception) {
                            exception.invoke(e)
                        }
                    } ?: next(o)
                }
            }
        }
    }

    fun postEvent(o: Any) {
        if (!channel.isClosedForSend) {
            coroutineScope.launch {
                channel.send(o as T)
            }
        }
    }

    fun cancel() {
        channel.cancel()
    }
}