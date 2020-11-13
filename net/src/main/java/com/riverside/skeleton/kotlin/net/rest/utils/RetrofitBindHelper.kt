package com.riverside.skeleton.kotlin.net.rest.utils

import android.net.TrafficStats
import com.riverside.skeleton.kotlin.net.ConstUrls
import com.riverside.skeleton.kotlin.net.rest.handler.SessionHandlerFactory
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.fastjson.FastJsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * 网络连接服务类 1.0
 * b_e  2017/12/25
 */
class RetrofitBindHelper {
    private val retrofit: Retrofit
    private val httpHeader: Int by MetadataInfo("HTTP_HEADER", -1)

    init {
        retrofit = Retrofit.Builder().client(genericClient()).baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(FastJsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    /**
     * 生成通用客户端
     */
    private fun genericClient() = with(OkHttpClient().newBuilder()) {
        this.connectTimeout(ConstUrls.CONNECT_TIME_OUT.toLong(), TimeUnit.SECONDS)

        //设置Header
        this.addInterceptor { chain ->
            chain.proceed(with(chain.request().newBuilder()) {
                TrafficStats.setThreadStatsTag(1)

                //根据http_header的设置，取得Header设置
                if (httpHeader > -1) {

                    ContextHolder.applicationContext.resources.getStringArray(httpHeader)
                        .forEach { header ->
                            val items = header.split(":", limit = 2)
                            val key = items[0].trim { it <= ' ' }
                            var value: String = items[1].trim { it <= ' ' }
                            if (value.startsWith("\${") && value.endsWith("}")) {
                                value = SessionHandlerFactory.getHeaderValue(
                                    value.substring(2, value.length - 1)
                                )
                            }
                            this.addHeader(key, value)
                        }
                }

                this.build()
            })
        }

        // 设置Log的生成级别
        this.addInterceptor(HttpLoggingInterceptor().apply {
            this.level =
                if (ContextHolder.isDebug) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
        })

        // 设置Cookie的保存和读取
        this.cookieJar(object : CookieJar {
            private val cookieStore = mutableListOf<Cookie>()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                // 判断是否保存Session
                if (SessionHandlerFactory.canToSave(url.toString())) {
                    cookieStore.clear()
                    cookieStore.addAll(cookies)
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore
            }
        })

        this.build()
    }

    /**
     * 绑定Rest服务
     */
    fun <T> doBind(restService: Class<T>) = retrofit.create(restService)

    companion object {
        var baseUrl: String = ConstUrls.SERVER_ROOT
        private val mapRetrofit = ConcurrentHashMap<String, RetrofitBindHelper?>()

        /**
         * 取得当前对象
         */
        fun getInstance(): RetrofitBindHelper =
            mapRetrofit[baseUrl] ?: RetrofitBindHelper().also { mapRetrofit[baseUrl] = it }

        /**
         * 生成文件上传对象
         */
        fun getFilePart(
            name: String,
            file: File,
            listener: ProgressRequestBody.UploadCallbacks
        ): RequestBody {
            val builder = ProgressRequestBody.Builder()
            builder.addFormDataPart(name, file.name, file.absolutePath, listener)
            return builder.build()
        }
    }
}