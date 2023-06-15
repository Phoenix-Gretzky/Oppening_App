package com.example.openingapp.NetworkCalls;

import android.content.Context;
import android.os.AsyncTask;

import com.example.openingapp.GlobalClasses.GlobalClassForFunctions;
import com.example.openingapp.Models.BaseDataClass;
import com.example.openingapp.NetworkCalls.RestCalls.RestCallsWithRetrofit;
import com.example.openingapp.R;
import com.example.openingapp.ui.dashboard.DashboardViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AsyncTasks {


    /*AsynTaskForGettingBaseData */
    public static class AsynTaskForGettingBaseData extends AsyncTask<String, Void, String> {

        Context mContext;
        boolean bIsSuccessfull;
        GlobalClassForFunctions objectForGlobalFunction;
        String Json;
        DashboardViewModel dashboardViewModel;



        public AsynTaskForGettingBaseData(Context context, DashboardViewModel dashboardViewModel) {
            this.mContext = context;
            bIsSuccessfull = false;
            this.dashboardViewModel=dashboardViewModel;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GlobalClassForFunctions.getInstance().showCustomLoadingDialog(mContext);
        }

        @Override
        protected String doInBackground(String... strings) {
            String strResponse = "";
            try {
                strResponse = RestCallsWithRetrofit.getInstance().GetLinksRestCall(mContext);
                System.out.println("Response after AsynTaskForGettingBaseData = " + strResponse);
            } catch (Exception e) {
                System.out.println("Some error occurred in AsynTaskForGettingBaseData doInBackground= " + e.getMessage());
                e.printStackTrace();
            }
            return strResponse;
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            System.out.println(" AsynTaskForGettingBaseData onPostExecute --------------" + response);
            if (response != null && !response.trim().isEmpty()) {
                try {

                    JSONObject data = new JSONObject(response);


                    if (GlobalClassForFunctions.getInstance().ReturnIfNullOrEmpty(data, "message")
                            && data.getBoolean("status")) {
                        bIsSuccessfull = true;
                        BaseDataClass baseData = new Gson()
                                .fromJson(data.toString(), new TypeToken<BaseDataClass>(){}.getType());

                        try {
                            baseData.setJsonObjectForChart(data.getJSONObject("data").getJSONObject("overall_url_chart"));
                        } catch (JSONException e) {
                            baseData.setJsonObjectForChart(null);
                            throw new RuntimeException(e);
                        }
                        dashboardViewModel.updateLiveDataForObservableLiveDataForLinksApi(baseData);


                    } else {
                        GlobalClassForFunctions.getInstance().showAlertdialog("Error", mContext.getResources().getString(R.string.error_retry), mContext);

                    }
                } catch (Exception e) {
                    // hide progress bar
                    GlobalClassForFunctions.getInstance().hideProgresDialogue(mContext);
                    System.out.println("Some error occurred " + e);
                    GlobalClassForFunctions.getInstance().showAlertdialog("Error", mContext.getResources().getString(R.string.error_occurred), mContext, true);
                }
            } else {
                GlobalClassForFunctions.getInstance().showAlertdialog("Error", mContext.getResources().getString(R.string.error_occurred), mContext, true);
            }
        }
    }


}
