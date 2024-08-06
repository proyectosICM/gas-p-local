package com.icm.gas_p_local

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icm.gas_p_local.common.KeyboardUtils
import com.icm.gas_p_local.common.WindowFocusHandler
import com.icm.gas_p_local.utils.ESP32ConnectionManager
import com.icm.gas_p_local.utils.NameDeviceExtractor
import com.icm.gas_p_local.utils.NetworkUtils

class AddDevice : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var btnTest: Button
    private lateinit var btnAdd: Button
    private lateinit var btnBack: Button
    private lateinit var etIpAddress: EditText
    private lateinit var tvValidationMessage: TextView
    private lateinit var deviceNameData: TextView

    private var connectionManager: ESP32ConnectionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        progressBar = findViewById(R.id.progressBar)
        btnTest = findViewById(R.id.btnTest)
        btnAdd = findViewById(R.id.btnAdd)
        btnBack = findViewById(R.id.btnBack)
        etIpAddress = findViewById(R.id.etIpAddress)
        tvValidationMessage = findViewById(R.id.tvValidationMessage)
        deviceNameData = findViewById(R.id.deviceNameData)

        btnTest.setOnClickListener {
            // Hide the keyboard
            KeyboardUtils.hideKeyboard(this)

            // Clear previous validation message and button
            tvValidationMessage.text = ""
            tvValidationMessage.setTextColor(resources.getColor(R.color.white, null)) // Color por defecto
            btnAdd.visibility = View.GONE // Ocultar el botón Agregar

            val ipAddress = etIpAddress.text.toString()
            if (ipAddress.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                simulateLongOperation(ipAddress)
            } else {
                tvValidationMessage.text = "Ingrese una IP válida."
                tvValidationMessage.setTextColor(resources.getColor(R.color.colorError, null)) // Color rojo
            }
        }

        btnAdd.setOnClickListener {
            // Aquí puedes agregar la lógica para manejar la acción del botón Agregar
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun simulateLongOperation(ipAddress: String) {
        Handler().postDelayed({
            progressBar.visibility = View.GONE
            val routerIp = NetworkUtils.getRouterIpAddress(this)
            if (isIpInSameNetwork(ipAddress, routerIp)) {
                connectionManager?.disconnect()
                Log.d("Respuesta", "d")
                connectionManager = ESP32ConnectionManager(ipAddress, 82)
                connectionManager?.connect { isConnected ->
                    Log.d("Respuesta", "d2")
                    if (isConnected) {
                        // Enviar el primer mensaje
                        connectionManager?.sendMessage("getName")

                        // Esperar la respuesta
                        connectionManager?.receiveMessage { response ->
                                tvValidationMessage.text = "Dispositivo conectado correctamente."
                                tvValidationMessage.setTextColor(resources.getColor(R.color.colorSuccess, null))
                                val name = NameDeviceExtractor.extractName(response)
                                Log.d("Respuesta", "$response")
                                Log.d("Nombre", "$name")
                                deviceNameData.text = "Nombre del dispositivo: $name"
                                btnAdd.visibility = View.VISIBLE

                        }
                    } else {
                        tvValidationMessage.text = "No se pudo conectar al dispositivo."
                        tvValidationMessage.setTextColor(resources.getColor(R.color.colorError, null))
                    }
                }
            } else {
                tvValidationMessage.text = "IP no válida dentro de la red."
                tvValidationMessage.setTextColor(resources.getColor(R.color.colorError, null))
            }
        }, 3000) // Retraso de 3 segundos (3000 milisegundos)
    }

    private fun isIpInSameNetwork(ipAddress: String, routerIp: String): Boolean {
        // Convert IP addresses to integer values
        val ipParts = ipAddress.split(".").map { it.toInt() }
        val routerIpParts = routerIp.split(".").map { it.toInt() }
        return if (ipParts.size == 4 && routerIpParts.size == 4) {
            // Check if IP is in the same subnet as the router IP
            ipParts[0] == routerIpParts[0] && ipParts[1] == routerIpParts[1] && ipParts[2] == routerIpParts[2]
        } else {
            false
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowFocusHandler.handleWindowFocusChanged(this, hasFocus)
    }
}
