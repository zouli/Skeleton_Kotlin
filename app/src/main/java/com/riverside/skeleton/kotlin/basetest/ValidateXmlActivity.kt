package com.riverside.skeleton.kotlin.basetest;

import android.widget.CheckBox
import com.github.yoojia.inputs.AndroidNextInputs
import com.github.yoojia.inputs.LazyLoaders
import com.github.yoojia.inputs.StaticScheme
import com.github.yoojia.inputs.WidgetAccess
import com.riverside.skeleton.kotlin.R
import com.riverside.skeleton.kotlin.base.activity.SBaseActivity
import com.riverside.skeleton.kotlin.kotlinnextinputs.*
import kotlinx.android.synthetic.main.activity_validate_xml.*

class ValidateXmlActivity : SBaseActivity() {
    override val layoutId: Int get() = R.layout.activity_validate_xml

    override fun initView() {
        title = "Validate XML"

        for (i in 0 until 6) {
            val cb = CheckBox(activity)
            cb.id = R.id.cb_1
            cb.text = "cb_1"
            gl_1.addView(cb)
        }

        btn_do.setOnClickListener {
            validate()
        }
    }

    override fun doValidate(): Boolean {
        val snackbarMessageDisplay = AndroidSnackbarMessageDisplay()
        val toastMessageDisplay = AndroidToastMessageDisplay()
        val blankMessageDisplay = BlankMessageDisplay()
        val access = WidgetAccess(activity)

        val loader = LazyLoaders(activity)
        val inputs = AndroidNextInputs()

        inputs.setMessageDisplay(
            if (rb_show_snackbar.isChecked) snackbarMessageDisplay else toastMessageDisplay
        )

        inputs.add(
            access.findRadioGroup(R.id.rg_1),
            StaticScheme.Required().msg("请在rb_1,rb_2,rb_3中选择一个")
        )
        inputs.add(
            access.findGridLayoutSelectable(R.id.gl_1, R.id.cb_1),
            StaticScheme.Required().msg("请至少选择一个cb_1")
        )
        inputs.add(
            access.findOnceSatisfy(
                R.id.et_1,
                access.findEditText(R.id.et_1),
                access.findEditText(R.id.et_2)
            ),
            StaticScheme.Required().msg("文本框至少输入一项")
        )
        inputs.add(
            access.findEditText(R.id.et_2),
            SchemeExtend.RequiredAndPreviousRequired(loader.fromEditText(R.id.et_1))
        )

        return inputs.test()
    }
}
