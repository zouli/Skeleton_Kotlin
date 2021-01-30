package com.riverside.skeleton.kotlin.widget.containers.refreshview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.AbsListView
import android.widget.ListView
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 可刷新的Listview     1.0
 * b_e      2020/11/19
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RefreshListView(context: Context, attrs: AttributeSet?) : RefreshAbsListView(context, attrs) {
    constructor(context: Context) : this(context, null)

    //刷新方向
    @Attr(AttrType.FLAG)
    private val _direction: Int by AttributeSetInfo(
        context, attrs, R.styleable.RefreshListView,
        R.styleable.RefreshListView_rlv_direction, 2
    )

    //分割位
    @Attr(AttrType.DRAWABLE)
    private val _divider: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.RefreshListView,
        R.styleable.RefreshListView_rlv_divider, null
    )

    //分割位高度
    @Attr(AttrType.DIMENSION)
    private val _dividerHeight: Int by AttributeSetInfo(
        context, attrs, R.styleable.RefreshListView,
        R.styleable.RefreshListView_rlv_dividerHeight, 1
    )

    init {
        initView()
        setListener()
    }

    var divider = _divider
        set(value) {
            (getListView() as ListView).divider = value
            field = value
        }

    var dividerHeight = _dividerHeight
        set(value) {
            (getListView() as ListView).dividerHeight = value
            field = value
        }

    override fun updateDirection(): Int = _direction

    override fun initListView(): AbsListView = ListView(context).apply {
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.isVerticalScrollBarEnabled = false
        this.isHorizontalScrollBarEnabled = false
        this.descendantFocusability = ListView.FOCUS_BLOCK_DESCENDANTS
        this.divider = _divider
        if (_dividerHeight > 0) this.dividerHeight = _dividerHeight
        this.post { reloadList() }
    }

}