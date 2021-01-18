package com.bertalabs.baroflight.ui.front;

import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FrontViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private Button hButton;

    public FrontViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}