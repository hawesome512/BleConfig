package com.hawesome.bleconfig.view.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.clj.fastble.data.BleDevice
import com.hawesome.bleconfig.kit.Repository

class ConfigModel:ViewModel() {

    private val connectLiveData = MutableLiveData<BleDevice>()

    val connectResult = Transformations.switchMap(connectLiveData){ Repository.connectForResult(it) }

    /*
    *   连接蓝牙
    * */
    fun connect(bleDevice: BleDevice){
        connectLiveData.value = bleDevice
    }

}