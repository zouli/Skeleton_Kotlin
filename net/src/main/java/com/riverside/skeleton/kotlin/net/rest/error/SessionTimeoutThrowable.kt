package com.riverside.skeleton.kotlin.net.rest.error

/**
 * Session过期错误类 1.0
 * b_e  2019/09/27
 */
class SessionTimeoutThrowable(detailMessage: String, errorCode: String) :
    RestThrowable(detailMessage, errorCode)