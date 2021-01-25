package com.riverside.skeleton.kotlin.widget.containers.refreshview

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.BaseAdapter
import android.widget.ScrollView
import androidx.annotation.RequiresApi
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.containers.CompleteListView

/**
 * 可刷新的CompleteListView  1.0
 * b_e  2021/01/25
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class RefreshCompleteListView(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
) : RefreshView<ScrollView>(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var listView: CompleteListView
    private lateinit var scrollView: ScrollView

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this(context, attrs, defStyleAttr, 0)

    //刷新方向
    @Attr(AttrType.FLAG)
    private val _direction: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshCompleteListView,
        R.styleable.RefreshCompleteListView_rclv_direction, 2
    )

    //分割位
    @Attr(AttrType.DRAWABLE)
    private val _divider: Drawable? by AttributeSetInfo(
        attrs, R.styleable.RefreshCompleteListView,
        R.styleable.RefreshCompleteListView_rclv_divider, null
    )

    //分割位高度
    @Attr(AttrType.DIMENSION)
    private val _dividerHeight: Int by AttributeSetInfo(
        attrs, R.styleable.RefreshCompleteListView,
        R.styleable.RefreshCompleteListView_rclv_dividerHeight, 1
    )

    init {
        initView()
        setListener()
    }

    var divider = _divider
        set(value) {
            getListView().divider = value
            field = value
        }

    var dividerHeight = _dividerHeight
        set(value) {
            getListView().dividerHeight = value
            field = value
        }

    fun setAdapter(adapter: BaseAdapter) {
        listView.adapter = adapter
    }

    fun setOnItemClickListener(listener: (view: View, position: Int) -> Unit) =
        listView.setOnItemClickListener { view, position ->
            listener(view, position)
        }

    fun setEmptyView(emptyView: View) {
        listView.emptyView = emptyView
    }

    fun getListView() = listView

    fun getScrollView() = scrollView

    /**
     * 取得刷新模式
     *
     * @return
     */
    override fun updateDirection(): Int = _direction

    /**
     * 初始化List控件
     *
     * @return
     */
    override fun getView(): ScrollView = ScrollView(context).also {
        it.isVerticalScrollBarEnabled = false
        it.isHorizontalScrollBarEnabled = false
        it.addView(
            CompleteListView(
                context
            ).apply {
                this.layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                this.orientation = VERTICAL
                this.descendantFocusability = FOCUS_BLOCK_DESCENDANTS
                this.divider = _divider
                if (_dividerHeight > 0) this.dividerHeight = _dividerHeight
                this.post { reloadList() }
            }.apply { listView = this })
    }.apply { scrollView = this }
}