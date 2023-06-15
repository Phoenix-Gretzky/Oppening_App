package com.example.openingapp.ui.dashboard;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.openingapp.Models.BaseDataClass;

public class DashboardViewModel extends AndroidViewModel {
    private static final MutableLiveData<BaseDataClass> mutableLiveDataForLinksApi = new MutableLiveData<>();
    private final LiveData<BaseDataClass> observableLiveDataForLinksApi;

    public DashboardViewModel(@NonNull Application application) {
        super(application);

        observableLiveDataForLinksApi = getMutableLiveDataForLinksApi();


    }
    @Override
    public void onCleared() {
        mutableLiveDataForLinksApi.setValue(null);
        super.onCleared();
    }

    private LiveData<BaseDataClass> getMutableLiveDataForLinksApi() {
        return mutableLiveDataForLinksApi;
    }


    public void updateLiveDataForObservableLiveDataForLinksApi(BaseDataClass notifyRecords) {
        mutableLiveDataForLinksApi.setValue(notifyRecords);
    }

    public LiveData<BaseDataClass> getObservableLiveDataForLinksApi() {
        return observableLiveDataForLinksApi;
    }


}
