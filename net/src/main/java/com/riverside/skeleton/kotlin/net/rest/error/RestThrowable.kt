package com.riverside.skeleton.kotlin.net.rest.error

/**
 * Rest通用错误类    1.0
 * b_e  2019/09/27
 */
open class RestThrowable : Throwable {
    var errorCode: String

    constructor(errorCode: String) {
        this.errorCode = errorCode
    }

    constructor(detailMessage: String, errorCode: String) :
            super(detailMessage) {
        this.errorCode = errorCode
    }

    constructor(detailMessage: String, cause: Throwable, errorCode: String) :
            super(detailMessage, cause) {
        this.errorCode = errorCode
    }

    constructor(cause: Throwable, errorCode: String) :
            super(cause) {
        this.errorCode = errorCode
    }
}