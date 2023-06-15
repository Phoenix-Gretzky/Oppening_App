package com.example.openingapp.NetworkCalls.ApiInterface

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiInterfaceForCalls {


    @GET("dashboardNew")
     fun getLinksData(@Header("Authorization") access_token : String): Call<ResponseBody>


}