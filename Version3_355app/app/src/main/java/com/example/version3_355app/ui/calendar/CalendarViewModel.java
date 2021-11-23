package com.example.version3_355app.ui.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CalendarView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.version3_355app.R;

import java.util.Date;

public class CalendarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CalendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}