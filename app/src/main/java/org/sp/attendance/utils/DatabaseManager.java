package org.sp.attendance.utils;

/**
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
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.CodeReceiveActivity;
import org.sp.attendance.R;

import java.util.Random;

public class DatabaseManager {

    static Boolean isDestroyed = true;
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    private static String deviceHardwareID;
    private static Context ctx = CodeReceiveActivity.getmContext();
    private static DatabaseModel databaseModel;

    public static void destroy() {
        ctx = null;
        deviceHardwareID = null;
        database.goOffline();
        isDestroyed = true;
    }

    /*
        Student device operations
     */
    private static String globalClassValue;

    static void initialize(Context context) {
        ctx = context;
        database.goOnline();
        isDestroyed = false;
    }

    static void submitStudentDevice(final String message, final String deviceID) {
        deviceHardwareID = deviceID;
        databaseModel = new DatabaseModel();
        // TODO: Check class state
        reference.child(message).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                                if (dataSnapshot.hasChild(AccountsManager.loggedInUserID) || dataSnapshot.child(deviceHardwareID).exists()) {
                                    // Device exists, check if submission is valid
                                    showDatabaseResult(ctx.getResources().getString(R.string.title_code_failed),
                                            ctx.getResources().getString(R.string.error_already_submitted));
                                } else {
                                    final String key = dataSnapshot.child(AccountsManager.loggedInUserID).getKey();
                                    databaseModel.setDeviceID(deviceHardwareID);
                                    databaseModel.setTimeStamp(ServerValue.TIMESTAMP);
                                    final DatabaseReference databaseReference = reference.child(message).child(key);
                                    databaseReference.setValue(databaseModel);

                                    showDatabaseResult(ctx.getResources().getString(R.string.title_code_success), "");
                                }

                        } else {
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

    static void openDatabaseForLecturer() {
        reference.child(globalClassValue).child(":::CLASS_STATE:::").setValue("CLASS_STARTED");
    }

    /*
        Lecturer device operations
     */

    public static void closeDatabaseForLecturer() {
        reference.child(globalClassValue).child(":::CLASS_STATE:::").setValue("CLASS_ENDED");
    }

    static String generateMessage(String code) {
        String message = (generateClassCode() + "|||" + code);
        globalClassValue = message;
        return message;
    }

    @NonNull
    public static String[] parseMessage(String message) {
        return message.split("|");
    }

    @NonNull
    private static String generateClassCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 18; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    private static void showDatabaseResult(String title, String message){
        new AlertDialog.Builder(ctx)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(ctx.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)ctx).finish();
                    }
                })
                .create()
                .show();
    }
}
