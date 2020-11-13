package com.riverside.skeleton.kotlin.base.utils

import android.view.KeyEvent
import java.lang.ref.WeakReference

/**
 * 返回键点击次数统计    1.0
 * b_e  2019/08/29
 */
object BackButtonHelper {
    //最后一次点击的时间
    private var lastTime: Long = 0
    //连续点击次数
    private var hits = 0

    fun onKeyUp(keyCode: Int, event: KeyEvent?, callback: (hits: Int) -> Unit): Boolean {
        val keyEvent = WeakReference(event)
        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.get()?.repeatCount == 0) {
            //点击间隔时间大于2秒重新计算
            if (System.currentTimeMillis() - lastTime > 2000) {
                hits = 0
            }
            callback(++hits)
            lastTime = System.currentTimeMillis()
            return true
        }
        return false
    }
}