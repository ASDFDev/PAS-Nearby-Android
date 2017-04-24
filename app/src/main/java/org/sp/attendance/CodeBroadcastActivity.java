package org.sp.attendance;

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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;

import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

public class CodeBroadcastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CodeManager.destroy();
        DatabaseManager.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CodeManager.destroy();
        DatabaseManager.destroy();
    }

    public void startBroadcast(View view) {
        final String code = ((EditText) findViewById((R.id.textCode))).getText().toString();
        if (code.matches("")) {
            new AlertDialog.Builder(CodeBroadcastActivity.this)
                    .setTitle(R.string.title_warning)
                    .setMessage(R.string.error_code_disappeared)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
            return;
        }
        new AlertDialog.Builder(CodeBroadcastActivity.this)
                .setTitle(R.string.title_warning)
                .setMessage(getResources().getString(R.string.continue_confirmation) + code + getResources().getString(R.string.continue_confirmation2))
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        (findViewById(R.id.layout_code_input)).setVisibility(ScrollView.GONE);
                        (findViewById(R.id.layout_code_broadcasting)).setVisibility(ScrollView.VISIBLE);
                        CodeManager.setupLecturerEnvironment(CodeBroadcastActivity.this, code);
                        hideKeyboard();
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    public void stopBroadcast(View view) {
        DatabaseManager.closeDatabaseForLecturer();
        CodeManager.destroy();
        DatabaseManager.destroy();
        new AlertDialog.Builder(CodeBroadcastActivity.this)
                .setTitle("Are you sure?")
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.title_permission)
                    .setMessage(R.string.error_nearby_access_denied)
                    .setCancelable(false)
                    .setPositiveButton(R.string.continue_prompt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CodeManager.resolvingPermissionError = false;
                        }
                    })
                    .create()
                    .show();
            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission)
                        .setMessage(R.string.error_nearby_access_still_denied)
                        .setCancelable(false)
                        .setPositiveButton(R.string.continue_prompt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            } else {
            }
        }
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
    // Reserved space for future database management for staff

