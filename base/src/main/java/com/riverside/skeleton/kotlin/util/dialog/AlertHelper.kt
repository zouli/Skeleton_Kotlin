package com.riverside.skeleton.kotlin.util.dialog

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ArrayAdapter
import androidx.appcompat.widget.DialogTitle
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.R
import com.riverside.skeleton.kotlin.base.activity.ActivityStackManager

fun Context.alert(themeResId: Int = 0, init: AlertBuilder.() -> Unit): AlertBuilder =
    AlertBuilder(this, themeResId).apply { init() }

fun Fragment.alert(themeResId: Int = 0, init: AlertBuilder.() -> Unit): AlertBuilder? =
    activity?.alert(themeResId, init)

fun Service.alert(themeResId: Int = 0, init: AlertBuilder.() -> Unit): AlertBuilder? =
    ActivityStackManager.currentActivity?.alert(themeResId, init)

fun Context.alert(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: AlertBuilder.() -> Unit
): AlertBuilder = AlertBuilder(this, themeResId).apply {
    this.message = message
    title?.let { this.title = title }
    init()
}

fun Fragment.alert(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: AlertBuilder.() -> Unit
): AlertBuilder? = activity?.let {
    AlertBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

fun Service.alert(
    message: CharSequence, title: CharSequence? = null, themeResId: Int = 0,
    init: AlertBuilder.() -> Unit
): AlertBuilder? = ActivityStackManager.currentActivity?.let {
    AlertBuilder(it, themeResId).apply {
        this.message = message
        title?.let { this.title = title }
        init()
    }
}

fun Context.alert(
    messageId: Int, titleId: Int? = null, themeResId: Int = 0,
    init: AlertBuilder.() -> Unit
): AlertBuilder = AlertBuilder(this, themeResId).apply {
    this.messageId = messageId
    titleId?.let { this.titleId = titleId }
    init()
}

/**
 * AlertDialogBuilder的Kotlin封装类
 */
open class AlertBuilder(val context: Context, themeResId: Int) {
    private val builder = AlertDialog.Builder(context, themeResId)

    private var result = Result.NO
    private lateinit var dialog: AlertDialog
    private lateinit var handler: Handler
    private var windowSetting: Window.() -> Unit = {}
    private var maxHeight: Int = 0

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

    var customView: View
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setView(value)
        }

    var isCancelable: Boolean
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            builder.setCancelable(value)
        }

    fun <T> items(
        data: List<T>, isCenterAble: Boolean = false,
        block: (dialog: DialogInterface, item: T, index: Int) -> Unit
    ) {
        val array = Array(data.size) { index -> data[index].toString() }
        if (isCenterAble) {
            builder.setAdapter(
                ArrayAdapter<CharSequence>(
                    context, R.layout.select_dialog_item_center, android.R.id.text1, array
                )
            ) { dialog, which ->
                block(dialog, data[which], which)
            }
        } else
            builder.setItems(array) { dialog, which ->
                block(dialog, data[which], which)
            }
    }

    fun <T> singleChoice(
        data: List<T>, checkedItem: Int,
        block: (dialog: DialogInterface, item: T, index: Int) -> Unit
    ) {
        builder.setSingleChoiceItems(
            Array(data.size) { index -> data[index].toString() }, checkedItem
        ) { dialog, which ->
            block(dialog, data[which], which)
        }
    }

    fun <T> multiChoice(
        data: List<T>, checkedItems: Array<Boolean>,
        block: (dialog: DialogInterface, item: T, index: Int, isChecked: Boolean) -> Unit
    ) {
        builder.setMultiChoiceItems(
            Array(data.size) { index -> data[index].toString() }, checkedItems.toBooleanArray()
        ) { dialog, which, isChecked ->
            block(dialog, data[which], which, isChecked)
        }
    }

    fun onCancelled(block: (dialog: DialogInterface) -> Unit) {
        builder.setOnCancelListener(block)
        setResult(Result.NO)
    }

    fun onKeyPressed(block: (dialog: DialogInterface, keyCode: Int, event: KeyEvent) -> Boolean) {
        builder.setOnKeyListener { dialog, keyCode, event ->
            block(dialog, keyCode, event)
        }
    }

    fun negativeButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setNegativeButton(text) { dialog, which ->
            block(dialog, which)
            setResult(Result.NO)
        }
    }

    fun negativeButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setNegativeButton(resId) { dialog, which ->
            block(dialog, which)
            setResult(Result.NO)
        }
    }

    fun positiveButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setPositiveButton(text) { dialog, which ->
            block(dialog, which)
            setResult(Result.YES)
        }
    }

    fun positiveButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setPositiveButton(resId) { dialog, which ->
            block(dialog, which)
            setResult(Result.YES)
        }
    }

    fun neutralButton(text: CharSequence, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setNeutralButton(text) { dialog, which ->
            block(dialog, which)
            setResult(Result.NEUTRAL)
        }
    }

    fun neutralButton(resId: Int, block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setNeutralButton(resId) { dialog, which ->
            block(dialog, which)
            setResult(Result.NEUTRAL)
        }
    }

    fun okButton(block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            block(dialog, which)
            setResult(Result.YES)
        }
    }

    fun cancelButton(block: (dialog: DialogInterface, which: Int) -> Unit) {
        builder.setNegativeButton(android.R.string.cancel) { dialog, which ->
            block(dialog, which)
            setResult(Result.NO)
        }
    }

    fun build(): AlertDialog = builder.create()

    fun show(): AlertDialog = builder.show().apply {
        window?.windowSetting().also {
            this.listView?.apply {
                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    if (this.height > maxHeight)
                        this.layoutParams.apply { height = maxHeight }
                }
            }
        }
    }

    fun setWindow(block: Window.() -> Unit) {
        windowSetting = block
    }

    fun showAtBottom(maxHeight: Int = Int.MAX_VALUE, backgroundColor: Int = Color.WHITE) {
        this.maxHeight = maxHeight
        setWindow {
            attributes?.apply {
                gravity = Gravity.BOTTOM
                width = WindowManager.LayoutParams.MATCH_PARENT
                setBackgroundDrawable(ColorDrawable(backgroundColor))
            }
        }
    }

    private fun setResult(result: Result) {
        this.result = result
        if (::handler.isInitialized) handler.sendEmptyMessage(0)
    }

    fun showResult(): Result {
        isCancelable = false
        dialog = builder.show()
        try {
            Looper.getMainLooper()
            handler = Handler {
                throw java.lang.RuntimeException()
            }
            Looper.loop()
        } catch (e: Exception) {
        }
        dialog.dismiss()
        return result
    }

    var centerTitle: CharSequence
        @Deprecated(GETTER, level = DeprecationLevel.ERROR) get() = getter()
        set(value) {
            customTitle =
                LayoutInflater.from(context).inflate(R.layout.alert_dialog_title_center, null)
                    .apply { this.findViewById<DialogTitle>(R.id.alertTitle).text = value }
        }

    enum class Result {
        YES, NO, NEUTRAL
    }

    companion object {
        private const val GETTER = "属性为只写入"
        private fun getter(): Nothing = throw RuntimeException(GETTER)
    }
}