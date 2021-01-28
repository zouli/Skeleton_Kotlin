package com.riverside.skeleton.kotlin.application

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.imnjh.imagepicker.ImageLoader
import com.imnjh.imagepicker.PickerConfig
import com.imnjh.imagepicker.SImagePicker
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.application.SBaseApplication
import com.riverside.skeleton.kotlin.db.DbBeanHelper
import com.riverside.skeleton.kotlin.db.DatabaseHelper
import com.riverside.skeleton.kotlin.dbtest.A
import com.riverside.skeleton.kotlin.dbtest.B
import com.riverside.skeleton.kotlin.dbtest.C
import com.riverside.skeleton.kotlin.util.file.createNoMediaFile
import com.riverside.skeleton.kotlin.util.file.mkdirs
import com.riverside.skeleton.kotlin.util.file.unaryPlus
import com.riverside.skeleton.kotlin.util.resource.getColorById
import com.squareup.picasso.Picasso

class MyApplication : SBaseApplication() {
    init {
        moduleApplications = arrayOf(SBaseApplication::class.java)
    }

    override fun onCreate() {
        super.onCreate()

        this.createNoMediaFile()

        DatabaseHelper.databaseName = mkdirs("db") + +"database.xml"
        DbBeanHelper.addBean(A::class, B::class, C::class)

        SImagePicker.init(
            PickerConfig.Builder().setAppContext(this)
                .setImageLoader(object : ImageLoader {
                    override fun bindImage(
                        imageView: ImageView?, uri: Uri?, width: Int, height: Int
                    ) {
                        Picasso.get().load(uri).resize(width, height)
                            .centerCrop().into(imageView)
                    }

                    override fun bindImage(imageView: ImageView?, uri: Uri?) {
                        Picasso.get().load(uri).into(imageView)
                    }

                    override fun createImageView(context: Context?): ImageView? {
                        val imageView = ImageView(context)
                        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                        return imageView
                    }

                    override fun createFakeImageView(context: Context?): ImageView? {
                        return ImageView(context)
                    }
                })
                .setToolbaseColor(getColorById(R.color.colorPrimary))
                .build()
        )
    }
}
