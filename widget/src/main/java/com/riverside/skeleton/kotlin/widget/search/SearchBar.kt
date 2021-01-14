package com.riverside.skeleton.kotlin.widget.search

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.widget.R
import kotlinx.android.synthetic.main.view_search_bar.view.*

/**
 * 搜索控件 1.1
 *
 * ?attr/searchBarStyle   输入框背景样式
 * ?attr/searchBarEditTextStyle 输入框样式
 * ?attr/searchBarButtonStyle   按钮样式
 *
 * b_e  2021/01/07
 * 1.1  添加是否显示查询按钮  2021/01/14
 * 1.1  添加是否得到焦点    2021/01/14
 */
class SearchBar(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    //左侧图片
    @Attr(AttrType.DRAWABLE)
    private val logo: Drawable? by AttributeSetInfo(
        attrs, R.styleable.SearchBar, R.styleable.SearchBar_sb_logo, null
    )

    //hint文字
    @Attr(AttrType.STRING)
    private val hint: String by AttributeSetInfo(
        attrs, R.styleable.SearchBar, R.styleable.SearchBar_sb_hint, ""
    )

    //取消按钮文字
    @Attr(AttrType.STRING)
    private val cancelTitle: String by AttributeSetInfo(
        attrs, R.styleable.SearchBar, R.styleable.SearchBar_sb_cancelButton, ""
    )

    //查询按钮文字
    @Attr(AttrType.STRING)
    private val searchTitle: String by AttributeSetInfo(
        attrs, R.styleable.SearchBar, R.styleable.SearchBar_sb_searchButton, ""
    )

    //输入框左侧空白
    @Attr(AttrType.DIMENSION)
    private val editTextMarginStart: Int by AttributeSetInfo(
        attrs, R.styleable.SearchBar,
        R.styleable.SearchBar_sb_editTextMarginStart, 0
    )

    //是否显示查询按钮
    @Attr(AttrType.BOOLEAN)
    private val buttonVisibility: Boolean by AttributeSetInfo(
        attrs, R.styleable.SearchBar,
        R.styleable.SearchBar_sb_buttonVisibility, true
    )

    //是否得到焦点
    @Attr(AttrType.BOOLEAN)
    private val focusable: Boolean by AttributeSetInfo(
        attrs, R.styleable.SearchBar,
        R.styleable.SearchBar_sb_isFocusable, true
    )

    private var _buttonVisibility = buttonVisibility

    fun setLogo(logo: Drawable) {
        iv_logo.setImageDrawable(logo)
        iv_logo.visibility = View.VISIBLE
    }

    fun setHint(hint: String) {
        et_search.hint = hint
    }

    fun setCancelTitle(title: String) {
        if (title.isNotEmpty()) tv_cancel.text = title
    }

    fun setSearchTitle(title: String) {
        if (title.isNotEmpty()) tv_search.text = title
    }

    fun setEditTextMarginStart(start: Int) {
        (et_search.layoutParams as LayoutParams).leftMargin = start
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            (et_search.layoutParams as LayoutParams).marginStart = start
        }
    }

    fun setButtonVisibility(visibility: Boolean) {
        _buttonVisibility = visibility
        setSearchButton(!et_search.text.isNullOrEmpty())
    }

    override fun setFocusable(focusable: Boolean) {
        if (focusable) et_search.requestFocus()
        else ll_background.requestFocus()
    }

    var text
        get() = et_search.text.toString()
        set(value) = et_search.setText(value)

    init {
        initView()
        initEvent()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_search_bar, this@SearchBar)
        logo?.let {
            setLogo(it)
        }
        setHint(hint)
        setEditTextMarginStart(editTextMarginStart)
        setCancelTitle(cancelTitle)
        setSearchTitle(searchTitle)
        setSearchButton(!et_search.text.isNullOrEmpty())

        isFocusable = focusable
    }

    private fun initEvent() {
        et_search.setOnFocusChangeListener { _, hasFocus ->
            setSearchButton(hasFocus && !et_search.text.isNullOrEmpty())
        }

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                setSearchButton(s.isNotEmpty())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })
    }

    /**
     * 添加输入框修改监视事件
     */
    fun addTextChangedListener(textWatcher: TextWatcher) {
        et_search.addTextChangedListener(textWatcher)
    }

    /**
     * 查询按钮按下
     */
    fun setOnSearchListener(block: (text: String) -> Unit) {
        tv_search.setOnClickListener {
            block(et_search.text.toString())
        }
    }

    /**
     * 取消按钮按下
     */
    fun setOnCancelListener(block: () -> Unit) {
        tv_cancel.setOnClickListener {
            block()
        }
    }

    /**
     * 切换按钮显示状态
     */
    private fun setSearchButton(isSearch: Boolean) {
        tv_search.visibility = if (isSearch && _buttonVisibility) View.VISIBLE else View.GONE
        tv_cancel.visibility = if (!isSearch && _buttonVisibility) View.VISIBLE else View.GONE
    }
}