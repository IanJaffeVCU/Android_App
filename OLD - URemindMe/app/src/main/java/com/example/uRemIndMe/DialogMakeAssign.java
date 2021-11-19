package com.example.uRemIndMe;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class DialogMakeAssign extends DialogFragment {

    Spinner subject;
    TextView dateshow,timeshow;
    EditText detials;
    ImageButton submit,time,date;
    ProgressBar pb;
    Integer subid = -1;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.dialog_add_assignment,container,false);

        //Setting up the toolbar
        Toolbar toolbar = rootview.findViewById(R.id.dialog_add_assignment_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //Refrencing
        subject = rootview.findViewById(R.id.dialog_add_assignment_subject);
        detials = rootview.findViewById(R.id.dialog_add_assignment_details);
        dateshow = rootview.findViewById(R.id.dialog_add_assignment_date_show);
        timeshow = rootview.findViewById(R.id.dialog_add_assignment_time_show);
        subject = rootview.findViewById(R.id.dialog_add_assignment_subject);
        time = rootview.findViewById(R.id.dialog_add_assign_time);
        date = rootview.findViewById(R.id.dialog_add_assign_date);
        submit = rootview.findViewById(R.id.dialog_add_assignment_submit);
        pb = rootview.findViewById(R.id.dialog_add_assign_pb);
        calendar = Calendar.getInstance();

        //Setting up the spinner
        if(Global.documentData.subjects != null){


            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,Global.documentData.subjects);
            subject.setAdapter(adapter);
            subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subid = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }





        //Setting up the timedialog

        time.setOnClickListener(timeOnClick);
        date.setOnClickListener(dateOnClick);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty()){
                    Toast.makeText(getActivity(), "Please fill all the entries", Toast.LENGTH_SHORT).show();
                }else{
                    uploadData();
                    pb.setVisibility(View.VISIBLE);
                }
            }
        });




        return rootview;
    }

    Calendar calendar;

    public void setupNotifications(){
        Intent i = new Intent(getActivity(),Notifications.class);
        AlarmManager alarmmanager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);                      //to run the alarm even if we close the app
        alarmmanager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis()-System.currentTimeMillis() , PendingIntent.getBroadcast(getActivity().getBaseContext(), 1,i, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private  void uploadData(){
        String sub = Global.documentData.subjects.get(subid);
        Long timestamp = calendar.getTimeInMillis();
        Global.ModalClasses.AssignmentModal data = new Global.ModalClasses.AssignmentModal(detials.getText().toString(),timestamp,sub);


        setupNotifications();

        if(Global.documentData.assignment == null){
            Global.documentData.assignment = new ArrayList<>();
            Global.documentData.assignment.add(data);
        }else{
            Global.documentData.assignment.add(data);
            BasicFunctions.SortAssignmentData();
        }


        Global.userRef.update("assignment",Global.documentData.assignment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pb.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Assignment Successfully Added", Toast.LENGTH_SHORT).show();
                FragmentReminders.adapter.notifyDataSetChanged();
            }
        });

        //Make furthur changes
    }

    private View.OnClickListener timeOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar cal = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar.set(Calendar.HOUR, hourOfDay);
                    calendar.set(Calendar.MINUTE,minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                    timeshow.setText(sdf.format(calendar.getTime()));
                }
            },cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE),false);

            dialog.show();
        }
    };

    private  View.OnClickListener dateOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, (int) System.currentTimeMillis());
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH , month);
                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
                    dateshow.setText(sdf.format(calendar.getTime()));
                }
            },cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR));

            dialog.getDatePicker().setMinDate(System.currentTimeMillis());

            dialog.show();


        }
    };

    private boolean isEmpty(){
        if(timeshow.getText().toString().equals("")){
            return true;
        }else if(dateshow.getText().toString().equals("")){
            return  true;
        }else if(subid == -1){
            return true;
        }

        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        int height = ViewGroup.LayoutParams.MATCH_PARENT;
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setLayout(height,width);

    }
}
