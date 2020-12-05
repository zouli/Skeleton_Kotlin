package com.riverside.skeleton.kotlin.net.eventbus

import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor

/**
 * RxBus 1.1
 * 支持Sticky模式
 *
 * b_e  2019/07/25
 */
object RxBus {
    val stickyEventMap = mutableMapOf<Any, Any>()
    val mRxBusObservable = PublishProcessor.create<Any>().toSerialized()

    /**
     * 发送信息
     */
    fun post(o: Any) = mRxBusObservable.onNext(o)

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
     * 生产观察者
     */
    inline fun <reified T : Any> toFlowable(): Flowable<T> = mRxBusObservable.ofType(T::class.java)

    /**
     * 生产观察者 Sticky模式
     */
    inline fun <reified T : Any> toStickyFlowable(): Flowable<T> {
        synchronized(stickyEventMap) {
            stickyEventMap[T::class.java]?.run {
                return Flowable.just(this as T).mergeWith(toFlowable())
            }
        }
        return toFlowable()
    }

    /**
     * 注册接收者
     */
    inline fun <reified T : Any> register(
        scheduler: Scheduler? = null, crossinline next: (T) -> Unit
    ): Disposable =
        toStickyFlowable<T>().apply {
            scheduler?.let { this.observeOn(it) }
        }.subscribe { next(it) }

    /**
     * 注销观察者
     */
    fun unRegister(disposable: Disposable) {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    /**
     * 判断是否有观察者
     */
    val hasSubscribers get() = mRxBusObservable.hasSubscribers()

    /**
     * 删除Sticky模式保存的数据
     */
    @Synchronized
    inline fun <reified T : Any> removeSticky() = stickyEventMap.remove(T::class.java)

    /**
     * 清空所有Sticky模式保存的数据
     */
    @Synchronized
    fun clearSticky() = stickyEventMap.clear()
}
