package com.icm.gas_p_local

import ReceiveMessageTask
import SendMessageTask
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icm.gas_p_local.utils.NetworkUtils

class AddDevice : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var btnTest: Button
    private lateinit var btnAdd: Button
    private lateinit var btnBack: Button
    private lateinit var etIpAddress: EditText
    private lateinit var tvValidationMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_device)

        progressBar = findViewById(R.id.progressBar)
        btnTest = findViewById(R.id.btnTest)
        btnAdd = findViewById(R.id.btnAdd)
        btnBack = findViewById(R.id.btnBack)
        etIpAddress = findViewById(R.id.etIpAddress)
        tvValidationMessage = findViewById(R.id.tvValidationMessage)

        btnTest.setOnClickListener {
            // Hide the keyboard
            hideKeyboard()

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
                tvValidationMessage.text = "IP válida dentro de la red."
                tvValidationMessage.setTextColor(resources.getColor(R.color.colorSuccess, null))

                // Enviar mensaje "isConnected" al dispositivo
                SendMessageTask(ipAddress, 82).execute("isConnected")

                // Recibir respuesta del dispositivo
                ReceiveMessageTask(ipAddress, 82) { response ->
                    if (response == "connect") {
                        tvValidationMessage.text = "Dispositivo conectado correctamente."
                        tvValidationMessage.setTextColor(resources.getColor(R.color.colorSuccess, null))
                        btnAdd.visibility = View.VISIBLE
                    } else {
                        tvValidationMessage.text = "El dispositivo no ha respondido."
                        tvValidationMessage.setTextColor(resources.getColor(R.color.colorError, null))
                    }
                }.execute()

            } else {
                tvValidationMessage.text = "IP no válida dentro de la red."
                tvValidationMessage.setTextColor(resources.getColor(R.color.colorError, null))
            }
        }, 3000)
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

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }


}
