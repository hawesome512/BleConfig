package com.hawesome.bleconfig.view.config

import android.Manifest
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.clj.fastble.data.BleDevice
import com.hawesome.bleconfig.R
import com.hawesome.bleconfig.ext.showToast
import com.hawesome.bleconfig.kit.BluetoothKit
import com.hawesome.bleconfig.kit.OnBleDisConnectListener
import com.hawesome.bleconfig.kit.TagKit
import com.hawesome.bleconfig.model.CellType
import com.uuzuche.lib_zxing.activity.CaptureActivity
import com.uuzuche.lib_zxing.activity.CodeUtils
import kotlinx.android.synthetic.main.activity_config.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class ConfigActivity : AppCompatActivity(), OnScanListener, OnBleDisConnectListener {

    companion object {
        const val REQUEST_SCAN = 1
        const val TAG = "DeviceActivity"
        const val EXT_DEVICE = "device"
    }

    private var bleDevice: BleDevice? = null
    //获取扫码回调信息
    private lateinit var serialView: SerialView
    private val viewModel by lazy {
        ViewModelProvider(this).get(ConfigModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        BluetoothKit.onDisconnectListener = this
        bleDevice = intent.getParcelableExtra<BleDevice>(EXT_DEVICE)
        title = bleDevice?.name
        TagKit.getDeviceType()?.cells?.let { cells ->
            cells.forEach {
                if (it.section != null) {
                    val section = SectionView(this, it)
                    container.addView(section)
                }
                val childView = when (it.getType()) {
                    CellType.DATETIME -> DatetimeView(this, it)
                    CellType.SERIAL -> {
                        serialView = SerialView(this, it)
                        serialView
                    }
                    CellType.SEGMENT -> SegmentView(this, it)
                    CellType.REFER -> ReferView(this, it)
                }
                container.addView(childView)
            }
            val confirm = ConfirmView(this,cells)
            container.addView(confirm)
        }
        viewModel.connectResult.observe(this) {
            if (it.getOrDefault(false)) return@observe
            //重连失败，退出or重试
            AlertDialog.Builder(this).apply {
                setTitle(R.string.blue_disconnect)
                setPositiveButton(R.string.retry) { dialog, which ->
                    viewModel.connect(bleDevice!!)
                }
                setNegativeButton(R.string.exit) { dialog, which ->
                    disposeBle()
                    finish()
                }
            }.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != REQUEST_SCAN || data == null) return
        val bundle: Bundle = data.getExtras() ?: return
        if (bundle.getInt(CodeUtils.RESULT_TYPE) != CodeUtils.RESULT_SUCCESS) return
        bundle.getString(CodeUtils.RESULT_STRING)?.let { info ->
            serialView.setSerial(info)
            return
        }
        R.string.scan_fail.showToast()
    }

    override fun onScan() {
        checkCameraPermissionWithPermissionCheck()
    }

    @NeedsPermission(Manifest.permission.CAMERA, Manifest.permission.VIBRATE)
    fun checkCameraPermission() {
        val intent = Intent(this, CaptureActivity::class.java)
        startActivityForResult(intent, REQUEST_SCAN)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                disposeBle()
                finish()
            }
            else -> {}
        }
        return true
    }

    override fun onDisConnect() {
        R.string.blue_reconnect.showToast()
        viewModel.connect(bleDevice!!)

    }

    override fun onDestroy() {
        disposeBle()
        super.onDestroy()
    }

    private fun disposeBle() {
        BluetoothKit.onDisconnectListener = null
        TagKit.onDisconnected()
        bleDevice?.let { BluetoothKit.disConnect(it) }
        bleDevice = null
    }
}