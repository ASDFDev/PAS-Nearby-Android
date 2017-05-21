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

import org.sp.attendance.CodeReceiveActivity;
import org.sp.attendance.R;
import org.sp.attendance.models.DatabaseModel;

public class DatabaseManager {

    static Boolean isDestroyed = true;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static String deviceHardwareID;
    private static Context ctx = CodeReceiveActivity.getmContext();
    private static DatabaseModel databaseModel;
    private static String globalClassValue;
    private static String databaseArray;

    public static void destroy() {
        ctx = null;
        deviceHardwareID = null;
        database.goOffline();
        isDestroyed = true;
    }

    /*
        Student device operations
     */

    static void initialize(Context context) {
        ctx = context;
        database.goOnline();
        isDestroyed = false;
    }


    static void submitStudentDevice(final String message, final String deviceID, final String timeStamp) {
        deviceHardwareID = deviceID;
        databaseModel = new DatabaseModel();
        reference.child(message).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            System.out.println("Datasnapshot value: " + dataSnapshot.getValue());
                            databaseArray = String.valueOf(dataSnapshot.getValue());
                                if (dataSnapshot.hasChild(AccountsManager.loggedInUserID) ||
                                        databaseArray.contains(deviceID)) {
                                    // Device and username exists
                                    showDatabaseResult(ctx.getResources().getString(R.string.title_code_failed),
                                            ctx.getResources().getString(R.string.error_already_submitted));
                                } else {
                                    final String key = dataSnapshot.child(AccountsManager.loggedInUserID).getKey();
                                    databaseModel.setDeviceID(deviceHardwareID);
                                    databaseModel.setTimeStamp(timeStamp);
                                    final DatabaseReference databaseReference = reference.child(message).child(key);
                                    databaseReference.setValue(databaseModel);
                                    showDatabaseResult(ctx.getResources().getString(R.string.title_code_success), "Submitted on: " + timeStamp);
                                }
                            } else{
                                showDatabaseResult(ctx.getResources().getString(R.string.title_code_failed),
                                        ctx.getResources().getString(R.string.error_code_unenrolled));
                            }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        showDatabaseResult(ctx.getResources().getString(R.string.title_code_failed),
                                ctx.getResources().getString(R.string.error_code_invalid));
                    }
                });
    }



    /*
        Lecturer device operations
     */
    
    static String generateMessage(String code) {
        globalClassValue = code;
        return code;
    }

    static void openDatabaseForLecturer(){
        reference.child(globalClassValue).setValue("");
    }

    private static void showDatabaseResult(String title, String message){
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(ctx.getResources().getString(R.string.dismiss), (dialog, which) -> ((Activity)ctx).finish())
                .create()
                .show();
    }
}
