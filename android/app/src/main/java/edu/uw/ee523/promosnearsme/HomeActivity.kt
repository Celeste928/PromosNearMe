package edu.uw.ee523.promosnearsme

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleReadCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException
import com.clj.fastble.scan.BleScanRuleConfig
import com.squareup.picasso.Picasso

class HomeActivity : BaseActivity() {

    val SERVICE_UUID = "bb7c542c-e06e-11ec-9d64-0242ac120002"
    val SELLER_CHAR_ID = "f2fdd1c8-e06e-11ec-9d64-0242ac120002"

    var linearLayout: LinearLayout? = null
    var noPromoText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_home)

        initViews()

        initBle()
        scanBle()
    }

    private fun initViews() {
        noPromoText = findViewById(R.id.NoPromotionText)
        linearLayout = findViewById(R.id.PromosLayout)
    }

    private fun initBle() {
        BleManager.getInstance().init(application)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(10000).operateTimeout = 5000

        val scanRuleConfig = BleScanRuleConfig.Builder()
            .setScanTimeOut(10000)
            .build()
        BleManager.getInstance().initScanRule(scanRuleConfig)
    }

    private fun scanBle() {
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.i("TAG", "Scan started")
            }
            @SuppressLint("MissingPermission")
            override fun onScanning(bleDevice: BleDevice) {
                Log.i("TAG", "Found device ${bleDevice.name}")
            }
            override fun onScanFinished(scanResultList: List<BleDevice>) {
                Log.i("TAG", "Scan count: ${scanResultList.size}")
                handleDiscoveredDevices(scanResultList.filter { bleDev -> bleDev.name != null && bleDev.name.contains("PromosNearMe") })
            }
        })
    }

    private fun handleDiscoveredDevices(devices: List<BleDevice>) {
        Log.i("TAG", "Found ${devices.size} devices for PromosNearMe")
        for (device: BleDevice in devices) {
            BleManager.getInstance().connect(device.mac, object : BleGattCallback() {
                override fun onStartConnect() {}
                override fun onConnectFail(bleDevice: BleDevice, exception: BleException) {
                    Log.e("TAG", exception.description)
                }

                override fun onConnectSuccess(
                    bleDevice: BleDevice,
                    gatt: BluetoothGatt,
                    status: Int
                ) {
                    Log.i("TAG", "Connected to ${bleDevice.name}")
                    BleManager.getInstance().read(
                        bleDevice,
                        SERVICE_UUID,
                        SELLER_CHAR_ID,
                        object : BleReadCallback() {
                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun onReadSuccess(data: ByteArray) {
                                handleSeller(String(data))
                                BleManager.getInstance().disconnect(bleDevice)
                            }

                            override fun onReadFailure(exception: BleException) {
                                Log.e("TAG", "Read failed, please try again $exception")
                            }
                        })
                }

                override fun onDisConnected(
                    isActiveDisConnected: Boolean,
                    bleDevice: BleDevice,
                    gatt: BluetoothGatt,
                    status: Int
                ) {
                    Log.i("TAG", "Disconnected")
                }
            })

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleSeller(sellerId: String) {
        Log.i("TAG", "Got seller Id: ${sellerId}")

        val poster: ImageView = ImageView(this)
        Picasso.get()
            .load("https://i.ytimg.com/vi/y2Ea1-5jj48/maxresdefault.jpg")
            .into(poster)
        poster.scaleType = ImageView.ScaleType.FIT_XY
        poster.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }

        linearLayout!!.addView(poster)
        noPromoText!!.visibility = View.INVISIBLE
        Log.i("TAG", "Done")

    }
}