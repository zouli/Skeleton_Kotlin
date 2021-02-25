package com.riverside.skeleton.kotlin.net.rest.utils

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription

open class RestSubscriber<T> : Subscriber<T> {
    private lateinit var next: (t: T) -> Unit
    private lateinit var error: (t: Throwable) -> Unit
    private lateinit var complete: () -> Unit

    override fun onSubscribe(s: Subscription) {
        s.request(Long.MAX_VALUE)
    }

    fun onNext(block: (t: T) -> Unit) {
        this.next = block
    }

    override fun onNext(t: T) {
        if (::next.isInitialized) next(t)
    }

    fun onError(block: (t: Throwable) -> Unit) {
        this.error = block
    }

    override fun onError(t: Throwable) {
        if (::error.isInitialized) error(t)
    }

    fun onComplete(block: () -> Unit) {
        this.complete = block
    }

    override fun onComplete() {
        if (::complete.isInitialized) complete()
    }
}

fun <T, R : RestSubscriber<T>> Flowable<T>.restSubscribe(init: R.() -> Unit): Disposable {
    val restSubscriber = RestSubscriber<T>() as R
    restSubscriber.init()

    return this.subscribe(
        { restSubscriber.onNext(it) },
        { restSubscriber.onError(it) },
        { restSubscriber.onComplete() })
}