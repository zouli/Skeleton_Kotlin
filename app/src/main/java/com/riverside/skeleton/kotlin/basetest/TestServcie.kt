package com.riverside.skeleton.kotlin.basetest

import com.riverside.skeleton.kotlin.base.service.SBaseService
import com.riverside.skeleton.kotlin.slog.SLog
import com.riverside.skeleton.kotlin.util.extras.Extra

class TestService : SBaseService() {
    private val flag: Int by Extra()

    override fun onCall(flags: Int, startId: Int) {
        SLog.w(flag)
    }

    fun getIndex() = (0..10).random()
}