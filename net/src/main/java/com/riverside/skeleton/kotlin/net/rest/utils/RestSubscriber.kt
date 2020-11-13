package com.riverside.skeleton.kotlin.net.rest.utils

import org.reactivestreams.Subscription
import org.reactivestreams.Subscriber

open class RestSubscriber<T> : Subscriber<T> {
    override fun onSubscribe(s: Subscription) {
        s.request(Long.MAX_VALUE)
    }

    override fun onNext(t: T) {

    }

    override fun onError(t: Throwable) {

    }

    override fun onComplete() {

    }
}