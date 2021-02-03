package com.riverside.skeleton.kotlin.widgettest.xml

import android.graphics.BitmapFactory
import com.github.yoojia.inputs.AndroidNextInputs
import com.github.yoojia.inputs.StaticScheme
import com.github.yoojia.inputs.ValueScheme
import com.github.yoojia.inputs.WidgetAccess
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.kotlinnextinputs.*
import com.riverside.skeleton.kotlin.net.rest.CommonRestService
import com.riverside.skeleton.kotlin.net.rest.utils.retrofit
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs.findBoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.kotlinnextinputs.findInputCaptchaView
import kotlinx.android.synthetic.main.activity_captcha_xml.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class CaptchaActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_captcha_xml

    override fun initView() {
        title = "Captcha"

        icv.setOnSendListener {
            icvValidate().apply { }
        }

        bcv.setCharNumber(5)
        bcv.setOnInputChangedListener {
            it.toast(this@CaptchaActivity)
        }

        btn_val.setOnClickListener {
            validate()
        }

        retrofit<CommonRestService>().getCaptchaImage(
            "http://zyln.org/zyz/code.do", System.currentTimeMillis().toString()
        ).enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ResponseBody>, response: Response<ResponseBody>
            ) {
                response.headers()["Set-Cookie"]
                response.body()?.byteStream().let {
                    val bmp = BitmapFactory.decodeStream(it) //读取图像数据
                    icv1.setCaptchaImage(bmp)
                }
            }
        })
//        Thread {
//            try {
//                val netUrl =
//                    URL("http://zyln.org/zyz/code.do?t=" + System.currentTimeMillis())
//                val conn: HttpURLConnection = netUrl.openConnection() as HttpURLConnection
//                conn.requestMethod = "GET"
//                conn.connectTimeout = 1000
//                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
//                    val map: Map<String, List<String>> = conn.headerFields
////                            map["Set-Cookie"]?.let { block(it) }
//                    val inputStream = conn.inputStream
//                    val bmp = BitmapFactory.decodeStream(inputStream) //读取图像数据
//                    icv1.setCaptchaImage(bmp)
//
//                    inputStream.close()
//                }
//                conn.disconnect()
//            } catch (e: IOException) {
//            }
//        }.start()
    }

    private fun icvValidate() = nextInput {
        messageDisplay = AndroidToastMessageDisplay()

        add(et_phone.nextInput, StaticScheme.Required().msg("请输入手机号码"))
    }.test()

    override fun doValidate(): Boolean {
        val toastMessageDisplay = AndroidToastMessageDisplay()
        val access = WidgetAccess(this)
        val inputs = AndroidNextInputs()

        inputs.messageDisplay = toastMessageDisplay

        inputs.add(access.findInputCaptchaView(R.id.icv), StaticScheme.Required().msg("请输入验证码"))
        inputs.add(access.findBoxCaptchaView(R.id.bcv), StaticScheme.Required().msg("请输入Box验证码"))
        inputs.add(
            access.findInputCaptchaView(R.id.icv),
            ValueScheme.EqualsTo("1111").msg("验证码输入错误")
        )
        inputs.add(
            access.findBoxCaptchaView(R.id.bcv),
            ValueScheme.EqualsTo("22222").msg("Box验证码输入错误")
        )

        return inputs.test()
    }
}