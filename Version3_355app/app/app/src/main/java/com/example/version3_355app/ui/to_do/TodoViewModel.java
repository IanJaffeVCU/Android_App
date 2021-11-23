package com.example.version3_355app.ui.to_do;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TodoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TodoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is ToDO fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}