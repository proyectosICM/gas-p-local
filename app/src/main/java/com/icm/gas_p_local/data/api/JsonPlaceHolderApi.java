package com.icm.gas_p_local.data.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonPlaceHolderApi {
    @GET("devices")
    Call<List<DeviceModelData>> getDevices();
}
