package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.PicassoImageLoader

/**
 * 图片显示Grid控件   1.0
 * b_e      2020/11/23
 */
class ImageGridView(context: Context, attrs: AttributeSet?) : GridLayout(context, attrs) {
    constructor(context: Context) : this(context, null)

    var imageList = mutableListOf<String>()
        set(value) {
            field = value
            showImage()
        }

    /**
     * 只读
     */
    @Attr(AttrType.BOOLEAN)
    private val _isReadOnly: Boolean by AttributeSetInfo(
        attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_readOnly, false
    )

    /**
     * 是否动态调整列数
     * 1个图片时为1列
     * 2，4个图片时为2列
     * 其他情况为3列
     */
    @Attr(AttrType.BOOLEAN)
    private val _isSmartColumnCount: Boolean by AttributeSetInfo(
        attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_smartColumnCount, false
    )

    /**
     * 添加图片按钮图标
     */
    @Attr(AttrType.DRAWABLE)
    private val _addButtonIcon: Drawable? by AttributeSetInfo(
        attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_addButtonIcon, null
    )

    /**
     * 最多可添加几个图片
     */
    @Attr(AttrType.INTEGER)
    private val _imageCount: Int by AttributeSetInfo(
        attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_imageCount, 9
    )

    /**
     * 分割线宽度
     */
    @Attr(AttrType.DIMENSION)
    private val _dividerSize: Int by AttributeSetInfo(
        attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_dividerSize, 0
    )

    init {
        showImage()
    }

    var isSmartColumnCount = _isSmartColumnCount
        set(value) {
            field = value
            showImage()
        }

    var addButtonIcon = _addButtonIcon
        set(value) {
            field = value
            showImage()
        }

    var dividerSize = _dividerSize
        set(value) {
            field = value
            showImage()
        }

    var imageCount = _imageCount
        set(value) {
            field = value
            showImage()
        }

    /**
     * 是否为只读状态
     */
    var isReadOnly = _isReadOnly
        set(value) {
            field = value
            showImage()
        }

    /**
     * 动态列数
     */
    private val smartColumnCount by lazy {
        if (isReadOnly && isSmartColumnCount) when (imageList.size) {
            1 -> 1
            2, 4 -> 2
            else -> 3
        } else this.columnCount
    }

    /**
     * 显示图片
     */
    private fun showImage() {
        this.post {
            columnCount = smartColumnCount
            removeAllViews()

            (0 until imageList.count()).forEach { i ->
                this.addView(getImageView(i))
            }

            //判断是否显示默认添加图片按钮
            if (!isReadOnly && addButtonIcon != null && childCount < imageCount) {
                this.addView(getAddImageView())
            }
        }
    }

    /**
     * 生成图片容器
     */
    private val rl_image
        get() = RelativeLayout(context).apply {
            layoutParams =
                LayoutParams(
                    this@ImageGridView.width / smartColumnCount,
                    this@ImageGridView.width / smartColumnCount
                )
            setPadding(dividerSize, dividerSize, dividerSize, dividerSize)
        }

    /**
     * 生成图片控件
     */
    private val iv_image
        get() = ImageView(context).apply {
            layoutParams =
                RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    .apply { addRule(RelativeLayout.CENTER_IN_PARENT) }
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }

    /**
     * 取得图片控件
     */
    private fun getImageView(position: Int) = rl_image.apply {
        val iWidth = this@ImageGridView.width / smartColumnCount
        addView(iv_image.apply {
            imageLoader.loadImage(this@apply, imageList[position], iWidth, iWidth)
            this.tag = position
            setOnClickListener { v ->
                if (::imageClickListener.isInitialized)
                    imageClickListener(v, position, imageList[position])
            }
        })

        if (!isReadOnly) addView(ImageView(context).apply {
            layoutParams = RelativeLayout.LayoutParams(iWidth / 4, iWidth / 4).apply {
                addRule(RelativeLayout.ALIGN_PARENT_TOP)
                addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
            }
            setPadding(4.dip, 4.dip, 4.dip, 4.dip)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.imagegrid_delete_image)
            tag = position
            setOnClickListener { v ->
                if (::deleteImageClickListener.isInitialized)
                    deleteImageClickListener(position, imageList[position])
            }
        })
    }

    /**
     * 生成默认添加图片按钮
     */
    private fun getAddImageView() = rl_image.apply {
        addView(iv_image.apply {
            setImageDrawable(addButtonIcon)
            setOnClickListener {
                if (::addImageClickListener.isInitialized) addImageClickListener()
            }
        })
    }

    /**
     * 删除指定图片
     */
    fun removeImage(index: Int) {
        imageList.removeAt(index)
        showImage()
    }

    /**
     * 图片加载器
     */
    var imageLoader = PicassoImageLoader()

    private lateinit var imageClickListener: (v: View, position: Int, url: String) -> Unit

    fun setOnImageClickListener(block: (v: View, position: Int, url: String) -> Unit) {
        imageClickListener = block
    }

    private lateinit var deleteImageClickListener: (position: Int, url: String) -> Unit

    fun setDeleteImageClickListener(block: (position: Int, url: String) -> Unit) {
        deleteImageClickListener = block
    }

    private lateinit var addImageClickListener: () -> Unit

    fun setAddImageClickListener(block: () -> Unit) {
        addImageClickListener = block
    }
}