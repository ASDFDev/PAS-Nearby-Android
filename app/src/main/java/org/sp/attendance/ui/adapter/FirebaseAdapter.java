package org.sp.attendance.ui.adapter;


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

}
