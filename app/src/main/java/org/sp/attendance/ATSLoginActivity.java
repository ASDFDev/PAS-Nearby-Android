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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;


import org.sp.attendance.utils.AccountsManager;
import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.StartUpManager;




public class ATSLoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atslogin);
        showProgressDialog();
        if (!CodeManager.isDestroyed) {
            CodeManager.destroy();
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
                .setCancelable(false)
                .setPositiveButton(R.string.dismiss, (dialog, which) -> {
                })
                .create()
                .show();
    }

    private void showProgressDialog(){
        ProgressDialog progressdialog = new ProgressDialog(ATSLoginActivity.this);
        progressdialog.setMessage(getResources().getString(R.string.please_wait));
        progressdialog.setCancelable(false);
        progressdialog.show();
        initChecks();
        progressdialog.dismiss();
    }


    private void initChecks(){
        new StartUpManager(this);
        StartUpManager.initStartUp();
    }
}
