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
import com.riverside.skeleton.kotlin.util.screen.deviceWidth
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.ImageLoader
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.PicassoImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import q.rorbin.fastimagesize.FastImageSize
import kotlin.math.abs

/**
 * 图片显示Grid控件   1.1
 *
 * b_e  2020/11/23
 * 1.1 添加SmartColumn加载器 2020/12/05
 * 1.1 修改一个图片的显示状态  2020/12/29
 * 1.2 显示前先计算控件大小   2021/01/25
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
        context, attrs, R.styleable.ImageGridView,
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
        context, attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_smartColumnCount, false
    )

    /**
     * 添加图片按钮图标
     */
    @Attr(AttrType.DRAWABLE)
    private val _addButtonIcon: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_addButtonIcon, null
    )

    /**
     * 删除图片按钮图标
     */
    @Attr(AttrType.DRAWABLE)
    private val _deleteButtonIcon: Drawable? by AttributeSetInfo(
        context, attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_deleteButtonIcon, null
    )

    /**
     * 最多可添加几个图片
     */
    @Attr(AttrType.INTEGER)
    private val _imageCount: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageGridView,
        R.styleable.ImageGridView_igv_imageCount, 9
    )

    /**
     * 分割线宽度
     */
    @Attr(AttrType.DIMENSION)
    private val _dividerSize: Int by AttributeSetInfo(
        context, attrs, R.styleable.ImageGridView,
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

    var deleteButtonIcon = _deleteButtonIcon
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
    private val smartColumnCount
        get() =
            if (isReadOnly && isSmartColumnCount)
                smartColumnLoader.getColumnCount(imageList.size)
            else
                this.columnCount

    private var realWidth = 0

    private val realHeightCache = mutableMapOf<String, Int>()

    var realHeight = 0

    /**
     * 取得网络图片大小
     */
    private fun getNetworkImageSize(url: String) = runBlocking {
        withContext(Dispatchers.Default) {
            FastImageSize.with(url).setUseCache(false).get()
        }
    }

    /**
     * 取得Item的宽度
     */
    private fun getItemWidth(columnCount: Int, spec: Int) =
        (realWidth - paddingLeft - paddingRight - (columnCount - 1) * dividerSize) / columnCount * spec + (spec - 1) * dividerSize

    /**
     * 取得真实宽度、高度
     */
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        val widthMode = MeasureSpec.getMode(widthSpec)
        val widthSize = MeasureSpec.getSize(widthSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> context.deviceWidth.coerceAtMost(widthSize)
            MeasureSpec.AT_MOST -> 0.coerceAtMost(widthSize)
            else -> return
        }

        realWidth = width
        realHeight = when {
            isMulti -> {
                val columnCount = abs(smartColumnCount)
                val (row, _, spec) = smartColumnLoader.getCoordinate(
                    if (isReadOnly || imageList.size == imageCount) imageList.size - 1 else imageList.size
                    , columnCount, smartColumnCount
                )
                val itemWidth = getItemWidth(columnCount, spec)
                (row + spec) * itemWidth + (if (row > 0) row else 0) * dividerSize
            }
            imageList.size == 1 -> {
                realHeightCache[imageList[0]] ?: getNetworkImageSize(imageList[0]).let { size ->
                    ((realWidth * 2 / 3).toDouble() / size[0].toDouble() * size[1].toDouble()).toInt()
                        .apply {
                            realHeightCache[imageList[0]] = this
                        }
                }
            }
            else -> 0
        } + paddingTop + paddingBottom

        if (realHeight > 0) setMeasuredDimension(realWidth, realHeight)
    }

    /**
     * 是否为多张图片
     */
    private val isMulti get() = imageList.size > 1 || !isReadOnly || !isSmartColumnCount

    /**
     * 显示图片
     */
    private fun showImage() {
        this.post {
            removeAllViewsInLayout()

            columnCount = abs(smartColumnCount)

            (0 until imageList.count()).forEach { i ->
                val view = getImageView(i)
                this.addViewInLayout(view, i, getLayoutParams(view.layoutParams, i))
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
    private fun getLayoutParams(layoutParams: ViewGroup.LayoutParams, position: Int) =
        with(layoutParams as LayoutParams) {
            val (row, col, spec) = smartColumnLoader.getCoordinate(
                position, columnCount, smartColumnCount
            )

            this.rowSpec = spec(row, spec)
            this.columnSpec = spec(col, spec)

            val itemWidth = getItemWidth(columnCount, spec)

            if (isMulti) {
                this.width = itemWidth
                this.height = itemWidth
            } else this.width = itemWidth * 2 / 3

            if (col > 0) this.leftMargin = dividerSize
            if (row > 0) this.topMargin = dividerSize
            this
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
        val iWidth = realWidth / abs(smartColumnCount)
        addView(ivImage.apply {
            if (isMulti)
                imageLoader.loadImage(this, imageList[position], iWidth, iWidth)
            else
                imageLoader.loadImage(this, imageList[position])
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
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            deleteButtonIcon?.let { setImageDrawable(it) }
                ?: setImageResource(R.drawable.imagegrid_delete_image)
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
                    else -> (position / columnCount) + 1
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