package com.riverside.skeleton.kotlin.net.rest.utils

import com.riverside.skeleton.kotlin.slog.SLog
import io.reactivex.functions.Function
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class RetryPolicy(
    private val count: Int = 3,
    private val delay: Int = 3000,
    private val increaseDelay: Int = 3000
) :
    Function<Flowable<out Throwable>, Flowable<*>> {

    override fun apply(observable: Flowable<out Throwable>): Flowable<*> {
        return observable
            .zipWith(
                Flowable.range(1, count + 1),
                BiFunction<Throwable, Int, ThrowableCountWrapper> { throwable, integer ->
                    ThrowableCountWrapper(throwable, integer)
                }).flatMap(Function<ThrowableCountWrapper, Flowable<*>> { wrapper ->
                if ((wrapper.throwable is ConnectException
                            || wrapper.throwable is SocketTimeoutException
                            || wrapper.throwable is TimeoutException) && wrapper.count < count + 1
                ) {
                    //如果超出重试次数也抛出错误，否则默认是会进入onCompleted
                    SLog.e("在" + delay + "毫秒后重试,共" + count + "次")
                    return@Function Flowable.timer(
                        (delay + (wrapper.count - 1) * increaseDelay).toLong(),
                        TimeUnit.MILLISECONDS
                    )
                }
                Flowable.error<Any>(wrapper.throwable)
            })
    }

    private inner class ThrowableCountWrapper(val throwable: Throwable, val count: Int)
}