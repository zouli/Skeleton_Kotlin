package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 可Check的LinearLayout  1.0
 * b_e      2020/11/15
 */
open class CheckableLinearLayout(context: Context, attrs: AttributeSet?) :
    LinearLayout(context, attrs), Checkable {

    constructor(context: Context) : this(context, null)

    @Attr(AttrType.BOOLEAN)
    private val mCheckedChild: Boolean by AttributeSetInfo(
        attrs, R.styleable.CheckableLinearLayout,
        R.styleable.CheckableLinearLayout_cll_checkedChild, false
    )

    var checkedChild = mCheckedChild
        set(value) {
            field = value
            setChildChecked(mChecked and value)
        }

    private var mChecked = false

    override fun setChecked(checked: Boolean) {
        // 只有变化时调用
        if (checked != mChecked) {
            if (checkedChild)
                setChildChecked(checked)

            // 保存Check状态
            mChecked = checked
            refreshDrawableState()
        }
    }

    private fun setChildChecked(checked: Boolean) =
        (0 until childCount).forEach { i ->
            with(getChildAt(i)) {
                if (this is Checkable) this.isChecked = checked
            }
        }

    override fun isChecked(): Boolean = mChecked

    override fun toggle() {
        // 更新Check状态
        isChecked = !mChecked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray =
        super.onCreateDrawableState(extraSpace + 1).apply {
            // 更新控件状态
            if (isChecked) View.mergeDrawableStates(this, CHECKED_STATE_SET)
        }

    override fun drawableStateChanged() {
        super.drawableStateChanged()

        // 更新控件状态
        background?.let {
            it.state = drawableState
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable? =
        // 保存Check状态
        SavedState(super.onSaveInstanceState()).apply {
            checked = isChecked
        }

    @Override
    override fun onRestoreInstanceState(state: Parcelable?) = with(state as SavedState) {
        // 回复Check状态
        super.onRestoreInstanceState(this)
        isChecked = this.checked
        requestLayout()
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_selected)
    }

    /**
     * 状态保存类
     */
    private class SavedState : BaseSavedState {
        var checked = false

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            checked = parcel.readValue(null) as Boolean
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(checked)
        }

        override fun toString(): String {
            return "${CheckableLinearLayout::class.qualifiedName}.SavedState{${
            Integer.toHexString(System.identityHashCode(this))} checked=${checked}}"
        }
    }
}