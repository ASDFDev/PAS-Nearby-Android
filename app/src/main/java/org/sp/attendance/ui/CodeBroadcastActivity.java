package org.sp.attendance.ui;

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
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import org.sp.attendance.R;
import org.sp.attendance.models.MessageModel;
import org.sp.attendance.service.sntp.SntpConsumer;
import org.sp.attendance.utils.AccountCheck;
import org.sp.attendance.utils.DateTime;
import org.sp.attendance.ui.adapter.FirebaseAdapter;
import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

// TODO: USE FRAGMENT!!! What kind of code is this?

public class CodeBroadcastActivity extends AppCompatActivity {

    private final CodeManager codeManager = new CodeManager(this);
    private final DatabaseManager databaseManager = new DatabaseManager(this);
    private final FirebaseAdapter firebaseAdapter = new FirebaseAdapter(this);
    private final SntpConsumer sntpConsumer = new SntpConsumer(this);

    private TextView textView;
    private String serializedMessage = "";
    private static final String TAG = "CodeBroadcastActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        codeManager.destroy();
        databaseManager.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        codeManager.destroy();
        databaseManager.destroy();
    }

    public void startBroadcast(View view) {
        final String code = ((EditText) findViewById((R.id.textCode))).getText().toString();
        final String stringDuration = ((EditText) findViewById((R.id.textDuration))).getText().toString();
        final int intDuration = Integer.parseInt(stringDuration);
        if (code.matches("") || stringDuration.matches("")) {
            new AlertDialog.Builder(CodeBroadcastActivity.this)
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.error_empty_editText_boardcast)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                    })
                    .create()
                    .show();
            return;
        } else if(intDuration == 0 || intDuration >= 1440) {
            new AlertDialog.Builder(CodeBroadcastActivity.this)
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.error_invalid_timing)
                    .setCancelable(false)
                    .setIcon(R.drawable.ic_warning_black_24dp)
                    .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                    })
                    .create()
                    .show();
            return;
        }
        new AlertDialog.Builder(CodeBroadcastActivity.this)
                .setTitle(R.string.confirmation)
                .setMessage(getResources().getString(R.string.continue_confirmation) + code + getResources().getString(R.string.continue_confirmation2))
                .setIcon(R.drawable.ic_question_answer_black_24dp)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialog, id) -> {
                    hideKeyboard();
                    setContentView(R.layout.activity_attendance);
                    Date timeStamp = sntpConsumer.getNtpTime();
                    codeManager.setupLecturerEnvironment(this, serializedMessage(code,timeStamp), DateTime.INSTANCE.convertSecondsToMins(intDuration));
                    setTextView(intDuration);
                    databaseManager.getStudent(code, timeStamp);
                    textView = findViewById(R.id.studentCount);
                })
                .setNegativeButton(R.string.no, (dialog, id) -> dialog.cancel())
                .create()
                .show();
    }

    private void setTextView(int min_duration) {
        new CountDownTimer(DateTime.INSTANCE.convertMilliToSecs(min_duration), 1000
                /* textView will be updated every sec(1000 milliseconds) */) {
            public void onTick(long millisUntilFinished) {
                textView = findViewById(R.id.timeLeft);
                String timeRemainingFormatted = String.format(getResources().getString(R.string.time_remaining),
                DateTime.INSTANCE.timeInHourMinSecs(millisUntilFinished));
                textView.setText(timeRemainingFormatted);
            }
            public void onFinish() {
                textView.setText(R.string.attendance_ended);
                String formattedStudentCount = String.format(getResources().getString(R.string.student_count),
                        firebaseAdapter.getStudentCount());
                textView.setText(formattedStudentCount);
            }
        }.start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_permission)
                    .setMessage(R.string.error_nearby_access_denied)
                    .setCancelable(false)
                    .setPositiveButton(R.string.continue_prompt, (dialog, which) -> CodeManager.resolvingPermissionError = false)
                    .create()
                    .show();
            if (resultCode == Activity.RESULT_CANCELED) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission)
                        .setMessage(R.string.error_nearby_access_still_denied)
                        .setIcon(R.drawable.ic_error_outline_black_50dp)
                        .setCancelable(false)
                        .setPositiveButton(R.string.continue_prompt, (dialog, which) -> finish())
                        .create()
                        .show();
            }
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private String serializedMessage(String code, Date timeStamp){
        MessageModel messageModel = new MessageModel();
        messageModel.setNearbyMessage(code);
        messageModel.setTimeStamp(timeStamp);
        messageModel.setUsername(AccountCheck.INSTANCE.areWeDemoAccountOrSpiceAccount());
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(messageModel);
            objectOutputStream.flush();
            serializedMessage = byteArrayOutputStream.toString();
        } catch (Exception exception){
            /* This should *NEVER* happen. But weird things happen all the time...*/
            Log.wtf(TAG,exception);
        }
        return serializedMessage;
    }

}

