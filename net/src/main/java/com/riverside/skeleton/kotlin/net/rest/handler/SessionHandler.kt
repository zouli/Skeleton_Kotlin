package com.riverside.skeleton.kotlin.net.rest.handler

import io.reactivex.Flowable

/**
 * Session处理接口  1.1
 * b_e  2019/09/27
 */
interface SessionHandler {
    /**
     * 判断是否可以保存当前Session
     */
    fun canToSave(url: String): Boolean

    /**
     * 重新登录
     */
    fun reLogin(): Flowable<Any>

    /**
     * 跳转到登录画面
     */
    fun toLogin(closeAll: Boolean)

    /**
     * 取得Header对应的Value
     *
     * @param key
     * @return
     */
    fun getHeaderValue(key: String): String
}