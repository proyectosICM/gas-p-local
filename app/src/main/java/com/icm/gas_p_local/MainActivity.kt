package com.icm.gas_p_local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icm.gas_p_local.common.WindowFocusHandler
import com.icm.gas_p_local.data.LoadDeviceStorageManager
import com.icm.gas_p_local.utils.ESP32ConnectionManager

class MainActivity : AppCompatActivity() {

    private lateinit var llDevicesContent: LinearLayout
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var isConnectedTextView: TextView
    private lateinit var ipRouterTextView: TextView

    private var connectionManager: ESP32ConnectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()

        initializeNetworkChangeReceiver()

        registerNetworkChangeReceiver()

        loadAndDisplayDevices()
    }

    private fun initializeUI() {
        val btnAddDevice = findViewById<Button>(R.id.btnAddDevice)
        llDevicesContent = findViewById(R.id.llDevicesContent)
        isConnectedTextView = findViewById(R.id.isConnected)
        ipRouterTextView = findViewById(R.id.ipRouter)

        btnAddDevice.setOnClickListener {
            val intent = Intent(this, AddDevice::class.java)
            startActivity(intent)
        }
    }

    private fun initializeNetworkChangeReceiver() {
        networkChangeReceiver = NetworkChangeReceiver(isConnectedTextView, ipRouterTextView)
    }

    private fun registerNetworkChangeReceiver() {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, intentFilter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowFocusHandler.handleWindowFocusChanged(this, hasFocus)
    }

    private fun loadAndDisplayDevices() {
        // Carga los dispositivos desde el JSON
        val devices = LoadDeviceStorageManager.loadDevicesFromJson(this)

        // Limpia el contenedor
        llDevicesContent.removeAllViews()

        // Infla y agrega una tarjeta para cada dispositivo
        val inflater = LayoutInflater.from(this)
        for (device in devices) {
            val view = inflater.inflate(R.layout.device_item, llDevicesContent, false)
            val tvDeviceName = view.findViewById<TextView>(R.id.tvDeviceName)
            val tvDeviceIp = view.findViewById<TextView>(R.id.tvDeviceIp)
            val btnAction = view.findViewById<Button>(R.id.btnAction)

            tvDeviceName.text = device.nameDevice
            tvDeviceIp.text = device.ipDevice

            // Configura el botón de acción si es necesario
            btnAction.setOnClickListener {
                Log.d("DeviceAction", "Accionado")
                connectionManager?.disconnect()
                connectionManager = ESP32ConnectionManager(tvDeviceIp.text.toString(), 82)
                connectionManager?.connect { isConnected ->
                    connectionManager?.sendMessage("activate")
                   // connectionManager?.sendMessage("disconnect")
                }
            }

            llDevicesContent.addView(view)
        }
    }
}
