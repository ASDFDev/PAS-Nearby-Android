package org.sp.attendance.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.sp.attendance.R;

import java.util.ArrayList;

public class LecturerOptionsSelectorActivity extends AppCompatActivity {

    private ArrayList<String> optionsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer);
        showOptionsToLecturer();
    }

    private void showOptionsToLecturer(){
        ListView listView = findViewById(R.id.SelectionListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                optionsArrayList);
        optionsArrayList.add("Start Attendance Taking");
        optionsArrayList.add("View Previous Class Attendance");
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if(position == 0){
                Intent codeBroadcastIntent = new Intent(this, CodeBroadcastActivity.class);
                this.startActivity(codeBroadcastIntent);
            } else {
                Intent calendarActivity = new Intent(this,CalendarActivity.class);
                this.startActivity(calendarActivity);
            }
        });
    }


}
