package com.hawesome.bleconfig.kit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hawesome.bleconfig.model.*

/*
* 【点】工具类
* */
object TagKit : BleReceivedCallback {

    const val TAG = "TagKit"

    //普通监控点
    private val tagList = mutableListOf<Tag>()

    //当前连接的设备类型，依设计方案，同时刻只允许一台外设接入
    private var deviceType: DeviceType? = null

    //设置结果
    private var configSize = 0
    private var checkSize = 0
    val configResultLiveData = MutableLiveData<Boolean?>(null)
    val configProcessLiveData = MutableLiveData<Float>(0f)

    fun onConnected(bleType: String) {
        deviceType = Devices.model.types.firstOrNull { it.name == bleType }
        tagList.clear()
        deviceType?.zones?.forEach { zone ->
            val location = zone.location.toInt(16)
            zone.items.forEachIndexed { index, item ->
                //Ir:1000，默认值
                val infos = item.split(Devices.NAME_VALUE_SEPARATOR)
                val defaultValue = if (infos.size == 2) infos[1].toInt() else Tag.NULL_VALUE
                val tag = Tag(infos[0], location + index, defaultValue)
                tagList.add(tag)
            }
        }
    }

    fun onDisconnected() {
        tagList.clear()
        deviceType = null
        configSize = 0
        checkSize = 0
        configResultLiveData.postValue(null)
        configProcessLiveData.postValue(0f)
    }

    /*
    *   返回有效数据
    * */
    override fun onSuccess(result: ReceivedResult) {
        val location = result.cmdInfo.location
        val lastLocation = getLastLocation()
        when (result) {
            ReceivedResult.WRITE_SUCCESS -> {
                deviceType?.zones?.firstOrNull { it.location.toInt(16) == location }?.let { zone ->
                    getTags(zone.getItemNames()).forEach {
                        //记录片区将跳过核对
                        it.state =
                            if (zone.log) TagConfigState.CHECKED else TagConfigState.CONFIGURED
                    }
                }
                configProcessLiveData.postValue(0.5f / configSize)
                //设置最后一个片区后，开始执行核对
                if (location == lastLocation) requestCheck()
            }
            ReceivedResult.READ_SUCCESS -> {
                val data = result.data
                data.forEachIndexed { index, i ->
                    val tag = tagList.first { it.address == location + index }
                    //核对【点】值相同，例外：时钟片区时间实时更新无法核对（跳过）
                    if (tag.value == i || location == lastLocation) {
                        tag.state = TagConfigState.CHECKED
                    }
                    configProcessLiveData.postValue(0.5f / checkSize)
                    //核对至最后一个点，发布【核对】完成的通知
                    if (location == lastLocation && index == data.size - 1) {
                        val result = checkConfigSuccess()
//                        val failedTags = tagList.filter { it.state != TagConfigState.CHECKED }
                        configResultLiveData.postValue(result)
                    }
                }
            }
            ReceivedResult.ERROR -> {
            }
        }
    }

    fun getTags(names: List<String>) = tagList.filter { names.contains(it.name) }

    fun getDeviceType() = deviceType

    fun requestConfig() {
        tagList.forEach { it.state = TagConfigState.DEFAULT }
        val cmds = mutableListOf<BleCommand>()
        deviceType?.zones?.forEach { address ->
            val values = getTags(address.getItemNames()).map { it.value }
            val cmdInfo = CMDInfo(CMDType.WRITE, address.location.toInt(16), values.size)
            val cmd = ModbusKit.buildWriteCMD(cmdInfo, values)
            val bleCommand = BleCommand(cmd, BleCommand.DEFAULT_REPEAT)
            cmds.add(bleCommand)
        }
        configSize = cmds.size
        BluetoothKit.executeCMDList(cmds)
    }

    private fun getLastLocation() = deviceType?.zones?.lastOrNull()?.location?.toIntOrNull(16) ?: -1

    private fun requestCheck() {
        val cmds = mutableListOf<BleCommand>()
        //记录片区清空后，将不能读数据
        deviceType?.zones?.filter { !it.log }?.forEach {
            val cmdInfo = CMDInfo(CMDType.READ, it.location.toInt(16), it.items.size)
            val cmd = ModbusKit.buildReadCMD(cmdInfo)
            val bleCommand = BleCommand(cmd, BleCommand.DEFAULT_REPEAT)
            cmds.add(bleCommand)
        }
        checkSize = cmds.size
        BluetoothKit.executeCMDList(cmds)
    }

    //核对设置结果
    private fun checkConfigSuccess(): Boolean = tagList.all { it.state == TagConfigState.CHECKED }

}