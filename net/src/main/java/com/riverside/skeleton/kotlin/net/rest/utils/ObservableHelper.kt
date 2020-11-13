package com.riverside.skeleton.kotlin.net.rest.utils

import com.riverside.skeleton.kotlin.net.ConstUrls
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

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