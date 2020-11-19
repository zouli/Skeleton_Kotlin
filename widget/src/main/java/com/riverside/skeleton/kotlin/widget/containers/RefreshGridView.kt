package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.AbsListView
import android.widget.GridView
import androidx.annotation.RequiresApi
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R

/**
 * 可刷新的Gridview     1.0
 * b_e      2020/11/19
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RefreshGridView(context: Context, attrs: AttributeSet?) : RefreshAbsListView(context, attrs) {
    constructor(context: Context) : this(context, null)

    @Attr(AttrType.FLAG)
    private val _direction: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshGridView,
        R.styleable.RefreshGridView_rgv_direction, 2
    )

    @Attr(AttrType.INTEGER)
    private val _numColumns: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshGridView,
        R.styleable.RefreshGridView_rgv_numColumns, 1
    )

    @Attr(AttrType.DIMENSION)
    private val _horizontalSpacing: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshGridView,
        R.styleable.RefreshGridView_rgv_horizontalSpacing, 0
    )

    @Attr(AttrType.DIMENSION)
    private val _verticalSpacing: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshGridView,
        R.styleable.RefreshGridView_rgv_verticalSpacing, 0
    )

    init {
        initView()
        setListener()
    }

    var numColumns = _numColumns
        set(value) {
            (getListView() as GridView).numColumns = value
            field = value
        }

    var horizontalSpacing = _horizontalSpacing
        set(value) {
            (getListView() as GridView).horizontalSpacing = value
            field = value
        }

    var verticalSpacing = _verticalSpacing
        set(value) {
            (getListView() as GridView).verticalSpacing = value
            field = value
        }

    override fun updateDirection(): Int = _direction

    override fun initListView(): AbsListView = GridView(context).apply {
        this.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        this.isVerticalScrollBarEnabled = false
        this.isHorizontalScrollBarEnabled = false
        this.descendantFocusability = GridView.FOCUS_BLOCK_DESCENDANTS
        this.numColumns = _numColumns
        if (_horizontalSpacing > 0) this.horizontalSpacing = _horizontalSpacing
        if (_verticalSpacing > 0) this.verticalSpacing = _verticalSpacing
        this.post { reloadList() }
    }
}