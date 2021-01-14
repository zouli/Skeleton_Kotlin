package com.riverside.skeleton.kotlin.widget.gallery

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.extras.Extra
import com.riverside.skeleton.kotlin.widget.R
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.ImageLoader
import com.riverside.skeleton.kotlin.widget.gallery.imageloader.PicassoImageLoader
import com.riverside.skeleton.kotlin.widget.gallery.pager.PagerView
import kotlinx.android.synthetic.main.activity_full_screen.*
import java.lang.Exception

/**
 * 共通图片全屏显示画面   1.0
 * b_e      2020/11/26
 */
class FullScreenActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_full_screen

    private lateinit var imageLoader: ImageLoader

    private lateinit var pagerView: PagerView

    //图片地址
    private val image_url: List<String> by Extra()

    //默认图片index
    private val default_index: Int by Extra()

    //图片加载器
    private val image_loader: String by Extra(default = PicassoImageLoader::class.java.name)

    //分页显示器
    private val pager_view: String by Extra()

    override fun initView() {
        if (pager_view.isNotEmpty()) pagerView =
            Class.forName(pager_view).newInstance() as PagerView

        container.adapter = ImagePagerAdapter()
        container.setCurrentItem(default_index, true)
        container.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (::pagerView.isInitialized) pagerView.onPageScrollStateChanged(state)
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                if (::pagerView.isInitialized)
                    pagerView.onPageScrolled(
                        position, image_url.size, positionOffset, positionOffsetPixels
                    )
            }

            override fun onPageSelected(position: Int) {
                if (::pagerView.isInitialized)
                    pagerView.onPageSelected(position, image_url.size)
            }
        })

        if (::pagerView.isInitialized) {
            activity_full_image.addView(pagerView.getView(activity, image_url.size))
            pagerView.onPageSelected(default_index, image_url.size)
        }
    }

    private fun loadImage(imageView: ImageView, position: Int) {
        if (!::imageLoader.isInitialized) imageLoader =
            try {
                Class.forName(image_loader).newInstance() as ImageLoader
            } catch (e: Exception) {
                PicassoImageLoader()
            }
        imageLoader.loadImage(imageView, image_url[position])
    }

    inner class ImagePagerAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, o: Any): Boolean = view == o

        override fun getCount(): Int = image_url.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any =
            PhotoView(container.context).apply {
                loadImage(this, position)
                container.addView(
                    this,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

        override fun destroyItem(container: ViewGroup, position: Int, o: Any) {
            container.removeView(o as View)
        }
    }

    companion object {
        const val IMAGE_URL = "image_url"
        const val DEFAULT_INDEX = "default_index"
        const val IMAGE_LOADER = "image_loader"
        const val PAGER_VIEW = "pager_view"
    }
}