package com.riverside.skeleton.kotlin.widget.adapter.viewholders

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * 适用于RecyclerView的ViewHolder
 *
 * b_e      2020/11/16
 */
class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mHolderImpl: ViewHolderImpl = ViewHolderImpl(itemView)

    inline fun <reified T : View> findViewById(viewId: Int): T =
        mHolderImpl.findViewById(viewId)

    val context: Context
        get() = mHolderImpl.mItemView.context

    fun getItemView(): View = mHolderImpl.mItemView

    fun setText(viewId: Int, stringId: Int): RecyclerViewHolder {
        mHolderImpl.setText(viewId, stringId)
        return this
    }

    fun setText(viewId: Int, text: String): RecyclerViewHolder {
        mHolderImpl.setText(viewId, text)
        return this
    }

    fun setTextColor(viewId: Int, color: Int): RecyclerViewHolder {
        mHolderImpl.setTextColor(viewId, color)
        return this
    }

    fun setBackgroundColor(viewId: Int, color: Int): RecyclerViewHolder {
        mHolderImpl.setBackgroundColor(viewId, color)
        return this
    }

    fun setBackgroundResource(viewId: Int, resId: Int): RecyclerViewHolder {
        mHolderImpl.setBackgroundResource(viewId, resId)
        return this
    }

    fun setBackgroundDrawable(viewId: Int, drawable: Drawable): RecyclerViewHolder {
        mHolderImpl.setBackgroundDrawable(viewId, drawable)
        return this
    }

    @TargetApi(16)
    fun setBackground(viewId: Int, drawable: Drawable): RecyclerViewHolder {
        mHolderImpl.setBackground(viewId, drawable)
        return this
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap): RecyclerViewHolder {
        mHolderImpl.setImageBitmap(viewId, bitmap)
        return this
    }

    fun setImageResource(viewId: Int, resId: Int): RecyclerViewHolder {
        mHolderImpl.setImageResource(viewId, resId)
        return this
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable): RecyclerViewHolder {
        mHolderImpl.setImageDrawable(viewId, drawable)
        return this
    }

    fun setImageDrawable(viewId: Int, uri: Uri): RecyclerViewHolder {
        mHolderImpl.setImageDrawable(viewId, uri)
        return this
    }

    @TargetApi(16)
    fun setImageAlpha(viewId: Int, alpha: Int): RecyclerViewHolder {
        mHolderImpl.setImageAlpha(viewId, alpha)
        return this
    }

    fun setChecked(viewId: Int, checked: Boolean): RecyclerViewHolder {
        mHolderImpl.setChecked(viewId, checked)
        return this
    }

    fun setProgress(viewId: Int, progress: Int): RecyclerViewHolder {
        mHolderImpl.setProgress(viewId, progress)
        return this
    }

    fun setProgress(viewId: Int, progress: Int, max: Int): RecyclerViewHolder {
        mHolderImpl.setProgress(viewId, progress, max)
        return this
    }

    fun setMax(viewId: Int, max: Int): RecyclerViewHolder {
        mHolderImpl.setMax(viewId, max)
        return this
    }

    fun setRating(viewId: Int, rating: Float): RecyclerViewHolder {
        mHolderImpl.setRating(viewId, rating)
        return this
    }

    fun setRating(viewId: Int, rating: Float, max: Int): RecyclerViewHolder {
        mHolderImpl.setRating(viewId, rating, max)
        return this
    }

    fun setVisibility(viewId: Int, visible: Int): RecyclerViewHolder {
        mHolderImpl.setVisibility(viewId, visible)
        return this
    }

    fun setOnClickListener(viewId: Int, listener: (v: View) -> Unit): RecyclerViewHolder {
        mHolderImpl.setOnClickListener(viewId, View.OnClickListener { v -> listener(v) })
        return this
    }

    fun setOnTouchListener(
        viewId: Int, listener: (v: View, event: MotionEvent) -> Boolean
    ): RecyclerViewHolder {
        mHolderImpl.setOnTouchListener(
            viewId, View.OnTouchListener { v, event -> listener(v, event) })
        return this
    }
}