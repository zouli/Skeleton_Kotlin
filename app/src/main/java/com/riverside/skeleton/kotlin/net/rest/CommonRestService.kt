package com.riverside.skeleton.kotlin.net.rest

import com.riverside.skeleton.kotlin.net.jsonbean.JsonResponse
import io.reactivex.rxjava3.core.Flowable
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.QueryMap
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

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

    @GET
    fun getCaptchaImage(@Url url: String, @Query("t") t: String): Call<ResponseBody>
}