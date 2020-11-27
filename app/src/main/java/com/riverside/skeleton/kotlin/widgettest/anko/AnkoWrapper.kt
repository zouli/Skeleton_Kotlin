package com.riverside.skeleton.kotlin.widgettest.anko

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import com.riverside.skeleton.kotlin.widget.captcha.BoxCaptchaView
import com.riverside.skeleton.kotlin.widget.captcha.InputCaptchaView
import com.riverside.skeleton.kotlin.widget.containers.*
import org.jetbrains.anko.custom.ankoView

inline fun ViewManager.inputCaptchaView(init: InputCaptchaView.() -> Unit): InputCaptchaView =
    ankoView({ InputCaptchaView(it) }, theme = 0, init = init)

fun InputCaptchaView.onClick(handler: () -> Boolean) = setOnResultListener {
    handler()
}

var InputCaptchaView.sleepSecond: Int get() = 0; set(value) = setSleepSecond(value)

var InputCaptchaView.maxLength: Int get() = 0; set(value) = setMaxLength(value)

var InputCaptchaView.textHint: String get() = ""; set(value) = setTextHint(value)


inline fun ViewManager.boxCaptchaView(init: BoxCaptchaView.() -> Unit): BoxCaptchaView =
    ankoView({ BoxCaptchaView(it) }, theme = 0, init = init)

fun BoxCaptchaView.onInputChanged(handler: (text: String) -> Unit) = setOnInputChangedListener {
    handler(it)
}

var BoxCaptchaView.charNumber: Int get() = 0; set(value) = setCharNumber(value)

var BoxCaptchaView.divideWidth: Int get() = 0; set(value) = setDivideWidth(value)

var BoxCaptchaView.itemStyle: Int get() = 0; set(value) = setItemStyle(value)


open class _CheckableLinearLayout(ctx: Context) : CheckableLinearLayout(ctx) {

    inline fun <T : View> T.lparams(
        c: Context?,
        attrs: AttributeSet?,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(c!!, attrs!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        c: Context?,
        attrs: AttributeSet?
    ): T {
        val layoutParams = LinearLayout.LayoutParams(c!!, attrs!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT
    ): T {
        val layoutParams = LinearLayout.LayoutParams(width, height)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        weight: Float,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(width, height, weight)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        width: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        height: Int = android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
        weight: Float
    ): T {
        val layoutParams = LinearLayout.LayoutParams(width, height, weight)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        p: ViewGroup.LayoutParams?,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(p!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        p: ViewGroup.LayoutParams?
    ): T {
        val layoutParams = LinearLayout.LayoutParams(p!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        source: ViewGroup.MarginLayoutParams?,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    inline fun <T : View> T.lparams(
        source: ViewGroup.MarginLayoutParams?
    ): T {
        val layoutParams = LinearLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    inline fun <T : View> T.lparams(
        source: LinearLayout.LayoutParams?,
        init: LinearLayout.LayoutParams.() -> Unit
    ): T {
        val layoutParams = LinearLayout.LayoutParams(source!!)
        layoutParams.init()
        this@lparams.layoutParams = layoutParams
        return this
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    inline fun <T : View> T.lparams(
        source: LinearLayout.LayoutParams?
    ): T {
        val layoutParams = LinearLayout.LayoutParams(source!!)
        this@lparams.layoutParams = layoutParams
        return this
    }
}

inline fun Context.checkableLinearLayout(init: _CheckableLinearLayout.() -> Unit): CheckableLinearLayout {
    return ankoView({ ctx: Context -> _CheckableLinearLayout(ctx) }, theme = 0) { init() }
}

inline fun ViewManager.recyclerView(init: RecyclerView.() -> Unit): RecyclerView {
    return ankoView({ RecyclerView(ContextHolder.applicationContext, null) }, 0, init)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
inline fun ViewManager.refreshListView(init: RefreshListView.() -> Unit): RefreshListView {
    return ankoView({ RefreshListView(ContextHolder.applicationContext, null) }, 0, init)
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
inline fun ViewManager.refreshGridView(init: RefreshGridView.() -> Unit): RefreshGridView {
    return ankoView({ RefreshGridView(ContextHolder.applicationContext, null) }, 0, init)
}

inline fun ViewManager.completeListView(init: CompleteListView.() -> Unit): CompleteListView {
    return ankoView({ CompleteListView(ContextHolder.applicationContext, null) }, 0, init)
}

inline fun ViewManager.completeGridView(init: CompleteGridView.() -> Unit): CompleteGridView {
    return ankoView({ CompleteGridView(ContextHolder.applicationContext, null) }, 0, init)
}

inline fun ViewManager.imageGridView(init: ImageGridView.() -> Unit): ImageGridView {
    return ankoView({ ImageGridView(ContextHolder.applicationContext, null) }, 0, init)
}