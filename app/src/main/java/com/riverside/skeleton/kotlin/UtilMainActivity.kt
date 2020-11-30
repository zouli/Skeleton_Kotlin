package com.riverside.skeleton.kotlin

import android.os.Bundle
import android.os.Environment
import android.text.format.Formatter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.file.file
import com.riverside.skeleton.kotlin.util.file.size
import com.riverside.skeleton.kotlin.util.file.unaryPlus
import com.riverside.skeleton.kotlin.util.notice.snackbar
import com.riverside.skeleton.kotlin.util.notice.toast
import com.riverside.skeleton.kotlin.util.resource.ContextHolder
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent

class UtilMainActivity : AppCompatActivity() {
    var i = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Util"

        verticalLayout {
            lparams(matchParent, matchParent)

            button("notice") {
                onClick {
                    "${i++}".snackbar(this@UtilMainActivity) {
//                        duration = SnackbarHelper.LENGTH_SHORT
                        textColor = "#FF00FF".toColorInt()
                        action("DO", "#00FFFF".toColorInt()) {
                            "bbb".toast(this@UtilMainActivity)
                        }
                        callback { "aaa".toast(this@UtilMainActivity) }
                    }.show()
                }
            }.lparams(matchParent, wrapContent)

            button("file") {
                onClick {
                    val size =
                        (Environment.getExternalStorageDirectory()
                            .toString() + +"iflytek.pcm").file.size
                    SLog.w(size.bString)
                    SLog.w(size.kbString)
                    SLog.w(size.mbString)
                    SLog.w(size.gbString)
                    SLog.w(size.toString())
                }
            }.lparams(matchParent, wrapContent)

            button("directory") {
                onClick {
//                    FileSize.units = FileSize.FLAG_IEC_UNITS
                    val size =
                        (Environment.getExternalStorageDirectory().toString() + +"DCIM").file.size
                    SLog.w(size.bString)
                    SLog.w(size.kbString)
                    SLog.w(size.mbString)
                    SLog.w(size.gbString)
                    SLog.w(size.toString())

                    SLog.w(Formatter.formatShortFileSize(this@UtilMainActivity, size.b.toLong()))
                }
            }.lparams(matchParent, wrapContent)

            button("application") {
                onClick {
                    SLog.w(ContextHolder.applicationContext)
                }
            }
        }
    }
}