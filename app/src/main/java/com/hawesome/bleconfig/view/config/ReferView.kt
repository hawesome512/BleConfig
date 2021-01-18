package com.hawesome.bleconfig.view.config

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.SpinnerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.toResString
import com.hawesome.bleconfig.ext.toUnitString
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.DeviceCell
import com.hawesome.bleconfig.model.Devices
import kotlinx.android.synthetic.main.item_refer.view.*
import kotlinx.android.synthetic.main.item_section.view.nameText
import kotlinx.android.synthetic.main.spinner_item.view.*

class ReferView(context: Context, cell: DeviceCell, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs), AdapterView.OnItemSelectedListener {

    val referItems = mutableListOf<String>()
    val bindTags = TagKit.getTags(cell.tags)

    init {
        inflate(context, R.layout.item_refer, this)
        nameText.text = cell.tags.first().toResString().toUnitString()
        val referTag = TagKit.getTags(listOf(cell.unit!!)).first()
        //items:[100:A/B/C,200:a/b/c]
        val adapter = ArrayAdapter(context, R.layout.spinner_item, referItems)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        valueSpinner.adapter = adapter
        referTag.valueLiveData.observe(context as AppCompatActivity) { referValue ->
            valueSpinner.setBackgroundResource(R.color.red_500)
            val referInfo = cell.items.first { it.contains("$referValue:") }
            referItems.clear()
            referItems.addAll(referInfo.split(":").last().split("/"))
            adapter.notifyDataSetChanged()
            val newValue = referItems[0].toInt()
            bindTags.forEach { it.update(newValue) }
        }
        valueSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val newValue = referItems[position].toInt()
        bindTags.forEach { it.update(newValue) }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}