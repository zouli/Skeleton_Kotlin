package com.riverside.skeleton.kotlin.net.rest

import com.riverside.skeleton.kotlin.net.jsonbean.JsonResponse
import retrofit2.http.QueryMap
import io.reactivex.Flowable
import retrofit2.http.GET

interface CommonRestService {
    @GET(ConstUrls2.LOGIN)
    fun login(): Flowable<JsonResponse<String>>

    @GET(ConstUrls2.LOGOUT)
    fun logout(): Flowable<JsonResponse<String>>

    @GET(ConstUrls2.SESSION_TIMEOUT)
    fun sessionTimeout(): Flowable<JsonResponse<String>>

    @GET(ConstUrls2.GET_LIST)
    fun getList(@QueryMap param: Map<String, String>): Flowable<JsonResponse<List<String>>>

    @GET(ConstUrls2.RETRY)
    fun retry(): Flowable<JsonResponse<String>>
}