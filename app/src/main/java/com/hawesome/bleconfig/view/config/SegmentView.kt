package com.hawesome.bleconfig.view.config

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.toResString
import com.hawesome.bleconfig.ext.toUnitString
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.DeviceCell
import com.hawesome.bleconfig.model.Devices
import kotlinx.android.synthetic.main.item_section.view.nameText
import kotlinx.android.synthetic.main.item_segment.view.*

class SegmentView(context: Context, cell: DeviceCell, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.item_segment, this)
        nameText.text = cell.tags.first().toResString().toUnitString()

        val bindTags = TagKit.getTags(cell.tags)
        //items:["10/A","20/B"]
        val items = cell.items
        val layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f / items.size)
        layoutParams.setMargins(1, 0, 1, 0)
        for (index in 0 until items.size) {
            val infos = items[index].split(Devices.ITEM_INFO_SEPARATOR)
            val radioButton = RadioButton(context, null, 0, R.style.RadioButton)
            radioButton.isClickable = true
            radioButton.setBackgroundResource(R.drawable.bg_radio_button)
            radioButton.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(R.dimen.font_headline)
            )
            radioButton.text = infos.last().toResString()
            radioButton.tag = infos.first()
            radioButton.layoutParams = layoutParams
            radioButton.setOnClickListener {
                val value = it.tag.toString().toInt()
                bindTags.forEach { it.update(value) }
            }
            segment.addView(radioButton)
        }
    }
}