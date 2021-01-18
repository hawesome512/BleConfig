package com.hawesome.bleconfig.view.config

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.toDateTimeFormatter
import com.hawesome.bleconfig.ext.toResString
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.DeviceCell
import kotlinx.android.synthetic.main.item_datetime.view.*
import kotlinx.android.synthetic.main.item_refer.view.icon
import kotlinx.android.synthetic.main.item_section.view.nameText
import java.time.LocalDateTime
import java.util.*
import kotlin.concurrent.schedule

class DatetimeView(context: Context, cell: DeviceCell, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    companion object {
        const val SHOW_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"
    }

    private var autoTime: Boolean = false
    private var timerTask: TimerTask? = null
    private val bindTags = TagKit.getTags(cell.tags)

    init {
        inflate(context, R.layout.item_datetime, this)
        nameText.text = cell.type.toResString()
        setOnClickListener {
            autoTime = !autoTime
            val tintID = if (autoTime) R.color.red_500 else R.color.blue_500
            icon.setColorFilter(context.getColor(tintID))
            if (autoTime) {
                timerTask = Timer().schedule(0, 1000) {
                    val now = LocalDateTime.now()
                    //BCD码
                    bindTags[0].update(now.format("yyyy".toDateTimeFormatter()).toInt(16))
                    bindTags[1].update(now.format("MMdd".toDateTimeFormatter()).toInt(16))
                    bindTags[2].update(now.format("yyMM".toDateTimeFormatter()).toInt(16))
                    bindTags[3].update(now.format("ddHH".toDateTimeFormatter()).toInt(16))
                    bindTags[4].update(now.format("mmss".toDateTimeFormatter()).toInt(16))
                    (context as AppCompatActivity).runOnUiThread {
                        valueText.text = now.format(SHOW_DATETIME_PATTERN.toDateTimeFormatter())
                        valueText.setBackgroundResource(R.color.red_500)
                    }
                }
            } else {
                timerTask?.cancel()
            }
        }
    }
}