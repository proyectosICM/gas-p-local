package com.icm.gas_p_local

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.icm.gas_p_local.common.WindowFocusHandler

class DeviceSelectionActivity : AppCompatActivity() {
    private lateinit var btnLocalDevices: Button
    private lateinit var btnRemoteDevices: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_selection)

        btnLocalDevices = findViewById(R.id.btnLocalDevices)
        btnRemoteDevices = findViewById(R.id.btnRemoteDevices)

        btnLocalDevices.setOnClickListener{
            val intent = Intent(this, MainLocalActivity::class.java)
            startActivity(intent)
        }

        btnRemoteDevices.setOnClickListener{
            val intent = Intent(this, MainRemoteActivity    ::class.java)
            startActivity(intent)
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowFocusHandler.handleWindowFocusChanged(this, hasFocus)
    }
}