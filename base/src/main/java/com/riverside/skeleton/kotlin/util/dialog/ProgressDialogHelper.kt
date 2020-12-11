package com.riverside.skeleton.kotlin.util.dialog

import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.R
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager
import java.text.NumberFormat

fun Context.progressDialog(themeResId: Int = 0, init: ProgressDialogBuilder.() -> Unit):
        ProgressDialogBuilder = ProgressDialogBuilder(this, themeResId).apply { init() }

fun Fragment.progressDialog(themeResId: Int = 0, init: ProgressDialogBuilder.() -> Unit):
        ProgressDialogBuilder? = activity?.progressDialog(themeResId, init)

fun Service.progressDialog(themeResId: Int = 0, init: ProgressDialogBuilder.() -> Unit):
        ProgressDialogBuilder? =
    ActivityStackManager.currentActivity?.progressDialog(themeResId, init)

fun Context.progressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder = ProgressDialogBuilder(this, themeResId).apply {
    this.message = message
    title?.let { this.title = title }
    init()
}

fun Fragment.progressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? = activity?.let {
    ProgressDialogBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

fun Service.progressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? = ActivityStackManager.currentActivity?.let {
    ProgressDialogBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

fun Context.hProgressDialog(
    themeResId: Int = R.style.ProgressDialog, init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder = ProgressDialogBuilder(this, themeResId).apply { init() }

fun Fragment.hProgressDialog(
    themeResId: Int = R.style.ProgressDialog, init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? = activity?.hProgressDialog(themeResId, init)

fun Service.hProgressDialog(
    themeResId: Int = R.style.ProgressDialog, init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? =
    ActivityStackManager.currentActivity?.hProgressDialog(themeResId, init)

fun Context.hProgressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = R.style.ProgressDialog,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder = ProgressDialogBuilder(this, themeResId).apply {
    this.message = message
    title?.let { this.title = title }
    init()
}

fun Fragment.hProgressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = R.style.ProgressDialog,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? = activity?.let {
    ProgressDialogBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

fun Service.hProgressDialog(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = R.style.ProgressDialog,
    init: ProgressDialogBuilder.() -> Unit
): ProgressDialogBuilder? = ActivityStackManager.currentActivity?.let {
    ProgressDialogBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

/**
 * ProgressDialogBuilder的Kotlin封装类
 */
class ProgressDialogBuilder(val context: Context, themeResId: Int) {
    private val builder = ProgressDialog(context, themeResId)

    var title: CharSequence
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setTitle(value)
        }

    var titleId: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setTitle(value)
        }

    var message: CharSequence
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setMessage(value)
        }

    var messageId: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setMessage(value)
        }

    var icon: Drawable
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setIcon(value)
        }

    var iconId: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setIcon(value)
        }

    var customTitle: View
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setCustomTitle(value)
        }

    var isCancelable: Boolean
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setCancelable(value)
        }

    var progressNumberFormat: String
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.progressNumberFormat = value
        }

    var progressPercentFormat: NumberFormat
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.progressPercentFormat = value
        }

    var progressStyle: ProgressDialog.STYLE
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.progressStyle = value
        }

    var progress: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.progress = value
        }

    var secondaryProgress: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.secondaryProgress = value
        }

    var max: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.max = value
        }

    var incrementBy: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.incrementBy = value
        }

    var incrementSecondaryBy: Int
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.incrementSecondaryBy = value
        }

    var progressDrawable: Drawable?
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.progressDrawable = value
        }

    var indeterminateDrawable: Drawable?
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.indeterminateDrawable = value
        }

    var isIndeterminate: Boolean
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.isIndeterminate = value
        }

    var isCanceledOnTouchOutside: Boolean
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setCanceledOnTouchOutside(value)
        }

    fun onCancelled(block: (dialog: DialogInterface) -> Unit) {
        builder.setOnCancelListener(block)
    }

    fun onKeyPressed(block: (dialog: DialogInterface, keyCode: Int, event: KeyEvent) -> Boolean) {
        builder.setOnKeyListener { dialog, keyCode, event ->
            block(dialog, keyCode, event)
        }
    }

    fun negativeButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setButton(DialogInterface.BUTTON_NEGATIVE, text) { dialog, which ->
            block(dialog, which)
        }
    }

    fun negativeButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        negativeButton(context.getText(resId), block)
    }

    fun positiveButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setButton(DialogInterface.BUTTON_POSITIVE, text) { dialog, which ->
            block(dialog, which)
        }
    }

    fun positiveButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        positiveButton(context.getText(resId), block)
    }

    fun neutralButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setButton(DialogInterface.BUTTON_NEUTRAL, text) { dialog, which ->
            block(dialog, which)
        }
    }

    fun neutralButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        neutralButton(context.getText(resId), block)
    }

    fun okButton(block: (dialog: DialogInterface, which: Int) -> Unit) {
        positiveButton(android.R.string.ok, block)
    }

    fun cancelButton(block: (dialog: DialogInterface, which: Int) -> Unit) {
        negativeButton(android.R.string.cancel, block)
    }

    fun show(): ProgressDialog = builder.apply { show() }

    companion object {
        private const val GETTER = "属性为只写入"
        private fun getter(): Nothing = throw RuntimeException(GETTER)
    }
}
