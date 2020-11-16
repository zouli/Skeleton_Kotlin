package com.riverside.skeleton.kotlin.widget.adapter.viewholders

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.*
import android.widget.AdapterView.*
import androidx.annotation.IdRes

/**
 * ViewHolder操作子视图的实现类
 *
 * b_e      2020/11/16
 */
class ViewHolderImpl constructor(itemView: View) {
    var mCacheViews = mutableMapOf<Int, View>()
    val mItemView: View = itemView

    inline fun <reified T : View> findViewById(@IdRes viewId: Int): T =
        mCacheViews.getOrPut(viewId) { mItemView.findViewById(viewId) } as T

    fun setText(viewId: Int, stringId: Int) =
        findViewById<TextView>(viewId).setText(stringId)

    fun setText(viewId: Int, text: String?) {
        findViewById<TextView>(viewId).text = text
    }

    fun setTextColor(viewId: Int, color: Int) =
        findViewById<TextView>(viewId).setTextColor(color)

    fun setBackgroundColor(viewId: Int, color: Int) =
        findViewById<View>(viewId).setBackgroundColor(color)

    fun setBackgroundResource(viewId: Int, resId: Int) =
        findViewById<View>(viewId).setBackgroundResource(resId)

    fun setBackgroundDrawable(viewId: Int, drawable: Drawable?) =
        findViewById<View>(viewId).setBackgroundDrawable(drawable)

    @TargetApi(16)
    fun setBackground(viewId: Int, drawable: Drawable?) {
        findViewById<View>(viewId).background = drawable
    }

    fun setImageBitmap(viewId: Int, bitmap: Bitmap?) =
        findViewById<ImageView>(viewId).setImageBitmap(bitmap)

    fun setImageResource(viewId: Int, resId: Int) {
        findViewById<ImageView>(viewId).setImageResource(resId)
    }

    fun setImageDrawable(viewId: Int, drawable: Drawable) =
        findViewById<ImageView>(viewId).setImageDrawable(drawable)

    fun setImageDrawable(viewId: Int, uri: Uri) =
        findViewById<ImageView>(viewId).setImageURI(uri)

    @TargetApi(16)
    fun setImageAlpha(viewId: Int, alpha: Int) {
        findViewById<ImageView>(viewId).imageAlpha = alpha
    }

    fun setChecked(viewId: Int, checked: Boolean) {
//        val checkable: Checkable = findViewById(viewId)
//        checkable.isChecked = checked
        with(findViewById<View>(viewId)) {
            if (this is Checkable) {
                this.isChecked = checked
            }
        }
    }

    fun setProgress(viewId: Int, progress: Int) {
        findViewById<ProgressBar>(viewId).progress = progress
    }

    fun setProgress(viewId: Int, progress: Int, max: Int) =
        with(findViewById<ProgressBar>(viewId)) {
            this.max = max
            this.progress = progress
        }

    fun setMax(viewId: Int, max: Int) {
        findViewById<ProgressBar>(viewId).max = max
    }

    fun setRating(viewId: Int, rating: Float) {
        val view: RatingBar = findViewById(viewId)
        view.rating = rating
    }

    fun setVisibility(viewId: Int, visible: Int) {
        findViewById<View>(viewId).visibility = visible
    }

    fun setRating(viewId: Int, rating: Float, max: Int) =
        with(findViewById<RatingBar>(viewId)) {
            this.max = max
            this.rating = rating
        }

    fun setOnClickListener(viewId: Int, listener: View.OnClickListener) =
        findViewById<View>(viewId).setOnClickListener(listener)

    fun setOnTouchListener(viewId: Int, listener: View.OnTouchListener) =
        findViewById<View>(viewId).setOnTouchListener(listener)

    fun setOnLongClickListener(viewId: Int, listener: View.OnLongClickListener) =
        findViewById<View>(viewId).setOnLongClickListener(listener)

    fun setOnItemClickListener(viewId: Int, listener: OnItemClickListener) {
        findViewById<AdapterView<*>>(viewId).onItemClickListener = listener
    }

    fun setOnItemLongClickListener(viewId: Int, listener: OnItemLongClickListener) {
        findViewById<AdapterView<*>>(viewId).onItemLongClickListener = listener
    }

    fun setOnItemSelectedClickListener(viewId: Int, listener: OnItemSelectedListener) {
        findViewById<AdapterView<*>>(viewId).onItemSelectedListener = listener
    }

}