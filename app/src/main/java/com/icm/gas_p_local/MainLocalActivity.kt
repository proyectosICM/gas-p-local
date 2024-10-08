package com.icm.gas_p_local

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.icm.gas_p_local.common.WindowFocusHandler
import com.icm.gas_p_local.data.DeleteDeviceStorageManager
import com.icm.gas_p_local.data.LoadDeviceStorageManager
import com.icm.gas_p_local.utils.ESP32ConnectionManager

class MainLocalActivity : AppCompatActivity() {

    private lateinit var llDevicesContent: LinearLayout
    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var isConnectedTextView: TextView
    private lateinit var ipRouterTextView: TextView

    private var connectionManager: ESP32ConnectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_local)

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
            startActivityForResult(intent, ADD_DEVICE_REQUEST_CODE)
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
            val btnDeleteDevice = view.findViewById<Button>(R.id.btnDeleteDevice)

            tvDeviceName.text = device.nameDevice
            tvDeviceIp.text = device.ipDevice

            // Configura el botón de acción si es necesario
            btnAction.setOnClickListener {
                Log.d("DeviceAction", "Accionado")
                connectionManager?.disconnect()
                connectionManager = ESP32ConnectionManager(tvDeviceIp.text.toString(), 82)
                connectionManager?.connect { isConnected ->
                    if (isConnected) {
                        connectionManager?.sendMessage("activate")
                        runOnUiThread {
                            Toast.makeText(this, "Dispositivo activado", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "No se pudo conectar al dispositivo",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            btnDeleteDevice.setOnClickListener{
                showDeleteConfirmationDialog(device.nameDevice) {
                    val result = DeleteDeviceStorageManager.deleteDeviceFromJson(this, device.nameDevice)
                    if (result) {
                        loadAndDisplayDevices() // Recargar la lista de dispositivos después de la eliminación
                    } else {
                        Log.d("DeviceDeletion", "No se pudo eliminar el dispositivo")
                    }
                }
            }

            llDevicesContent.addView(view)
        }
    }

    private fun showDeleteConfirmationDialog(deviceName: String, onConfirm: () -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar eliminación")
        builder.setMessage("¿Estás seguro de que deseas eliminar el dispositivo '$deviceName'?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            onConfirm()
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_DEVICE_REQUEST_CODE && resultCode == RESULT_OK) {
            loadAndDisplayDevices() // Recargar la lista de dispositivos después de agregar uno nuevo
        }
    }

    companion object {
        private const val ADD_DEVICE_REQUEST_CODE = 1
    }
}
