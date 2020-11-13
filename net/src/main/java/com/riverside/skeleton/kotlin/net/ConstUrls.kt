package com.riverside.skeleton.kotlin.net

import com.riverside.skeleton.kotlin.util.packageinfo.MetadataInfo

class ConstUrls {
    companion object {
        // 链接超时时间
        val CONNECT_TIME_OUT: Int by MetadataInfo("CONNECT_TIME_OUT", 15)
        // 服务器地址
        val SERVER_ROOT: String by MetadataInfo("SERVER_HOST", "")
        // 链接重试次数
        val CONNECT_RETRY_COUNT: Int by MetadataInfo("CONNECT_RETRY_COUNT", 3)
        // 链接重试间隔
        val CONNECT_RETRY_DELAY: Int by MetadataInfo("CONNECT_RETRY_DELAY", 3000)
    }
}