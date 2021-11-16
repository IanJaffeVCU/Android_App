package com.example.uRemIndMe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class TimeTable extends AppCompatActivity {

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.recycler_timetable,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            Global.ModalClasses.TimeTableModal data = Global.documentData.timetable.get(daySelected.toString()).get(position);
            String[] timeinterval = getResources().getStringArray(R.array.timetable_timeinterval);
            holder.timeshow.setText(timeinterval[data.time]);
            holder.subname.setText(Global.documentData.timetable.get(daySelected.toString()).get(position).subname);

            holder.venue.setText(data.venue);

            holder.menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(TimeTable.this,holder.menu);
                    popupMenu.getMenuInflater().inflate(R.menu.timetable_recycler_menu,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            if(id == R.id.timetable_recycler_delete){
                                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTable.this);
                                builder.setTitle("Are you sure that you want to delte this class schedule?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        Global.documentData.timetable.get(daySelected.toString()).remove(position);
                                        if(Global.documentData.timetable.get(daySelected.toString()).size()!=0){
                                            BasicFunctions.SortTimeTableData(daySelected);
                                            Global.userRef.update("timetable."+daySelected,Global.documentData.timetable.get(daySelected.toString()));
                                        }else{
                                            Global.userRef.update("timetable."+daySelected,null);
                                        }


                                        adapter = new RecyclerViewAdapter();
                                        rv.setAdapter(adapter);
                                    }
                                });
                                builder.show();
                            }else if(id == R.id.timetable_recycler_subject){
                                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTable.this);
                                final String[] subject = Global.documentData.subjects.toArray(new String[0]);
                                builder.setItems(subject, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Global.documentData.timetable.get(daySelected.toString()).get(position).subname = Global.documentData.subjects.get(which);
                                        Global.userRef.update("timetable."+daySelected,Global.documentData.timetable.get(daySelected.toString()));
                                        adapter.notifyItemChanged(position);
                                    }
                                });
                                builder.show();
                            }else if(id == R.id.timetable_recycler_time){
                                AlertDialog.Builder builder = new AlertDialog.Builder(TimeTable.this);
                                builder.setTitle("Choose a time interval");
                                String[] timeIntervals = getResources().getStringArray(R.array.timetable_timeinterval);
                                builder.setItems(timeIntervals, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Global.documentData.timetable.get(daySelected.toString()).get(position).time = which;
                                        BasicFunctions.SortTimeTableData(daySelected.intValue());
                                        Global.userRef.update("timetable."+daySelected,Global.documentData.timetable.get(daySelected.toString()));
                                        adapter = new RecyclerViewAdapter();
                                        rv.setAdapter(adapter);
                                    }
                                });
                                builder.show();
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });

            ;
            holder.venue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    Global.documentData.timetable.get(daySelected.toString()).get(position).venue = s.toString();
                    Global.userRef.update("timetable."+daySelected,Global.documentData.timetable.get(daySelected.toString()));
                }
            });


        }

        @Override
        public int getItemCount() {
            if(Global.documentData.timetable.get(daySelected.toString()) == null){
                return 0;
            }
            return Global.documentData.timetable.get(daySelected.toString()).size();

        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            EditText venue;
            TextView timeshow,subname;
            ImageButton menu;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                subname = itemView.findViewById(R.id.recycler_timetable_subject);
                menu = itemView.findViewById(R.id.recycler_timetable_menu);
                venue = itemView.findViewById(R.id.recycler_timetable_venue);
                timeshow = itemView.findViewById(R.id.recycler_timetable_timeshow);

            }
        }
    }


    RecyclerView rv;
    FloatingActionButton add;
    Button addsubject;
    FirebaseFirestore db;
    public static RecyclerViewAdapter adapter;
    Integer daySelected;
    TextView dayShow;
    Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        //Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.timetable_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //Loading the data
        loadData();

        //Getting Selected day
        Date date = new Date();
        String day = new SimpleDateFormat("EEE").format(date);
        daySelected = BasicFunctions.getDayIndex(day);

        //Refrencing objects
        rv = findViewById(R.id.timetable_rv);
        add = findViewById(R.id.timetable_add);
        dayShow = findViewById(R.id.timetable_day);
        addsubject = findViewById(R.id.timetable_addsubject);

        add.setOnClickListener(addClickListener);
        addsubject.setOnClickListener(addSubjectOnClick);



        rv.setLayoutManager(new LinearLayoutManager(TimeTable.this));
        adapter = new RecyclerViewAdapter();
        rv.setAdapter(adapter);







    }

    void addEmtyObject(int i){
        List<Global.ModalClasses.TimeTableModal> list = new ArrayList<>();
        list.add(new Global.ModalClasses.TimeTableModal("No Subject","",0,0));
        Global.documentData.timetable.put(String.valueOf(i),list);
    }

    void loadData(){
        if(Global.documentData.timetable ==null){
            Global.documentData.timetable = new HashMap<>();
        }else{
            for(int i = 0 ; i<5; i++){
                if(Global.documentData.timetable.keySet().contains(String.valueOf(i))){
                    if(Global.documentData.timetable.get(String.valueOf(i))!=null){
                        BasicFunctions.SortTimeTableData(i);
                    }

                }
            }
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_weekdays,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.timetable_menu_0: daySelected  = 0 ;break;
            case R.id.timetable_menu_1: daySelected = 1; break;
            case R.id.timetable_menu_2: daySelected = 2; break;
            case R.id.timetable_menu_3 : daySelected = 3; break;
            case R.id.timetable_menu_4: daySelected = 4; break;
        }
        String[] days = getResources().getStringArray(R.array.weekdays);
        dayShow.setText(days[daySelected]);
        adapter.notifyDataSetChanged();
        return super.onOptionsItemSelected(item);
    }


    public View.OnClickListener addClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Global.ModalClasses.TimeTableModal data = new Global.ModalClasses.TimeTableModal("No Subject","",daySelected,9);
            if(Global.documentData.timetable.get(daySelected.toString()) == null){
                List<Global.ModalClasses.TimeTableModal> list = new ArrayList<>();
                list.add(data);
                Global.documentData.timetable.put(daySelected.toString(),list);
            }else{
                Global.documentData.timetable.get(daySelected.toString()).add(data);
            }

            Global.userRef.update("timetable."+daySelected,Global.documentData.timetable.get(daySelected.toString()));


            adapter.notifyDataSetChanged();
        }
    };

    public View.OnClickListener addSubjectOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogAddSubject dialog = new DialogAddSubject();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            dialog.show(ft,"hello");
        }
    };
}
