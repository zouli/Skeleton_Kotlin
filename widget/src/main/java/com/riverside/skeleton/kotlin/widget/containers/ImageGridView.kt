package com.riverside.skeleton.kotlin.widget.containers

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import com.riverside.skeleton.kotlin.util.attributeinfo.Attr
import com.riverside.skeleton.kotlin.util.attributeinfo.AttrType
import com.riverside.skeleton.kotlin.util.attributeinfo.AttributeSetInfo
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.ImageLoader
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.PicassoImageLoader
import kotlin.math.abs

/**
 * 图片显示Grid控件   1.1
 * b_e                      2020/11/23
 * 添加SmartColumn加载器     2020/12/05
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
        if (isReadOnly && isSmartColumnCount)
            smartColumnLoader.getColumnCount(imageList.size)
        else
            this.columnCount
    }

    /**
     * 显示图片
     */
    private fun showImage() {
        this.post {
            columnCount = abs(smartColumnCount)
            removeAllViews()

            (0 until imageList.count()).forEach { i ->
                this.addView(getImageView(i).apply {
                    getLayoutParams(layoutParams, i)
                })
            }

            //判断是否显示默认添加图片按钮
            if (!isReadOnly && addButtonIcon != null && childCount < imageCount) {
                this.addView(getAddImageView().apply {
                    getLayoutParams(layoutParams, imageList.size)
                })
            }
        }
    }

    /**
     * 取得LayoutParams
     */
    private fun getLayoutParams(layoutParams: ViewGroup.LayoutParams, position: Int) {
        with(layoutParams as LayoutParams) {
            val (row, col, spec) = smartColumnLoader.getCoordinate(
                position, columnCount, smartColumnCount
            )

            this.rowSpec = spec(row, spec)
            this.columnSpec = spec(col, spec)

            val width =
                this@ImageGridView.width - this@ImageGridView.paddingLeft - this@ImageGridView.paddingRight
            val itemWidth =
                (width - (columnCount - 1) * dividerSize) / columnCount * spec + (spec - 1) * dividerSize
            this.width = itemWidth
            this.height = itemWidth

            if (col > 0) this.leftMargin = dividerSize
            if (row > 0) this.topMargin = dividerSize
        }
    }

    /**
     * 生成图片容器
     */
    private val rlImage
        get() = RelativeLayout(context).apply {
            layoutParams = LayoutParams()
        }

    /**
     * 生成图片控件
     */
    private val ivImage
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
    private fun getImageView(position: Int) = rlImage.apply {
        val iWidth = this@ImageGridView.width / abs(smartColumnCount)
        addView(ivImage.apply {
            imageLoader.loadImage(this, imageList[position], iWidth, iWidth)
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
            setOnClickListener {
                if (::deleteImageClickListener.isInitialized)
                    deleteImageClickListener(position, imageList[position])
            }
        })
    }

    /**
     * 生成默认添加图片按钮
     */
    private fun getAddImageView() = rlImage.apply {
        addView(ivImage.apply {
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
    var imageLoader: ImageLoader = PicassoImageLoader()

    /**
     * SmartColumn加载器
     */
    var smartColumnLoader: SmartColumnLoader = DefaultSmartColumnLoader()

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

    /**
     * SmartColumn加载接口
     */
    interface SmartColumnLoader {
        /**
         * 取得行数
         */
        fun getColumnCount(size: Int): Int

        /**
         * 取得坐标
         */
        fun getCoordinate(
            position: Int, columnCount: Int, smartColumnCount: Int
        ): Triple<Int, Int, Int>
    }

    /**
     * 默认SmartColumn加载器
     */
    class DefaultSmartColumnLoader : SmartColumnLoader {
        override fun getColumnCount(size: Int) = when (size) {
            1 -> 1
            2, 4 -> 2
            3, 5, 6 -> -3
            else -> 3
        }

        override fun getCoordinate(
            position: Int, columnCount: Int, smartColumnCount: Int
        ): Triple<Int, Int, Int> {
            var spec = 1
            var row = position / columnCount
            var col = position % columnCount
            if (smartColumnCount < 0) {
                spec = if (position == 0) columnCount - 1 else 1
                row = when {
                    position == 0 -> 0
                    position < columnCount -> position - 1
                    else -> (position / columnCount) + columnCount - 1
                }
                col = when {
                    position == 0 -> 0
                    row < columnCount - 1 -> columnCount - 1
                    else -> position % columnCount
                }
            }

            return Triple(row, col, spec)
        }
    }
}