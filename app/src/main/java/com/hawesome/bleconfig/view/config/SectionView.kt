package com.hawesome.bleconfig.view.config

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.toResString
import com.hawesome.bleconfig.model.DeviceCell
import kotlinx.android.synthetic.main.item_section.view.*

class SectionView(context: Context, cell: DeviceCell, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.item_section, this)
        nameText.text = cell.section?.toResString()
        nameText.visibility =  if (cell.section.isNullOrEmpty()) GONE else VISIBLE
    }
}