package com.riverside.skeleton.kotlin.basetest

import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.util.converter.dip
import com.riverside.skeleton.kotlin.util.dialog.ProgressDialog
import com.riverside.skeleton.kotlin.util.dialog.alert
import com.riverside.skeleton.kotlin.util.dialog.hProgressDialog
import com.riverside.skeleton.kotlin.util.dialog.progressDialog
import com.riverside.skeleton.kotlin.util.notice.toast
import kotlinx.coroutines.delay
import org.jetbrains.anko.button
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

class DialogActivity : SBaseActivity() {
    override fun initView() {
        title = "Dialog"

        verticalLayout {
            lparams(matchParent, matchParent)

            button("Normal Alert") {
                onClick {
                    alert {
                        title = "Test"
                        message = "Normal"
                        positiveButton("OK") { dialog, which ->
                            "OK".toast(activity)
                        }
                        show()
                    }
                }
            }

            button("Style Alert") {
                onClick {
                    alert(R.style.AppTheme_Dialog_Alert) {
                        title = "Test"
                        message = "Style"
                        positiveButton("是") { dialog, which ->
                            "YES".toast(activity)
                        }
                        negativeButton("否") { dialog, which ->
                            "NO".toast(activity)
                        }
                        neutralButton(android.R.string.cancel) { dialog, which ->
                            "CANCEL".toast(activity)
                        }
                        show()
                    }
                }
            }

            button("Handy Alert") {
                onClick {
                    alert("Handy", "Test") {
                        okButton { _, _ ->
                            "OK".toast(activity)
                        }
                        cancelButton { _, _ ->
                            "CANCEL".toast(activity)
                        }
                    }.show()
                }
            }

            button("Center Title Alert") {
                onClick {
                    alert {
                        centerTitle = "Test"
                        message = "Center Title"
                        positiveButton("OK") { dialog, which ->
                            "OK".toast(activity)
                        }
                        show()
                    }
                }
            }

            button("List Alert") {
                onClick {
                    alert {
                        items(
                            listOf(
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("ccc", "3")
                            ), true
                        ) { _, item, _ ->
                            item.key.toast(activity)
                        }
                        cancelButton { _, _ ->
                            "CANCEL".toast(activity)
                        }
                        showAtBottom(400.dip)
                    }.show()
                }
            }

            button("Single Choice Alert") {
                onClick {
                    alert {
                        singleChoice(
                            listOf(
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("ccc", "3")
                            ), 2
                        ) { _, item, _ ->
                            item.key.toast(activity)
                        }
                        okButton { _, _ ->
                            "OK".toast(activity)
                        }
                        cancelButton { _, _ ->
                            "CANCEL".toast(activity)
                        }
                    }.show()
                }
            }

            button("Multi Choice Alert") {
                onClick {
                    alert {
                        multiChoice(
                            listOf(
                                Item("aaa", "1"),
                                Item("bbb", "2"),
                                Item("ccc", "3")
                            ),
                            arrayOf(true, false, true)
                        ) { _, item, _, _ ->
                            item.key.toast(activity)
                        }
                        okButton { _, _ ->
                            "OK".toast(activity)
                        }
                        cancelButton { _, _ ->
                            "CANCEL".toast(activity)
                        }
                    }.show()
                }
            }

            button("Progress") {
                onClick {
                    progressDialog("Progress") {
                        title = "Test"
                        isCancelable = true
                        isIndeterminate = true
                        show()
                    }
                }
            }

            button("Horizontal Progress") {
                onClick {
                    val progressDialog = hProgressDialog {
                        title = "Test"
                        message = "Progress"
                        isCancelable = true
                        isIndeterminate = true
                        progressStyle = ProgressDialog.STYLE.HORIZONTAL
                        cancelButton { dialog, which ->

                        }
                    }.show()

                    repeat(100) {
                        progressDialog.progress += 1
                        delay(50)
                    }
                    progressDialog.dismiss()
                }
            }
        }
    }

    data class Item(val name: String, val key: String) {
        override fun toString(): String {
            return name
        }
    }
}