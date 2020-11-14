package com.riverside.skeleton.kotlin.net.rest.utils

import io.reactivex.Flowable
import kotlin.reflect.KClass

/**
 * 网络连接服务类 1.0
 * b_e  2020/11/14
 */
fun <T : Any> retrofit(restService: KClass<T>): T =
    RetrofitBindHelper.getInstance().doBind(restService.java)

fun <T> Flowable<T>.startReading(): Flowable<T> = this.compose(ObservableHelper.startReading())

fun <T> Flowable<T>.checkSessionTimeout(): Flowable<T> =
    this.compose(ObservableHelper.checkSessionTimeout())

fun <T> Flowable<List<T>>.iterate(): Flowable<T> = this.compose(ObservableHelper.forEach())

fun <T> Flowable<T>.indexed(indexes: Flowable<Int>): Flowable<Pair<Int, T>> =
    this.compose(ObservableHelper.indexed(indexes))

fun <T> Flowable<T>.toMainThread(): Flowable<T> = this.compose(ObservableHelper.toMainThread())

fun <T, R> Flowable<T>.next(black: Flowable<T>.() -> Flowable<R>): Flowable<R> =
    this.startReading().run { black() }.toMainThread()