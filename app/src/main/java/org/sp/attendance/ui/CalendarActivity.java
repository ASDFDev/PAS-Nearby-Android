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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

import org.sp.attendance.R;
import org.sp.attendance.models.DateTime;
import org.sp.attendance.ui.adapter.FirebaseAdapter;
import org.sp.attendance.utils.DatabaseManager;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        showCalendar();
    }

    private void showCalendar(){
        CalendarView calendarView = findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener((calendarView1, i, i1, i2) -> {
            /*
            Day = i2 , Month = i1, Year = i
            i1 gives you an int(Month in numbering). We need to convert int to String then to month name
            since we are storing month name in Firebase
            */
            DatabaseManager databaseManager = new DatabaseManager(this);
            // We have to add 1 to the current month since array index _STARTS_ from 0
            int i3 = i1 + 1;
            String month = Integer.toString(i3);
            String day = Integer.toString(i2);
            String year = Integer.toString(i);
            FirebaseAdapter firebaseAdapter = new FirebaseAdapter(this);
            firebaseAdapter.destroy();
            databaseManager.showPastCode(year,
                  DateTime.INSTANCE.getMonthInNumberToName(month),
                    day);
        });
    }
}
