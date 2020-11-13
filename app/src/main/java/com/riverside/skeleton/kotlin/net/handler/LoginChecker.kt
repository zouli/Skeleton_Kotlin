package com.riverside.skeleton.kotlin.net.handler

import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.ConstUrls2
import com.riverside.skeleton.kotlin.net.rest.ObservableHelper2
import com.riverside.skeleton.kotlin.net.rest.handler.SessionHandler
import com.riverside.skeleton.kotlin.net.rest.utils.ObservableHelper
import com.riverside.skeleton.kotlin.net.rest.utils.RetrofitBindHelper
import com.riverside.skeleton.kotlin.util.converter.DateUtils
import com.riverside.skeleton.kotlin.util.converter.toString
import io.reactivex.Flowable
import java.util.*

class LoginChecker : SessionHandler {
    override fun canToSave(url: String): Boolean = ConstUrls2.LOGIN in url

    override fun reLogin(): Flowable<Any> =
        RetrofitBindHelper.getInstance().doBind(CommonRestService::class.java).login()
            .compose(ObservableHelper.startReading())
            .compose(ObservableHelper2.checkResult())

    override fun toLogin(closeAll: Boolean) = ActivityStackManager.toLoginActivity(closeAll)

    override fun getHeaderValue(key: String): String =
        Date().toString(DateUtils.DATE_FORMAT_PATTERN2)
}