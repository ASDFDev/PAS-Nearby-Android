package org.sp.attendance;

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

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import org.sp.attendance.utils.AccountsManager;
import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.StartUpManager;

public class ATSLoginActivity extends AppCompatActivity{

    public static final int REQUEST_CODE_INTRO = 1;
    public static final String firstRun = "org.sp.attendance.firstRun";


    CodeManager codeManager = new CodeManager(this);
    StartUpManager startUpManager = new StartUpManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atslogin);
        checkFirstRun();
        startUpManager.checkNetwork();
        startUpManager.checkPlayServices(this);
        if (!codeManager.isDestroyed) {
            codeManager.destroy();
        }
    }

    private void checkFirstRun(){
        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(firstRun,true);
        if (firstStart) {
            Intent IntroSlideActivity = new Intent(this, SlideIntro.class);
            startActivityForResult(IntroSlideActivity, REQUEST_CODE_INTRO);
        }
    }

    public void signIn(View view) {
        if (!((EditText) findViewById(R.id.textEdit_userID)).getText().toString().equals("") &&
                !((EditText) findViewById(R.id.textEdit_password)).getText().toString().equals("")) {
            new AccountsManager(ATSLoginActivity.this).execute("SignInOnly",
                    ((EditText) findViewById(R.id.textEdit_userID)).getText().toString(),
                    ((EditText) findViewById(R.id.textEdit_password)).getText().toString());
        } else {
            showDialog(this.getResources().getString(R.string.title_sign_in_failed),
                    this.getResources().getString(R.string.error_credentials_disappeared));
        }
    }

    private void showDialog(String title, String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                })
                .create()
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(firstRun, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(firstRun, true)
                        .apply();
                finish();
            }
        }
    }


}
