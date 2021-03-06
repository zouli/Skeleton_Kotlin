package com.riverside.skeleton.kotlin.net.rest

import com.riverside.skeleton.kotlin.net.jsonbean.JsonResponse
import com.riverside.skeleton.kotlin.net.rest.error.RestThrowable
import com.riverside.skeleton.kotlin.net.rest.error.SessionTimeoutThrowable
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.FlowableOnSubscribe
import io.reactivex.rxjava3.core.FlowableTransformer

object ObservableHelper2 {
    fun <T> checkResult() = FlowableTransformer<JsonResponse<T>, T> {
        it.flatMap { response ->
            Flowable.create(FlowableOnSubscribe<T> { subscriber ->
                if (response.resultflag != null) {
                    if (response.resultflag == "1") {
                        if (response.is_default_pw == "0") {
                            subscriber.onComplete()
                        } else {
                            val errorMsg = "密码错误"
                            subscriber.onError(RestThrowable(errorMsg, "1001"))
                        }
                    } else {
                        val errorMsg =
                            if (response.error_msg!!.isNotEmpty()) response.error_msg else "密码错误"
                        subscriber.onError(RestThrowable(errorMsg, response.error_code!!))
                    }
                } else {
                    if (response.error_code == "1") {
                        subscriber.onNext(response.bus_json)
                        subscriber.onComplete()
                    } else if (response.error_code == "-1" && response.error_msg == "SESSION TIMEOUT") {
                        subscriber.onError(
                            SessionTimeoutThrowable(response.error_msg, response.error_code)
                        )
                    } else {
                        subscriber.onError(
                            RestThrowable(response.error_msg!!, response.error_code!!)
                        )
                    }
                }
            }, BackpressureStrategy.DROP)   //TODO:背压策略
        }
    }
}

fun <T> Flowable<JsonResponse<T>>.checkResult(): Flowable<T> =
    this.compose(ObservableHelper2.checkResult())