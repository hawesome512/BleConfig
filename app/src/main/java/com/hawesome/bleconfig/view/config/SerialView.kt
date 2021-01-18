package com.hawesome.bleconfig.view.config

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.toResString
import com.hawesome.bleconfig.kit.BluetoothKit
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.DeviceCell
import kotlinx.android.synthetic.main.item_datetime.view.*

interface OnScanListener {
    fun onScan()
}

class SerialView(context: Context, cell: DeviceCell, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    val bindTags = TagKit.getTags(cell.tags)

    init {
        inflate(context, R.layout.item_datetime, this)
        nameText.text = cell.type.toResString()
        icon.setImageResource(R.drawable.ic_qrcode)
        setOnClickListener {
            (context as OnScanListener).onScan()
        }
        //todo:调试，免扫码
//        setSerial("XST-7T/20050005")
    }

    fun setSerial(info: String) {
        val serial = BluetoothKit.getDeviceSerial(info)
        valueText.text = serial
        valueText.setBackgroundResource(R.color.red_500)
        val value1 = serial.substring(0 until serial.length / 2).toInt(16)
        val value2 = serial.substring(serial.length / 2 until serial.length).toInt(16)
        bindTags[0].update(value1)
        bindTags[1].update(value2)
    }
}