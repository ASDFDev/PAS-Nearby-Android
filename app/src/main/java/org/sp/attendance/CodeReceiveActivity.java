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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.sp.attendance.utils.AccountsManager;
import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

public class CodeReceiveActivity extends AppCompatActivity {

    private static CodeReceiveActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        CodeManager.setupStudentEnvironment(this, AccountsManager.loggedInUserID);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    public void stopReceive(View view) {
        CodeManager.destroy();
        DatabaseManager.destroy();
        finish();
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
                        .setCancelable(false)
                        .setPositiveButton(R.string.continue_prompt, (dialog, which) -> finish())
                        .create()
                        .show();
            }
        }
    }


    public CodeReceiveActivity(){
        instance = this;
    }

    public static Context getmContext(){
        return instance;
    }

}