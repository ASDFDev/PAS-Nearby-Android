package org.sp.attendance.ui.adapter;

/*
 * Copyright 2017 Daniel Quah and Justin Xin
 *
 * This file is part of org.sp.attendance
 *
 * ATS_Nearby is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ATS_Nearby is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.sp.attendance.R;

import java.util.ArrayList;

public class FirebaseAdapter {

    private Context context;
    private ArrayList<String> studentArrayList = new ArrayList<>();

    public FirebaseAdapter(Context context){
        this.context = context;
    }

    public void displayStudentsInClass(String studentAccount){
        ListView listView = ((Activity)context).findViewById(R.id.ListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        studentArrayList.add(studentAccount);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }

    public void removeStudentInClass(String studentAccount){
        ListView listView = ((Activity)context).findViewById(R.id.ListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        studentArrayList.remove(studentAccount);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }

    public void showPreviousCode(String attendanceCode){
        ListView listView = ((Activity)context).findViewById(R.id.calendarListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        studentArrayList.add(attendanceCode);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }

    public void removeAttendanceCode(String attendanceCode){
        ListView listView = ((Activity)context).findViewById(R.id.calendarListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        studentArrayList.remove(attendanceCode);
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }

    public void destroy(){
        ListView listView = ((Activity)context).findViewById(R.id.calendarListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        studentArrayList.add("No attendance code for today :(");
        arrayAdapter.notifyDataSetChanged();
        listView.setAdapter(arrayAdapter);
    }
}
