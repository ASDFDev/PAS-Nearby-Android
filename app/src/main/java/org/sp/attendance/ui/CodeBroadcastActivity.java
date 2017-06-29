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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.R;
import org.sp.attendance.models.DateTime;
import org.sp.attendance.utils.Ntp.SntpConsumer;
import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class CodeBroadcastActivity extends AppCompatActivity {

    private CodeManager codeManager = new CodeManager(this);
    private DatabaseManager databaseManager = new DatabaseManager(this);
    private SntpConsumer sntpConsumer = new SntpConsumer(this);
    private String studentAccount;
    private TextView textView;
    private static final String FORMAT = "%02d:%02d:%02d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
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
                    /* Bad code, we will do something about it later on....
                    TODO: Refactor this...
                    */
                    setContentView(R.layout.activity_attendance);
                    codeManager.setupLecturerEnvironment(this, code, DateTime.INSTANCE.convertSecondsToMins(intDuration));
                    hideKeyboard();
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    setTextView(intDuration);
                    List<String> studentArrayList = new ArrayList<>();
                    ListView listView = findViewById(R.id.ListView);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_list_item_1,
                            studentArrayList);
                    DatabaseReference classReference = FirebaseDatabase.getInstance()
                            .getReference(DateTime.INSTANCE.getTrueYearToString(sntpConsumer.getNtpTime()))
                            .child(DateTime.INSTANCE.getTrueMonthToString(sntpConsumer.getNtpTime()))
                            .child(DateTime.INSTANCE.getTrueDayToString(sntpConsumer.getNtpTime()))
                            .child(code);
                    classReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                                studentAccount = childSnapshot.getKey();
                                arrayAdapter.notifyDataSetChanged();
                                studentArrayList.add(studentAccount);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("Database Error! Message: " + databaseError);
                        }
                    });
                    arrayAdapter.notifyDataSetChanged();
                    listView.setAdapter(arrayAdapter);
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
                textView.setText("Time remaining: " + String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) );
            }

            public void onFinish() {
                textView.setText(R.string.attendance_ended);
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

}

