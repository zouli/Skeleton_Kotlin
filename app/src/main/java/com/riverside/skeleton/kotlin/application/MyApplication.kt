package com.riverside.skeleton.kotlin.application

import com.riverside.skeleton.kotlin.base.application.SBaseApplication

class MyApplication: SBaseApplication() {
    init{
        moduleApplications = arrayOf(SBaseApplication::class.java)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
