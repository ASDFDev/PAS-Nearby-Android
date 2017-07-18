package org.sp.attendance.utils.account;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import org.sp.attendance.ui.CodeBroadcastActivity;
import org.sp.attendance.ui.CodeReceiveActivity;
import org.sp.attendance.R;

import java.util.Locale;

public class TempAccountManager extends AsyncTask<String, Integer, String> {

    /* Pseudo-accounts manager for lecturer.
       Provides a small database of pre-set accounts for testing use
       Can be replaced with actual connection code
     */

    public static String loggedInUserID;

    private ProgressDialog progressDialog;
    private final Context context;
    private String result;
    private SignInResponse signInState;
    private SignInType signInType;

    public TempAccountManager(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
            try {
                String userID = params[0];
                String password = params[1];
                String _userID = userID.toLowerCase(Locale.ENGLISH);
                if (_userID.equals("s10001") && password.equals("staff")) {
                    signInType = SignInType.Staff;
                    signInState = SignInResponse.SignedIn;
                    loggedInUserID = userID.toUpperCase(Locale.ENGLISH);
                } else if ((_userID.equals("stud1") ||
                        _userID.equals("stud2") ||
                        _userID.equals("stud3") ||
                        _userID.equals("stud4") ||
                        _userID.equals("stud5")) && password.equals("student")) {
                    signInType = SignInType.Student;
                    signInState = SignInResponse.SignedIn;
                    loggedInUserID = userID.toUpperCase(Locale.ENGLISH);
                } else {
                    signInState = SignInResponse.InvalidCredentials;
                }
            } catch (Exception e) {
                result = (context.getResources().getString((R.string.error_unknown)));
                signInState = SignInResponse.Unknown;
            }
        return result;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result1) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
                if (signInState.equals(SignInResponse.SignedIn)) {
                    if (signInType == SignInType.Student) {
                        Intent codeReceiveIntent = new Intent(context, CodeReceiveActivity.class);
                        context.startActivity(codeReceiveIntent);
                    } else if (signInType == SignInType.Staff) {
                        Intent codeBroadcastIntent = new Intent(context, CodeBroadcastActivity.class);
                        context.startActivity(codeBroadcastIntent);
                    }
                    updateUI();
                } else if (signInState.equals(SignInResponse.InvalidCredentials)) {
                    showPostExecuteDialog(context.getResources().getString(R.string.title_sign_in_failed),
                            context.getResources().getString(R.string.error_credentials_invalid));
                } else if (signInState.equals(SignInResponse.OutsideSP)) {
                    showPostExecuteDialog(context.getResources().getString(R.string.title_sign_in_failed),
                            context.getResources().getString(R.string.error_outside_sp));
                }
        } catch (Exception e) {
            showPostExecuteDialog("", (e.toString()));
        }
    }

    private void showPostExecuteDialog(String title, String message){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.dismiss), (dialog, which) -> {
                })
                .create()
                .show();
    }


    private String updateUI() {
        return result;
    }

    private enum SignInResponse {
        SignedIn, InvalidCredentials, OutsideSP, Unknown
    }

    private enum SignInType {
        Staff, Student
    }

}