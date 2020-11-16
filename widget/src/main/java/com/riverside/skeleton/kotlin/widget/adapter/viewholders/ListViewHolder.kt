package com.riverside.skeleton.kotlin.widget.adapter.viewholders

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.*

/**
 * 适用于AbsListView的ViewHolder
 *
 * b_e      2020/11/16
 */
class ListViewHolder private constructor(itemView: View) {
    val mHolderImpl: ViewHolderImpl = ViewHolderImpl(itemView)

    inline fun <reified T : View> findViewById(viewId: Int): T {
        return mHolderImpl.findViewById(viewId)
    }

    val context: Context
        get() = mHolderImpl.mItemView.context

    val itemView: View
        get() = mHolderImpl.mItemView

    fun setText(viewId: Int, stringId: Int): ListViewHolder {
        mHolderImpl.setText(viewId, stringId)
        return this
    }

    fun setText(viewId: Int, text: String): ListViewHolder {
        mHolderImpl.setText(viewId, text)
        return this
    }

    fun setTextColor(viewId: Int, color: Int): ListViewHolder {
        mHolderImpl.setTextColor(viewId, color)
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): ListViewHolder {
        mHolderImpl.setBackgroundColor(viewId, color)
        return this
    }

    fun setBackgroundResource(viewId: Int, resId: Int): ListViewHolder {
        mHolderImpl.setBackgroundResource(viewId, resId)
        return this
    }

    fun setBackgroundDrawable(viewId: Int, drawable: Drawable): ListViewHolder {
        mHolderImpl.setBackgroundDrawable(viewId, drawable)
        return this
    }

    @TargetApi(16)
    fun setBackground(viewId: Int, drawable: Drawable): ListViewHolder {
        mHolderImpl.setBackground(viewId, drawable)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap): ListViewHolder {
        mHolderImpl.setImageBitmap(viewId, bitmap)
        return this
    }

    fun setImageResource(viewId: Int, resId: Int): ListViewHolder {
        mHolderImpl.setImageResource(viewId, resId)
        return this
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable): ListViewHolder {
        mHolderImpl.setImageDrawable(viewId, drawable)
        return this
    }

    fun setImageDrawable(viewId: Int, uri: Uri): ListViewHolder {
        mHolderImpl.setImageDrawable(viewId, uri)
        return this
    }

    @TargetApi(16)
    fun setImageAlpha(viewId: Int, alpha: Int): ListViewHolder {
        mHolderImpl.setImageAlpha(viewId, alpha)
        return this
    }

    fun setChecked(viewId: Int, checked: Boolean): ListViewHolder {
        mHolderImpl.setChecked(viewId, checked)
        return this
    }

    fun setProgress(viewId: Int, progress: Int): ListViewHolder {
        mHolderImpl.setProgress(viewId, progress)
        return this
    }

    fun setProgress(viewId: Int, progress: Int, max: Int): ListViewHolder {
        mHolderImpl.setProgress(viewId, progress, max)
        return this
    }

    fun setMax(viewId: Int, max: Int): ListViewHolder {
        mHolderImpl.setMax(viewId, max)
        return this
    }

    fun setRating(viewId: Int, rating: Float): ListViewHolder {
        mHolderImpl.setRating(viewId, rating)
        return this
    }

    fun setRating(viewId: Int, rating: Float, max: Int): ListViewHolder {
        mHolderImpl.setRating(viewId, rating, max)
        return this
    }

    fun setVisibility(viewId: Int, visible: Int): ListViewHolder {
        mHolderImpl.setVisibility(viewId, visible)
        return this
    }

    fun setOnClickListener(viewId: Int, listener: (v: View) -> Unit): ListViewHolder {
        mHolderImpl.setOnClickListener(viewId, View.OnClickListener { v -> listener(v) })
        return this
    }

    fun setOnTouchListener(
        viewId: Int, listener: (v: View, event: MotionEvent) -> Boolean
    ): ListViewHolder {
        mHolderImpl.setOnTouchListener(
            viewId, View.OnTouchListener { v, event -> listener(v, event) })
        return this
    }

    fun setOnLongClickListener(viewId: Int, listener: (v: View) -> Boolean): ListViewHolder {
        mHolderImpl.setOnLongClickListener(viewId, View.OnLongClickListener { v -> listener(v) })
        return this
    }

    fun setOnItemClickListener(
        viewId: Int, listener: (parent: AdapterView<*>, view: View, position: Int, i: Long) -> Unit
    ): ListViewHolder {
        mHolderImpl.setOnItemClickListener(viewId,
            OnItemClickListener { parent, view, position, id ->
                listener(parent, view, position, id)
            })
        return this
    }

    fun setOnItemLongClickListener(
        viewId: Int,
        listener: (parent: AdapterView<*>, view: View, position: Int, i: Long) -> Boolean
    ): ListViewHolder {
        mHolderImpl.setOnItemLongClickListener(viewId,
            OnItemLongClickListener { parent, view, position, id ->
                listener(parent, view, position, id)
            })
        return this
    }

    fun setOnItemSelectedClickListener(
        viewId: Int, listener: OnItemSelectedListener
    ): ListViewHolder {
        mHolderImpl.setOnItemSelectedClickListener(viewId, listener)
        return this
    }

    companion object {
        operator fun get(convertView: View?, parent: ViewGroup, layoutId: Int?): ListViewHolder =
            (convertView ?: layoutId?.let {
                LayoutInflater.from(parent.context)
                    .inflate(it, parent, false).apply {
                        this.tag = ListViewHolder(this)
                    }
            })?.tag as ListViewHolder
    }

}