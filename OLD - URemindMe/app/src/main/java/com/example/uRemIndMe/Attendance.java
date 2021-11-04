package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;

public class Attendance extends AppCompatActivity {

   String[] keyArray;
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recycler_attendance,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            keyArray =Global.documentData.attendance.keySet().toArray(new String[Global.documentData.attendance.size()]);

            final Global.ModalClasses.AttendanceModal data = Global.documentData.attendance.get(keyArray[position]);
            final int attended = data.attended;
            int missed = data.missed;
            int percentage = calculatePercentage(attended,missed);

            holder.attended.setText(String.valueOf(attended));
            holder.missed.setText(String.valueOf(missed));
            holder.percent.setText(String.valueOf(percentage));
            holder.subname.setText(keyArray[position].toString());
            if(percentage<75){
                holder.status.setText("Short");
            }

            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Global.documentData.attendance.get(keyArray[position]).attended++;
                    int attended = Global.documentData.attendance.get(keyArray[position]).attended;
                    Global.userRef.update("attendance."+keyArray[position]+".attended",attended);
                    int percentage = calculatePercentage(attended,Global.documentData.attendance.get(keyArray[position]).missed);
                    holder.attended.setText(String.valueOf(attended));
                    holder.percent.setText(String.valueOf(percentage));

                    if(percentage<75){
                        holder.status.setText("Short");
                    }else{
                        holder.status.setText("Safe");
                    }
                }
            });

            holder.miss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Global.documentData.attendance.get(keyArray[position]).missed++;
                    int missed = Global.documentData.attendance.get(keyArray[position]).missed;
                    Global.userRef.update("attendance."+keyArray[position]+".missed",missed);
                    holder.missed.setText(String.valueOf(missed));
                    int percentage = calculatePercentage(Global.documentData.attendance.get(keyArray[position]).attended,missed);
                    holder.percent.setText(String.valueOf(percentage));
                    if(percentage<75){
                        holder.status.setText("Short");
                    }else{
                        holder.status.setText("Safe");
                    }
                }
            });
            holder.doneminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Global.documentData.attendance.get(keyArray[position]).attended !=0){
                        Global.documentData.attendance.get(keyArray[position]).attended--;
                        int attended = Global.documentData.attendance.get(keyArray[position]).attended;
                        Global.userRef.update("attendance."+keyArray[position]+".attended",attended);
                        holder.attended.setText(String.valueOf(attended));
                        int percentage = calculatePercentage(attended,Global.documentData.attendance.get(keyArray[position]).missed);
                        holder.percent.setText(String.valueOf(percentage));
                        if(percentage<75){
                            holder.status.setText("Short");
                        }else{
                            holder.status.setText("Safe");
                        }
                    }

                }
            });

            holder.missminus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Global.documentData.attendance.get(keyArray[position]).missed != 0) {
                        Global.documentData.attendance.get(keyArray[position]).missed--;
                        int missed = Global.documentData.attendance.get(keyArray[position]).missed;
                        Global.userRef.update("attendance." + keyArray[position] + ".missed", missed);
                        holder.missed.setText(String.valueOf(missed));
                        int percentage= calculatePercentage(Global.documentData.attendance.get(keyArray[position]).attended, missed);
                        holder.percent.setText(String.valueOf(percentage));
                        if(percentage<75){
                            holder.status.setText("Short");
                        }else{
                            holder.status.setText("Safe");
                        }
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return Global.documentData.attendance.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView subname, attended,missed,percent,status;
            ImageButton done,miss,doneminus,missminus;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                subname = itemView.findViewById(R.id.recycler_attendance_subname);
                attended = itemView.findViewById(R.id.recycler_attendance_attended);
                missed = itemView.findViewById(R.id.recycler_attendance_missed);
                percent =itemView.findViewById(R.id.recycler_attendance_percentage);
                status = itemView.findViewById(R.id.recycler_attendance_status);
                done = itemView.findViewById(R.id.recycler_attendance_done);
                miss = itemView.findViewById(R.id.recycler_attendance_notdone);
                doneminus = itemView.findViewById(R.id.recycler_attendance_attended_minus);
                missminus = itemView.findViewById(R.id.recycler_attendance_missed_minus);
            }
        }
    }

    RecyclerView rv;
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        //Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.attendance_toolbar);
        setSupportActionBar(toolbar);


        if(Global.documentData.attendance== null){
            Global.documentData.attendance = new HashMap<>();
        }

        //Refrencing
        rv = findViewById(R.id.attendance_rv);

        rv.setLayoutManager(new LinearLayoutManager(Attendance.this));
        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);




    }

    int calculatePercentage(int a, int b){
        if(a == 0 && b==0){
            return 0 ;
        }
        int percentage = ((a*100)/(a+b));
        return percentage;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
