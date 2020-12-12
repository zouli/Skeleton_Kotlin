package com.riverside.skeleton.kotlin.net.rest.utils

import com.riverside.skeleton.kotlin.net.ConstUrls
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableTransformer
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers

object ObservableHelper {
    /**
     * 开始读取网络数据
     */
    fun <T> startReading() = FlowableTransformer<T, T> {
        it.subscribeOn(Schedulers.io())
            .retryWhen(RetryPolicy(ConstUrls.CONNECT_RETRY_COUNT, ConstUrls.CONNECT_RETRY_DELAY))
    }

    /**
     * 检查Session是否超时
     */
    fun <T> checkSessionTimeout() = FlowableTransformer<T, T> {
        it.subscribeOn(Schedulers.io()).retryWhen(SessionTimeoutPolicy())
    }

    /**
     * 遍历List对象
     */
    fun <T> forEach() = FlowableTransformer<List<T>, T> {
        it.flatMap { list -> Flowable.fromIterable(list) }
    }

    /**
     * 进入主线程
     */
    fun <T> toMainThread() = FlowableTransformer<T, T> {
        it.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .unsubscribeOn(Schedulers.io())
    }

    /**
     * 添加索引
     */
    fun <T> indexed(indexs: Flowable<Int>) = FlowableTransformer<T, Pair<Int, T>> {
        it.zipWith(indexs, BiFunction { item, index -> Pair(index, item) })
    }
}