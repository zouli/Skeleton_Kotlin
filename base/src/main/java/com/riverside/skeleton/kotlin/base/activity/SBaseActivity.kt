package com.riverside.skeleton.kotlin.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.riverside.skeleton.kotlin.base.utils.KeyboardHelper
import com.riverside.skeleton.kotlin.base.utils.NoDoubleClickUtils

/**
 * Activity基类   1.0
 * <p>
 * b_e  2019/09/23
 */
abstract class SBaseActivity : AppCompatActivity() {
    lateinit var activity: SBaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this
        if (setLayoutID() > 0) setContentView(setLayoutID())

        initView()
    }

    override fun onResume() {
        super.onResume()
        //初始化防双击时间
        NoDoubleClickUtils.initLastClickTime()
    }

    abstract fun initView()

    open fun setLayoutID(): Int = 0

    open fun setMenuID(): Int = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return setMenuID().let {
            if (it == 0) {
                super.onCreateOptionsMenu(menu)
            } else {
                menuInflater.inflate(it, menu)
                true
            }
        }
    }

    /**
     * 验证方法
     * <p>
     * 在子类中重写doValidate()方法，填写验证逻辑
     */
    fun validate(): Boolean {
        KeyboardHelper.init(activity).hideKeyboard()

        return !NoDoubleClickUtils.isDoubleClick && doValidate().also {
            if (it) NoDoubleClickUtils.initLastClickTime()
        }
    }

    /**
     * 验证方法实现方法
     */
    open fun doValidate(): Boolean = true
}