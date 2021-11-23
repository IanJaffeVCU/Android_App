package com.example.uRemIndMe;

import java.util.Collections;
import java.util.Comparator;

public class BasicFunctions {

    static Integer getDayIndex(String day){
        if(day.equals("MON")){
            return 0;
        }else if(day.equals("TUE")){
            return 1;
        }else if(day.equals("WED")){
            return 2;
        }else if(day.equals("THU")){
            return  3;
        }else if(day.equals("FRI")){
            return 4;
        }else if(day.equals("SAT")){
            return 5;
        }
        return 0 ;
    }

    static void SortTimeTableData(int day){
        class SortByTime implements Comparator<Global.ModalClasses.TimeTableModal> {

            @Override
            public int compare(Global.ModalClasses.TimeTableModal o1, Global.ModalClasses.TimeTableModal o2) {
                return o1.time - o2.time;
            }
        }


        Collections.sort(Global.documentData.timetable.get(String.valueOf(day)),new SortByTime());
    }

    static void SortAssignmentData(){
        class SortByTime implements Comparator<Global.ModalClasses.AssignmentModal>{

            @Override
            public int compare(Global.ModalClasses.AssignmentModal o1, Global.ModalClasses.AssignmentModal o2) {
                return (int) (o2.timestamp - o1.timestamp);
            }
        }

        Collections.sort(Global.documentData.assignment,new SortByTime());
    }


}
