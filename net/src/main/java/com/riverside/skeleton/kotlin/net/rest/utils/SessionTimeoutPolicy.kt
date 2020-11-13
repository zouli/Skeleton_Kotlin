package com.riverside.skeleton.kotlin.net.rest.utils

import com.riverside.skeleton.kotlin.net.rest.error.SessionTimeoutThrowable
import com.riverside.skeleton.kotlin.net.rest.handler.SessionHandlerFactory
import io.reactivex.Flowable
import io.reactivex.functions.Function

/**
 * Session超时策略类    1.0
 * b_e  2019/10/12
 */
class SessionTimeoutPolicy : Function<Flowable<out Throwable>, Flowable<*>> {
    override fun apply(observable: Flowable<out Throwable>): Flowable<*> {
        return observable.flatMap { throwable ->
            if (throwable is SessionTimeoutThrowable) {
                // 重新登录
                SessionHandlerFactory.reLogin()
                    .doOnError { mRefreshTokenError ->
                        if (mRefreshTokenError is SessionTimeoutThrowable) {
                            //跳转到登录画面
                            SessionHandlerFactory.toLogin(false)
                        } else {
                            Flowable.error<Any>(mRefreshTokenError)
                        }
                    }
            } else Flowable.error<Any>(throwable)
        }
    }
}