package com.bertalabs.baroflight.ui.fill;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FillViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FillViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }


    public LiveData<String> getText() {
        return mText;
    }
}