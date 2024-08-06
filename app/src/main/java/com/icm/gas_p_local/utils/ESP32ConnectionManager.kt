package com.icm.gas_p_local.utils

import ReceiveMessageTask
import SendMessageTask
import android.os.AsyncTask
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

class ESP32ConnectionManager(private val serverIp: String, private val port: Int) {
    private var socket: Socket? = null
    private var outputStream: OutputStream? = null
    private var reader: BufferedReader? = null
    private var messageCallback: ((String?) -> Unit)? = null

    fun connect(callback: (Boolean) -> Unit) {
        ConnectTask(callback).execute()
    }

    fun disconnect() {
        try {
            sendMessage("disconnect")
            socket?.close()
            Log.d("mens", "Desconectado del servidor")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(message: String) {
        if (outputStream != null) {
            SendMessageTask(outputStream!!).execute(message)
            // Start receiving a response after sending a message
            receiveMessage { response ->
                // Handle the response
                messageCallback?.invoke(response)
                messageCallback = null
            }
        }
    }

    fun receiveMessage(callback: (String?) -> Unit) {
        if (reader != null) {
            messageCallback = callback
            ReceiveMessageTask(reader!!, callback).execute()
        }
    }

    private inner class ConnectTask(val callback: (Boolean) -> Unit) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                socket = Socket(serverIp, port)
                outputStream = socket?.getOutputStream()
                reader = BufferedReader(InputStreamReader(socket?.getInputStream()))
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(result: Boolean) {
            if (result) {
                sendMessage("isConnected")
                Log.d("mens", "Conectado al servidor")
            }
            callback(result)
        }
    }
}
