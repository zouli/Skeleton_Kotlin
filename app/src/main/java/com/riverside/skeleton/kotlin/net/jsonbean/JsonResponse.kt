package com.riverside.skeleton.kotlin.net.jsonbean

data class JsonResponse<T>(
    val error_code: String?,
    val is_default_pw: String?,
    val resultflag: String?,
    val error_msg: String?,
    val bus_json: T
)