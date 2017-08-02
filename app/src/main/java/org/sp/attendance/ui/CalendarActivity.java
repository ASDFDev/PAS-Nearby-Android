package org.sp.attendance.ui;

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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;
import android.widget.ListView;

import org.sp.attendance.R;
import org.sp.attendance.models.DateTime;
import org.sp.attendance.ui.adapter.FirebaseAdapter;
import org.sp.attendance.utils.DatabaseManager;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class CalendarActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private ListView listView;
    private static final String SHOWCASE_ID = "calendarActivity";
    private DatabaseManager databaseManager = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        fab = findViewById(R.id.fab);
        listView = findViewById(R.id.calendarListView);
        showCaseApp();
        showFab();
        showCalendar();
    }

    private void showCalendar(){
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((calendarView1, intYear, intMonth, intDay) -> {
            /*
            intDay gives you the month in numbering. We need to convert int to String then to month name
            since we are storing month name in Firebase
            */
            // We have to add 1 to the current month since array index _STARTS_ from 0
            int i3 = intMonth + 1;
            String month = Integer.toString(i3);
            String day = Integer.toString(intDay);
            String year = Integer.toString(intYear);
            FirebaseAdapter firebaseAdapter = new FirebaseAdapter(this);
            firebaseAdapter.destroy();
            databaseManager.showPastCode(year,
                  DateTime.INSTANCE.getMonthInNumberToName(month),
                    day);
        });
    }

    private void showFab(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent codeBroadcast = new Intent(CalendarActivity.this, CodeBroadcastActivity.class);
            startActivity(codeBroadcast);
        });
    }

    private void showCaseApp(){
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
        sequence.setConfig(config);
        sequence.addSequenceItem(fab, "Click here to start attendance taking now!" , "Got it!");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(listView)
                        .setDismissText("Got it!")
                        .setContentText("This is the attendance code")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();
    }
}
