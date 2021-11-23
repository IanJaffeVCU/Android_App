package com.example.uRemIndMe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class DialogAboutUs extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_aboutus,container,false);

        Toolbar toolbar = rootview.findViewById(R.id.dialog_aboutus_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return rootview;
    }

    @Override
    public void onStart() {
        super.onStart();
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setLayout(height,width);
    }
}
