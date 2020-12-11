package com.riverside.skeleton.kotlin.util.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.riverside.skeleton.kotlin.base.R
import java.text.NumberFormat

/**
 * 最像ProgressDialog的ProgressDialog  1.0
 *
 * b_e  2020/12/11
 */
class ProgressDialog : AlertDialog {
    constructor(context: Context) : this(context, 0)

    constructor(context: Context, themeResId: Int) : super(context, themeResId)

    private var mHasStarted = false

    private lateinit var progressBar: ProgressBar
    private lateinit var progressNumber: TextView
    private lateinit var progressPercent: TextView
    private lateinit var messageView: TextView
    private lateinit var viewUpdateHandler: Handler

    private val hasProgressBar get() = ::progressBar.isInitialized

    var progressNumberFormat = "%1d/%2d"
        set(value) {
            field = value
            onProgressChanged()
        }

    var progressPercentFormat: NumberFormat = NumberFormat.getPercentInstance().apply {
        maximumFractionDigits = 0
    }
        set(value) {
            field = value
            onProgressChanged()
        }

    var progressStyle = STYLE.SPINNER

    var progress = 0
        get() = if (hasProgressBar) progressBar.progress else field
        set(value) = (if (mHasStarted) {
            progressBar.progress = value
            onProgressChanged()
        } else field = value)

    var secondaryProgress = 0
        get() = if (hasProgressBar) progressBar.secondaryProgress else field
        set(value) = (if (hasProgressBar) {
            progressBar.secondaryProgress = value
            onProgressChanged()
        } else field = value)

    var max = 100
        get() = if (hasProgressBar) progressBar.max else field
        set(value) = if (hasProgressBar) {
            progressBar.max = value
            onProgressChanged()
        } else field = value

    var incrementBy = 0
        set(value) = if (hasProgressBar) {
            progressBar.incrementProgressBy(value)
            onProgressChanged()
        } else field += value

    var incrementSecondaryBy = 0
        set(value) = if (hasProgressBar) {
            progressBar.incrementSecondaryProgressBy(value)
            onProgressChanged()
        } else field += value

    var progressDrawable: Drawable? = null
        set(value) = if (hasProgressBar) progressBar.progressDrawable = value else field = value

    var indeterminateDrawable: Drawable? = null
        set(value) =
            if (hasProgressBar) progressBar.indeterminateDrawable = value else field = value

    var isIndeterminate = false
        get() = if (hasProgressBar) progressBar.isIndeterminate else field
        set(value) = if (hasProgressBar) progressBar.isIndeterminate = value else field = value

    private var message: CharSequence = ""

    @SuppressLint("HandlerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        val inflater = LayoutInflater.from(context)
        val attrs = context.obtainStyledAttributes(
            null, R.styleable.ProgressDialog, R.attr.alertDialogStyle, 0
        )

        if (progressStyle == STYLE.SPINNER) {
            setView(inflater.inflate(
                attrs.getResourceId(
                    R.styleable.ProgressDialog_progressLayout,
                    R.layout.progress_dialog
                ), null
            ).apply {
                progressBar = findViewById(R.id.progress)
                messageView = findViewById(R.id.message)
            })
        } else {
            viewUpdateHandler = object : Handler() {
                override fun handleMessage(msg: Message?) {
                    super.handleMessage(msg)

                    val progress = progressBar.progress
                    val max = progressBar.max
                    val percent = progress.toDouble() / max.toDouble()
                    progressNumber.text = progressNumberFormat.format(progress, max)
                    progressPercent.text =
                        SpannableString(progressPercentFormat.format(percent)).apply {
                            setSpan(
                                StyleSpan(Typeface.BOLD),
                                0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                }
            }
            setView(inflater.inflate(
                attrs.getResourceId(
                    R.styleable.ProgressDialog_horizontalProgressLayout,
                    R.layout.alert_dialog_progress
                ), null
            ).apply {
                progressBar = findViewById(R.id.progress)
                progressNumber = findViewById(R.id.progress_number)
                progressPercent = findViewById(R.id.progress_percent)
            })
        }
        attrs.recycle()
        if (max > 0) max = max
        if (progress > 0) progress = progress
        if (secondaryProgress > 0) secondaryProgress = secondaryProgress
        if (incrementBy > 0) incrementBy = incrementBy
        if (incrementSecondaryBy > 0) incrementSecondaryBy = incrementSecondaryBy
        if (progressDrawable != null) progressDrawable = progressDrawable
        if (indeterminateDrawable != null) indeterminateDrawable = indeterminateDrawable
        if (message.isNotEmpty()) setMessage(message)
        isIndeterminate = isIndeterminate
        onProgressChanged()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mHasStarted = true
    }

    override fun onStop() {
        super.onStop()
        mHasStarted = false
    }

    fun setMessage(messageId: Int) = setMessage(context.getText(messageId))

    override fun setMessage(message: CharSequence) = if (hasProgressBar)
        if (progressStyle == STYLE.HORIZONTAL)
            super.setMessage(message)
        else
            messageView.text = message
    else
        this.message = message

    private fun onProgressChanged() {
        if (progressStyle == STYLE.HORIZONTAL)
            if (::viewUpdateHandler.isInitialized && !viewUpdateHandler.hasMessages(0))
                viewUpdateHandler.sendEmptyMessage(0)
    }

    companion object {
        fun show(context: Context, title: CharSequence, message: CharSequence): ProgressDialog =
            show(context, title, message, false)

        fun show(
            context: Context, title: CharSequence, message: CharSequence, indeterminate: Boolean
        ): ProgressDialog = show(context, title, message, indeterminate, false)

        fun show(
            context: Context,
            title: CharSequence, message: CharSequence, indeterminate: Boolean,
            cancelable: Boolean
        ): ProgressDialog = show(context, title, message, indeterminate, cancelable, null)

        fun show(
            context: Context,
            title: CharSequence, message: CharSequence, indeterminate: Boolean,
            cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?
        ): ProgressDialog = ProgressDialog(context).apply {
            setTitle(title)
            setMessage(message)
            isIndeterminate = indeterminate
            setCancelable(cancelable)
            setOnCancelListener(cancelListener)
            show()
        }
    }

    enum class STYLE {
        SPINNER, HORIZONTAL
    }
}