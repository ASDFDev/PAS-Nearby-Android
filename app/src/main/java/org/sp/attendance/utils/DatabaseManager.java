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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.models.NtpModel;
import org.sp.attendance.R;
import org.sp.attendance.models.DatabaseModel;

import java.util.ArrayList;
import java.util.List;


public class DatabaseManager {

    Boolean isDestroyed = true;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static DatabaseModel databaseModel;
    private static String globalClassValue, databaseArray, studentAccount, deviceHardwareID;
    private Context context;

    public DatabaseManager(Context context){
        this.context = context;
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
        NtpModel ntpModel = new NtpModel(context);
        String currentTime = ntpModel.getTrueYear() + "/" + ntpModel.getTrueMonth() + "/" + ntpModel.getTrueDay();
        reference.child(currentTime + "/" + message)
                .addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            databaseArray = String.valueOf(dataSnapshot.getValue());
                                if (dataSnapshot.hasChild(AccountsManager.loggedInUserID) ||
                                        databaseArray.contains(deviceID)) {
                                    // Device and username exists
                                    showDatabaseResult(context.getResources().getString(R.string.title_code_failed),
                                            context.getResources().getString(R.string.error_already_submitted));
                                } else {
                                    final String key = dataSnapshot.child(AccountsManager.loggedInUserID).getKey();
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

    // TODO: Migrate to this function in AttendanceActivity
    public String getStudent(){
        List<String> studentArrayList = new ArrayList<>();
        DatabaseReference classReference = FirebaseDatabase.getInstance()
                .getReference("2017")
                .child("June")
                .child("25")
                .child("12345");
        classReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    studentAccount = childSnapshot.getKey();
                    studentArrayList.add(studentAccount);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error! Message: " + databaseError);
            }
        });
        // Currently this returns null
        return studentAccount;
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
