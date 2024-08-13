package com.icm.gas_p_local

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.icm.gas_p_local.common.WindowFocusHandler
import com.icm.gas_p_local.data.api.DeviceModelData
import com.icm.gas_p_local.data.api.JsonPlaceHolderApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainRemoteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_remote)

        recyclerView = findViewById(R.id.recyclerViewDevices)

        // Asignar un LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        getPost()
    }

    private fun getPost() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://samloto.com:4015/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi::class.java)
        val call = jsonPlaceHolderApi.getDevices()

        call.enqueue(object : Callback<List<DeviceModelData>> {
            override fun onResponse(call: Call<List<DeviceModelData>>, response: Response<List<DeviceModelData>>) {
                if (!response.isSuccessful) {
                    Log.e("Panel1Activity", "Response not successful: ${response.errorBody()?.string()}")
                    return
                }

                val devices = response.body()
                if (devices != null) {
                    deviceAdapter = DeviceAdapter(this@MainRemoteActivity, devices)
                    recyclerView.adapter = deviceAdapter
                    val jsonResponse = Gson().toJson(devices)
                    Log.d("Panel1Activity", "Posts loaded: ${jsonResponse}")
                } else {
                    Log.e("Panel1Activity", "No posts received")
                }
            }

            override fun onFailure(call: Call<List<DeviceModelData>>, t: Throwable) {
                Log.e("Panel1Activity", "API call failed: ${t.message}")
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        WindowFocusHandler.handleWindowFocusChanged(this, hasFocus)
    }
}
