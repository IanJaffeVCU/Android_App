package com.example.uRemIndMe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class DialogMakePoll extends DialogFragment {

    TextInputEditText subject, body;
    Button done;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.dialog_add_poll,container,false);

        //Toolbar
        Toolbar toolbar = rootview.findViewById(R.id.dialog_add_poll_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Refrencing

        subject = rootview.findViewById(R.id.dialog_add_poll_subject);
        body = rootview.findViewById(R.id.dialog_add_poll_body);
        done = rootview.findViewById(R.id.dialog_add_poll_submit);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.polls.add(new Global.ModalClasses.PollModal(subject.getText().toString()
                ,body.getText().toString(),Global.documentData.userInfo.name,0,0,System.currentTimeMillis()));
                Global.pollRef.update(Global.documentData.userInfo.batch.toString(),Global.polls);
                Toast.makeText(getActivity(), "Poll Successfully added", Toast.LENGTH_SHORT).show();
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
