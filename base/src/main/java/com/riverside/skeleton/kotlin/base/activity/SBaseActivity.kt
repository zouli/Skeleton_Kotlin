package com.riverside.skeleton.kotlin.base.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.riverside.skeleton.kotlin.base.fragment.ResultCallback
import com.riverside.skeleton.kotlin.base.utils.KeyboardHelper
import com.riverside.skeleton.kotlin.base.utils.NoDoubleClickUtils
import com.riverside.skeleton.kotlin.util.extras.ExtrasHelper
import com.riverside.skeleton.kotlin.util.extras.IntentsHelper
import com.riverside.skeleton.kotlin.util.resource.hashCode16

/**
 * Activity基类   1.0.1
 * <p>
 * b_e            2019/09/23
 * 封装ForResult  2020/11/26
 */
abstract class SBaseActivity : AppCompatActivity() {
    lateinit var activity: SBaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this
        if (layoutId > 0) setContentView(layoutId)

        ExtrasHelper.intent = intent
        initView()
    }

    override fun onResume() {
        super.onResume()
        //初始化防双击时间
        NoDoubleClickUtils.initLastClickTime()
    }

    abstract fun initView()

    open val layoutId: Int get() = 0

    open val menuId: Int get() = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menuId.let {
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

    fun addFragment(containerViewId: Int, fragment: Fragment) =
        supportFragmentManager.beginTransaction().add(containerViewId, fragment).commit()

    fun replaceFragment(containerViewId: Int, fragment: Fragment) =
        supportFragmentManager.beginTransaction().replace(containerViewId, fragment).commit()

    /**
     * 验证方法实现方法
     */
    open fun doValidate(): Boolean = true

    /**
     * 封装ForResult
     */
    val callbacks = SparseArray<ResultCallback>()

    inline fun <reified T : Activity> startActivityForResult(
        vararg params: Pair<String, Any?>, noinline callback: ResultCallback
    ) {
        callbacks.put(callback.hashCode16(), callback)
        IntentsHelper.startActivityForResult(this, T::class.java, params, callback.hashCode16())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbacks[requestCode]?.let {
            it(resultCode, data)
            callbacks.remove(requestCode)
        }
    }
}