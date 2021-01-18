package com.hawesome.bleconfig.model

import android.graphics.drawable.Drawable
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.view.BCApplication

/*
* 设置【点】状态：
* 默认，配置，验证
* 用于判定【点】设置是否成功
* */
enum class TagConfigState {
    DEFAULT {
        override fun getDrawable(): Drawable? {
            val drawable = context.getDrawable(R.drawable.ic_default)
            val tint = context.getColor(R.color.gray_500)
            drawable?.setTint(tint)
            return drawable
        }
    },
    CONFIGURED {
        override fun getDrawable(): Drawable? {
            val drawable = context.getDrawable(R.drawable.ic_uncheck)
            val tint = context.getColor(R.color.red_500)
            drawable?.setTint(tint)
            return drawable
        }
    },
    CHECKED {
        override fun getDrawable(): Drawable? {
            val drawable = context.getDrawable(R.drawable.ic_check)
            val tint = context.getColor(R.color.green_500)
            drawable?.setTint(tint)
            return drawable
        }
    };

    abstract fun getDrawable(): Drawable?
    val context = BCApplication.context
}