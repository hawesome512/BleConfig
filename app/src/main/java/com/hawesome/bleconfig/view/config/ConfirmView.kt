package com.hawesome.bleconfig.view.config

import android.content.Context
import android.content.DialogInterface
import android.util.AttributeSet
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MediatorLiveData
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.CellType
import com.hawesome.bleconfig.model.DeviceCell
import com.hawesome.bleconfig.model.Tag
import kotlinx.android.synthetic.main.item_button.view.*
import java.time.LocalDateTime
import kotlin.math.roundToInt

class ConfirmView(context: Context, cells: List<DeviceCell>, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.item_button, this)
        val tagNames = cells.map { it.tags.first() }
        val tags = TagKit.getTags(tagNames)
        //时间日期【点】实时更新，只绑定一次到订阅事件中
        val dateTagName = cells.first { it.getType() == CellType.DATETIME }.tags.first()
        val dateTag = tags.first { it.name == dateTagName }
        val tagsLiveData = MediatorLiveData<String>()
        tags.forEach { tag ->
            tagsLiveData.addSource(tag.valueLiveData) { value ->
                tagsLiveData.postValue(tag.name)
            }
        }
        progressBar.max = tags.size
        val activity = context as FragmentActivity
        tagsLiveData.observe(activity) { tagName ->
            val progress = tags.filter { it.value != Tag.NULL_VALUE }.size
            progressBar.progress = progress
            if (progress == tags.size) {
                confirmBtn.visibility = VISIBLE
            }
            if (tagName == dateTagName) {
                tagsLiveData.removeSource(dateTag.valueLiveData)
            }
        }
        confirmBtn.setOnClickListener {
            TagKit.requestConfig()
            confirmBtn.visibility = INVISIBLE
            progressBar.max = 100
            progressBar.progress = 0
        }
        TagKit.configProcessLiveData.observe(activity) {
            progressBar.progress += (progressBar.max * it).roundToInt()
        }
        TagKit.configResultLiveData.observe(activity) {
            if (it == null) return@observe
            confirmBtn.visibility = VISIBLE
            val title = if (it) R.string.check_success_title else R.string.check_fail_title
            val content = if (it) R.string.check_success_content else R.string.check_fail_content
            AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setCancelable(true)
                .setNegativeButton(R.string.confirm) { dialogInterface: DialogInterface, i: Int -> }
                .show()
        }
    }
}