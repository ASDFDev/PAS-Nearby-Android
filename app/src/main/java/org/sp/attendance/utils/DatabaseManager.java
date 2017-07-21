package org.sp.attendance.utils;

/*
 * Copyright 2016-2017 Daniel Quah and Justin Xin
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
import android.support.v7.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.models.DateTime;
import org.sp.attendance.R;
import org.sp.attendance.models.DatabaseModel;
import org.sp.attendance.service.sntp.SntpConsumer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DatabaseManager {

    Boolean isDestroyed = true;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseModel databaseModel;
    private static String globalClassValue, databaseArray, studentAccount, deviceHardwareID;
    private Context context;
    private SntpConsumer sntpConsumer;
    private Date timeStampCache;

    public DatabaseManager(Context context){
        this.context = context;
        sntpConsumer = new SntpConsumer(context);
    }

    public void destroy() {
        context = null;
        deviceHardwareID = null;
        database.goOffline();
        isDestroyed = true;
    }

    /*
        Student device operations
     */

    void initialize(Context context) {
        database.goOnline();
        isDestroyed = false;
    }

    void submitStudentDevice(final String message, final String deviceID, final String timeStamp) {
        deviceHardwareID = deviceID;
        databaseModel = new DatabaseModel();
        timeStampCache = sntpConsumer.getNtpTime();
        String currentTime = DateTime.INSTANCE.getTrueYearToString(timeStampCache) +
                "/" + DateTime.INSTANCE.getTrueMonthToString(timeStampCache) +
                "/" + DateTime.INSTANCE.getTrueDayToString(timeStampCache);
        reference.child(currentTime + "/" + message)
                .addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            databaseArray = String.valueOf(dataSnapshot.getValue());
                                if (dataSnapshot.hasChild(AccountCheck.INSTANCE.areWeDemoAccountOrSpiceAccount()) ||
                                        databaseArray.contains(deviceID)) {
                                    // Device and username exists
                                    showDatabaseResult(context.getResources().getString(R.string.title_code_failed),
                                            context.getResources().getString(R.string.error_already_submitted));
                                } else {
                                    final String key = dataSnapshot.child(AccountCheck.INSTANCE.areWeDemoAccountOrSpiceAccount()).getKey();
                                    databaseModel.setDeviceID(deviceHardwareID);
                                    databaseModel.setTimeStamp(timeStamp);
                                    final DatabaseReference databaseReference =
                                            reference.child(currentTime + "/" + message).child(key);
                                    databaseReference.setValue(databaseModel);
                                    showDatabaseResult(context.getResources().getString(R.string.title_code_success),
                                            context.getResources().getString(R.string.submission_message) + timeStamp);
                                }
                            } else{
                                showDatabaseResult(context.getResources().getString(R.string.title_code_failed),
                                        context.getResources().getString(R.string.error_code_unenrolled));
                            }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showDatabaseResult(context.getResources().getString(R.string.title_code_failed),
                                context.getResources().getString(R.string.error_code_invalid));
                    }
                });
    }



    /*
        Lecturer device operations
     */
    
    String generateMessage(String code) {
        globalClassValue = code;
        return code;
    }

    public void getStudent(String code){
        timeStampCache = sntpConsumer.getNtpTime();
        List<String> studentArrayList = new ArrayList<>();
        ListView listView = ((Activity)context).findViewById(R.id.ListView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                studentArrayList);
        DatabaseReference classReference = FirebaseDatabase.getInstance()
                .getReference(DateTime.INSTANCE.getTrueYearToString(timeStampCache))
                .child(DateTime.INSTANCE.getTrueMonthToString(timeStampCache))
                .child(DateTime.INSTANCE.getTrueDayToString(timeStampCache))
                .child(code);
        classReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                studentAccount = dataSnapshot.getKey();
                studentArrayList.add(studentAccount);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error! Message: " + databaseError);
            }
        });
        listView.setAdapter(arrayAdapter);
    }


    private void showDatabaseResult(String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.dismiss),
                        (dialog, which) -> ((Activity)context).finish())
                .create()
                .show();
    }
}
