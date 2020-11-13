package com.riverside.skeleton.kotlin.base.utils.collectinfo

import android.app.Application
import com.alibaba.fastjson.JSONObject
import kotlin.reflect.KClass

/**
 * 收集信息帮助类  1.1
 * b_e  2019/06/20
 */
object CollectInfoHelper {
    lateinit var application: Application
    @Volatile var infoSourceClassList: MutableList<KClass<out InfoSource>> = ArrayList()

    /**
     * 收集信息
     */
    val collectInfo: String
        get() = JSONObject().also { json ->
            for (infoSource in infoSourceClassList)
                json[infoSource.simpleName] = infoSource.getInfo(application)
        }.toJSONString()
}