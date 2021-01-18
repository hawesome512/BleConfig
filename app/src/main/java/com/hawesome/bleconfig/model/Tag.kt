package com.hawesome.bleconfig.model

import android.renderscript.Sampler
import androidx.lifecycle.MutableLiveData

/*
* 设置【点】
* */
data class Tag(
    val name: String,
    val address: Int,
    var value: Int = NULL_VALUE,
    var state: TagConfigState = TagConfigState.DEFAULT
) {
    companion object {
        const val NULL_VALUE = -1
    }

    val valueLiveData = MutableLiveData<Int>()

    fun update(newValue:Int){
        value = newValue
        valueLiveData.postValue(newValue)
    }
}