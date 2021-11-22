package com.example.version3_355app.ui.calendar;

import android.content.ClipData;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.version3_355app.R;
import com.example.version3_355app.databinding.FragmentCalendarBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarFragment extends Fragment {

    private CalendarViewModel calendarViewModel;
    private FragmentCalendarBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // By ID we can use each component
        // which id is assign in xml file
        // use findViewById() to get the
        // CalendarView and TextView
        CalendarView calendar  = (CalendarView)root.findViewById(R.id.calendar);
        TextView date_view = (TextView)root.findViewById(R.id.text_calendar);

        // Add Listener in calendar
        calendar
                .setOnDateChangeListener(
                        new CalendarView
                                .OnDateChangeListener() {
                            @Override

                            // In this Listener have one method
                            // and in this method we will
                            // get the value of DAYS, MONTH, YEARS
                            public void onSelectedDayChange(
                                    @NonNull CalendarView view,
                                    int year,
                                    int month,
                                    int dayOfMonth)
                            {

                                // Store the value of date with
                                // format in String type Variable
                                // Add 1 in month because month
                                // index is start with 0

                                //TEMPORARY PURPOSES

                                HashMap<String, String> assignments= new HashMap<String,String>();
                                assignments.put("11/22/2021","hw1\nhw2 \nhw4");
                                assignments.put("11/27/2021","Hw6");
                                assignments.put("11/23/2021","Hw3");
                                assignments.put("11/25/2021","Hw7");
                                assignments.put("11/20/2021","Hw10\nhw13");
                                assignments.put("11/19/2021","Hw2 \nhw15");
                                assignments.put("11/03/2021","Hw4");
                                assignments.put("12/20/2021","Hw5");
                                assignments.put("12/10/2021","Hw0");

                                //on click date is stored
                                String Date
                                        = (month + 1) + "/"
                                        + dayOfMonth + "/" + year;

                                // set this date in TextView for Display
                                date_view.setText(assignments.get(Date));
                            }
                        });
        final TextView textView = binding.textCalendar;
        calendarViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}