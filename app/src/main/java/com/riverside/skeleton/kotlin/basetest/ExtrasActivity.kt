package com.riverside.skeleton.kotlin.basetest

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.base.service.SBaseServiceConnection
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.Extra
import com.riverside.skeleton.kotlin.util.extras.bindingService
import com.riverside.skeleton.kotlin.util.extras.finishResultOK
import com.riverside.skeleton.kotlin.util.extras.startActivity
import com.riverside.skeleton.kotlin.util.extras.startService
import com.riverside.skeleton.kotlin.util.extras.stopService
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.widget.selector.CommonSelectorOptions
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ExtrasActivity : SBaseActivity() {
    private val testInt: Int by Extra()
    private val testLong: Int by Extra()
    private val testCharSequence: CharSequence by Extra()
    private val testString: String by Extra()
    private val testFloat: Float by Extra()
    private val testDouble: Double by Extra()
    private val testChar: Char by Extra()
    private val testShort: Short by Extra()
    private val testBoolean: Boolean by Extra()
    private val testListString: List<String> by Extra()
    private val testListInt: List<Int> by Extra()
    private val testObject: CommonSelectorOptions by Extra()
    private val testIntArray: IntArray by Extra()
    private val testLongArray: LongArray by Extra()
    private val testFloatArray: FloatArray by Extra()
    private val testDoubleArray: DoubleArray by Extra()
    private val testCharArray: CharArray by Extra()
    private val testShortArray: ShortArray by Extra()
    private val testBooleanArray: BooleanArray by Extra()
    private val testArrayInt: Array<Int> by Extra()
    private val testArrayLong: Array<Long> by Extra()
    private val testArrayFloat: Array<Float> by Extra()
    private val testArrayDouble: Array<Double> by Extra()
    private val testArrayChar: Array<Char> by Extra()
    private val testArrayShort: Array<Short> by Extra()
    private val testArrayBoolean: Array<Boolean> by Extra()

    private val flag: Int by Extra()
    var i = 1
    private var conn = SBaseServiceConnection<TestService>()

    override fun initView() {
        title = "Extras"

        SLog.w("testInt=$testInt,testLong=$testLong,testCharSequence=$testCharSequence,testString=$testString,testFloat=$testFloat,testDouble=$testDouble,testChar=$testChar,testShort=$testShort,testBoolean=$testBoolean,testListString=$testListString,testListInt=$testListInt,testObject=$testObject")
        SLog.w("testIntArray=$testIntArray,testLongArray=$testLongArray,testFloatArray=$testFloatArray,testDoubleArray=$testDoubleArray,testCharArray=$testCharArray,testShortArray=$testShortArray,testBooleanArray=$testBooleanArray")
        SLog.w("testArrayInt=$testArrayInt,testArrayLong=$testArrayLong,testArrayFloat=$testArrayFloat,testArrayDouble=$testArrayDouble,testArrayChar=$testArrayChar,testArrayShort=$testArrayShort,testArrayBoolean=$testArrayBoolean")

        scrollView {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            verticalLayout {
                lparams(matchParent, wrapContent)

                textView {
                    text = when (flag) {
                        1 -> "from startActivity"
                        2 -> "from startActivityForResult"
                        else -> ""
                    }
                }.lparams(matchParent, wrapContent)

                button("start activity") {
                    onClick {
                        startActivity<ExtrasActivity>("flag" to 1)
                    }
                }.lparams(matchParent, wrapContent)

                button("start activity for result") {
                    onClick {
                        startActivityForResult<ExtrasActivity>("flag" to 2) { resultCode, intent ->
                            if (resultCode == Activity.RESULT_OK) {
                                intent?.getStringExtra("value")
                                    ?.let { "Activity $it".toast(activity) }
                            }
                        }
                    }
                }.lparams(matchParent, wrapContent)

                button("RESULT OK") {
                    visibility = if (flag == 2) View.VISIBLE else View.GONE
                    textColor = "#FF0000".toColorInt()
                    onClick {
                        finishResultOK("value" to "OK")
                    }
                }.lparams(matchParent, wrapContent)

                button("start service") {
                    onClick {
                        startService<TestService>("flag" to i++)
                    }
                }.lparams(matchParent, wrapContent)

                button("stop service") {
                    onClick {
                        stopService<TestService>("flag" to i++)
                    }
                }.lparams(matchParent, wrapContent)


                button("bind service") {
                    onClick {
                        conn = bindingService<TestService>(
                            Context.BIND_AUTO_CREATE,
                            "flag" to i++,
                            onConnected = { name, service ->
                                SLog.w(service.getIndex())
                            },
                            onDisconnected = { name -> })
                    }
                }.lparams(matchParent, wrapContent)

                button("unbind service") {
                    onClick {
                        unbindService(conn)
                    }
                }.lparams(matchParent, wrapContent)

                verticalLayout {
                    id = F_ID
                }.lparams(matchParent, wrapContent)
            }
        }

        addFragment(F_ID, ExtrasFragment())
    }

    companion object {
        const val F_ID = 1
    }
}