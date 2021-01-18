package com.hawesome.bleconfig.model

import com.google.gson.Gson
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.view.BCApplication

/*
* 设备模型
* */
data class Devices(val types: List<DeviceType>) {
    companion object {
        const val ITEM_INFO_SEPARATOR = "/"
        const val NAME_VALUE_SEPARATOR = ":"

        //设备模型
        val model by lazy {
            val inputText =
                BCApplication.context.resources.openRawResource(R.raw.device).bufferedReader()
                    .use { it.readText() }
            val gson = Gson()
            gson.fromJson(inputText, Devices::class.java)
        }
    }
}

data class DeviceType(
    val name: String,
    val type: String,
    val cells: List<DeviceCell>,
    val zones: List<Address>
)

data class Address(val location: String, val items: List<String>, val log: Boolean) {

    fun getItemNames() = items.map { it.split(Devices.NAME_VALUE_SEPARATOR).first() }
}

data class DeviceCell(
    val type: String,
    val section: String?,
    val tags: List<String>,
    val items: List<String>,
    val unit: String?
) {
    fun getType() = CellType.valueOf(type.toUpperCase())
}

enum class CellType {
    DATETIME, SERIAL, REFER, SEGMENT
}