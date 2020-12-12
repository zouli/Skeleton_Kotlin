package com.riverside.skeleton.kotlin.net.rest.handler

import com.riverside.skeleton.kotlin.net.rest.error.SessionTimeoutThrowable
import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo
import io.reactivex.rxjava3.core.Flowable

/**
 * Session处理工厂类  1.0
 * b_e  2019/09/27
 */
object SessionHandlerFactory {
    val session_save_point: String by MetadataInfo("SESSION_SAVE_POINT", "")
    /**
     * 取得当前Session处理对象
     * 根据session_save_point中设置的类创建对象
     * session_save_point中设置的类需要实现SessionCheckerHandler接口
     */
    private val sessionHandler = try {
        session_save_point.takeIf { it.isNotEmpty() }?.let {
            Class.forName(it).newInstance() as SessionHandler
        }
    } catch (e: Exception) {
        throw RuntimeException("SESSION_SAVE_POINT中设置的类需要实现SessionCheckerHandler接口")
    }

    /**
     * 调用当前Session处理对象的"判断是否可以保存当前Session"方法
     */
    fun canToSave(url: String) = sessionHandler != null && sessionHandler.canToSave(url)


    /**
     * 调用当前Session处理对象的"重新登录"方法
     */
    fun reLogin(): Flowable<Any> =
        sessionHandler?.reLogin()
            ?: Flowable.error<Any>(SessionTimeoutThrowable("Session过期", "-1"))

    /**
     * 调用当前Session处理对象的"跳转到登录画面"方法
     */
    fun toLogin(closeAll: Boolean) = sessionHandler?.toLogin(closeAll)

    /**
     * 调用当前Session处理对象的"取得Header对应的Value"方法
     */
    fun getHeaderValue(key: String): String = sessionHandler?.getHeaderValue(key) ?: ""
}