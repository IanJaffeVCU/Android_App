package com.example.uRemIndMe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class DialogAddSubject extends DialogFragment {

    ListView lv;
    TextInputEditText name;
    ImageButton done;
    ArrayAdapter<String> adapter ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_addsubject,container,false);
        //Setting up toolbar
        Toolbar toolbar = rootview.findViewById(R.id.dialog_addsubject_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Refrencing
        lv = rootview.findViewById(R.id.dialog_addsubject_lv);
        name = rootview.findViewById(R.id.dialog_addsubject_name);
        done = rootview.findViewById(R.id.dialog_addsubject_done);

        //Fetching Data

        List<String> subjects  = Global.documentData.subjects;
        if(subjects == null){
            subjects = new ArrayList<>();
        }

        //Setting up lv
        adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,subjects);
        lv.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subname = name.getText().toString();
                if(Global.documentData.subjects ==  null){
                    Global.documentData.subjects = new ArrayList<>();
                }
                Global.documentData.subjects.add(subname);
                Global.userRef.update("subjects", FieldValue.arrayUnion(subname));

                if(Global.documentData.attendance== null){
                    Global.documentData.attendance = new HashMap<>();

                }

                Global.documentData.attendance.put(subname,new Global.ModalClasses.AttendanceModal(0,0));


                Global.userRef.update("attendance."+subname,new Global.ModalClasses.AttendanceModal(0,0));

                adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Global.documentData.subjects);
                lv.setAdapter(adapter);

                name.setText("");
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Do you want to delete this subject");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Global.userRef.update("subjects",FieldValue.arrayRemove(Global.documentData.subjects.get(position)));
                        String delted =Global.documentData.subjects.get(position);
                        Global.documentData.subjects.remove(position);

                        updateData(delted);

                        adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Global.documentData.subjects);
                        lv.setAdapter(adapter);
                    }
                });
                builder.show();

                return false;
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

    void updateData(String deleted){

        for(String day: Global.documentData.timetable.keySet()){
            int i =0  ;
            for(Global.ModalClasses.TimeTableModal modal: Global.documentData.timetable.get(day)){
                if(modal.subname.equals(deleted)){
                    modal.subname = "No Subject";
                    Global.documentData.timetable.get(day).set(i,modal);
                }

                TimeTable.adapter.notifyDataSetChanged();

                i++;
            }
        }

        Global.documentData.attendance.remove(deleted);
        Global.userRef.update("attendance",Global.documentData.attendance);

    }
}
