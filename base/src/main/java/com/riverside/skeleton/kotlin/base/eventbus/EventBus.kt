package com.riverside.skeleton.kotlin.base.eventbus

import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

/**
 * EventBus     1.0
 *
 * b_e      2020/12/05
 */
object EventBus : CoroutineScope {
    val MAIN: CoroutineDispatcher = Dispatchers.Main
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + job

    val stickyEventMap = ConcurrentHashMap<Class<*>, Any>()
    val contextMap = ConcurrentHashMap<Class<*>, MutableList<EventConsumer<*>>>()

    /**
     * 发送信息
     */
    fun post(o: Any) = postEvent(o)

    /**
     * 发生信息 Sticky模式
     */
    fun postSticky(o: Any) {
        synchronized(stickyEventMap) {
            stickyEventMap.put(o.javaClass, o)
        }
        post(o)
    }

    /**
     * 注册接收者
     */
    inline fun <reified T : Any> register(
        dispatcher: CoroutineDispatcher = MAIN,
        noinline next: (T) -> Unit
    ): EventConsumer<T> = register(dispatcher, next, null)

    inline fun <reified T : Any> register(
        dispatcher: CoroutineDispatcher = MAIN,
        noinline next: (T) -> Unit,
        noinline exception: ((Throwable) -> Unit)? = null
    ): EventConsumer<T> =
        EventConsumer(this, dispatcher, next, exception).also {
            if (contextMap[T::class.java] == null)
                contextMap.put(T::class.java, mutableListOf(it))
            else
                contextMap.get(T::class.java)?.add(it)
        }

    inline fun <reified T : Any> registerSticky(
        dispatcher: CoroutineDispatcher = MAIN,
        noinline next: (T) -> Unit
    ): EventConsumer<T> = registerSticky(dispatcher, next, null)

    inline fun <reified T : Any> registerSticky(
        dispatcher: CoroutineDispatcher = MAIN,
        noinline next: (T) -> Unit,
        noinline exception: ((Throwable) -> Unit)? = null
    ): EventConsumer<T> =
        EventConsumer(this, dispatcher, next, exception).also {
            if (contextMap[T::class.java] == null)
                contextMap.put(T::class.java, mutableListOf(it))
            else
                contextMap.get(T::class.java)?.add(it)
        }.also {
            synchronized(stickyEventMap) {
                stickyEventMap[T::class.java]?.run {
                    postEvent(this)
                }
            }
        }


    /**
     * 注销观察者
     */
    inline fun <reified T : Any> unregister(eventData: EventConsumer<T>) {
        val cloneContextMap = ConcurrentHashMap<Class<*>, MutableList<EventConsumer<*>>>()
        cloneContextMap.putAll(contextMap)
        cloneContextMap.filter { it.key == T::class.java }
            .forEach { (_, eventDataList) ->
                eventData.cancel()
                eventDataList.remove(eventData)
            }
    }

    /**
     * 注销所有观察者
     */
    fun unregisterAll() {
        coroutineContext.cancelChildren()
        contextMap.forEach { (_, eventDataList) ->
            eventDataList.forEach {
                it.cancel()
            }
            eventDataList.clear()
        }
        contextMap.clear()
    }

    /**
     * 判断是否有观察者
     */
    val hasSubscribers get() = contextMap.size > 0

    /**
     * 删除Sticky模式保存的数据
     */
    inline fun <reified T : Any> removeSticky() =
        synchronized(stickyEventMap) { stickyEventMap.remove(T::class.java) }

    /**
     * 清空所有Sticky模式保存的数据
     */
    @Synchronized
    fun clearSticky() = stickyEventMap.clear()

    fun postEvent(o: Any) {
        val cloneContextMap = ConcurrentHashMap<Class<*>, MutableList<EventConsumer<*>>>()
        cloneContextMap.putAll(contextMap)
        cloneContextMap.filter { it.key == o.javaClass || it.key == o.javaClass.superclass }
            .forEach { (_, eventDataList) ->
                eventDataList.forEach {
                    it.postEvent(o)
                }
            }
    }
}