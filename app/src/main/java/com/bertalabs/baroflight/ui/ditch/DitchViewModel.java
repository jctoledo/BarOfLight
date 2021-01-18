package com.bertalabs.baroflight.ui.ditch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DitchViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DitchViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}