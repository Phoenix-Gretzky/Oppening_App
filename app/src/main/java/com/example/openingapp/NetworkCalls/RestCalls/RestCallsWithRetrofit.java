package com.example.openingapp.NetworkCalls.RestCalls;

import android.content.Context;

import com.example.openingapp.GlobalClasses.GlobalClassForFunctions;
import com.example.openingapp.NetworkCalls.ApiInterface.ApiInterfaceForCalls;
import com.example.openingapp.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestCallsWithRetrofit {

    private String BASE_URL = "https://api.inopenapp.com/api/v1/";

    private Retrofit retrofit;
    ApiInterfaceForCalls apiInstance;

    public String bearer = "Bearer";

    private static final RestCallsWithRetrofit ourInstance = new RestCallsWithRetrofit();
    RestCallsWithRetrofit() {

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client ( new OkHttpClient( ).newBuilder ( )
                        .connectTimeout ( 120 , TimeUnit.SECONDS )
                        .readTimeout ( 120 , TimeUnit.SECONDS )
                        .writeTimeout ( 120 , TimeUnit.SECONDS )
                        .build ( ) )
                .build();

        apiInstance = retrofit .create(ApiInterfaceForCalls.class);
    }

    public static RestCallsWithRetrofit getInstance() {
        return ourInstance;
    }



    public String GetLinksRestCall(Context mContext) {
        if (!GlobalClassForFunctions.getInstance().IsConnected()) {

            GlobalClassForFunctions.getInstance().showAlertdialog("Connectivity Issue", mContext.getResources().getString(R.string.connectivity_issue), mContext, true);

            return "";
        }
        String responseBody = "";
        if (!GlobalClassForFunctions.getInstance().getAccessToken().trim().isEmpty()) {

            try {

              System.out.println("Header =" + "Bearer " + GlobalClassForFunctions.getInstance().getAccessToken());
                //call the getLocationArrays API and send json data
                Call<ResponseBody> call = getInstance().apiInstance.getLinksData(getInstance().bearer + " " + GlobalClassForFunctions.getInstance().getAccessToken());
                //res.code() == 200 &&
                //execute the API synchronously
                Response<ResponseBody> res = call.execute();
              System.out.println(res);
//                responseBody = GlobalClassForFunctions.getInstance().executeApi(res);

                responseBody=res.body().string();

            } catch (Exception ex) {
              System.out.println("Catch For GetLinksRestCall");
              System.out.println(ex.getMessage());
                // show toast message if error occurs
                // ShowToast();
            }

        }
        return responseBody;
    }

}




