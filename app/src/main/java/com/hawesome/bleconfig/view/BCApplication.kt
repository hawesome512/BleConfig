package com.hawesome.bleconfig.view

import android.app.Application
import android.content.Context
import com.uuzuche.lib_zxing.activity.ZXingLibrary

class BCApplication :Application(){

    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        ZXingLibrary.initDisplayOpinion(this)
    }
}