package com.icm.gas_p_local

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var isConnectedTextView: TextView
    private lateinit var ipRouterTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeUI()

        initializeNetworkChangeReceiver()

        registerNetworkChangeReceiver()
    }

    private fun initializeUI() {
        val btnAddDevice = findViewById<Button>(R.id.btnAddDevice)
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
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
    }
}
