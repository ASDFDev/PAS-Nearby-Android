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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.sp.attendance.CodeBroadcastActivity;
import org.sp.attendance.CodeReceiveActivity;
import org.sp.attendance.R;

public class AccountsManager extends AsyncTask<String, Integer, String> {

    // Pseudo-accounts manager, provides a small database of pre-set accounts for testing use
    // Can be replaced with actual connection code

    public static String loggedInUserID;

    private ProgressDialog progressDialog;
    private Context globalContext;
    private String result;
    private SignInResponse signInState;
    private CodeResponse codeState;
    private SignInType signInType;
    private String connectionType;

    public AccountsManager(Context context) {
        globalContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        connectionType = params[0];
        if (connectionType.equals("SignInOnly")) {
            try {
                String userID = params[1];
                String password = params[2];
                if (userID.toLowerCase().equals("s10001") && password.equals("staff")) {
                    signInType = SignInType.Staff;
                    signInState = SignInResponse.SignedIn;
                    loggedInUserID = userID.toUpperCase();
                    saveCredentials(userID, password);
                } else if ((userID.toLowerCase().equals("p1001000") ||
                        userID.toLowerCase().equals("p1001001") ||
                        userID.toLowerCase().equals("p1001002") ||
                        userID.toLowerCase().equals("p1001003") ||
                        userID.toLowerCase().equals("p1001004") ||
                        userID.toLowerCase().equals("p1001005") ||
                        userID.toLowerCase().equals("p1001006") ||
                        userID.toLowerCase().equals("p1001007") ||
                        userID.toLowerCase().equals("p1001008") ||
                        userID.toLowerCase().equals("p1001009") ||
                        userID.toLowerCase().equals("p1001010")) && password.equals("student")) {
                    signInType = SignInType.Student;
                    signInState = SignInResponse.SignedIn;
                    loggedInUserID = userID.toUpperCase();
                    saveCredentials(userID, password);
                } else {
                    signInState = SignInResponse.InvalidCredentials;
                }
            } catch (Exception e) {
                result = (globalContext.getResources().getString((R.string.error_unknown)));
                signInState = SignInResponse.Unknown;
            }
        } else {
            result = globalContext.getResources().getString((R.string.error_connection_invalid));
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(globalContext);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(globalContext.getResources().getString(R.string.please_wait));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result1) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (connectionType.equals("SignInOnly")) {
                if (signInState.equals(SignInResponse.SignedIn)) {
                    if (signInType == SignInType.Student) {
                        Intent codeReceiveIntent = new Intent(globalContext, CodeReceiveActivity.class);
                        globalContext.startActivity(codeReceiveIntent);
                    } else if (signInType == SignInType.Staff) {
                        Intent codeBroadcastIntent = new Intent(globalContext, CodeBroadcastActivity.class);
                        globalContext.startActivity(codeBroadcastIntent);
                    }
                    updateUI();
                } else if (signInState.equals(SignInResponse.InvalidCredentials)) {
                    showPostExecuteDialog(globalContext.getResources().getString(R.string.title_sign_in_failed),
                            globalContext.getResources().getString(R.string.error_credentials_invalid));
                } else if (signInState.equals(SignInResponse.OutsideSP)) {
                    showPostExecuteDialog(globalContext.getResources().getString(R.string.title_sign_in_failed),
                            globalContext.getResources().getString(R.string.error_outside_sp));
                }
            } else {
                showPostExecuteDialog(globalContext.getResources().getString(R.string.title_internal), "");
            }
        } catch (Exception e) {
            showPostExecuteDialog("", (e.toString()));
        }
    }

    private void saveCredentials(String userID, String password) {
        SharedPreferences sharedPref = globalContext.getSharedPreferences("org.sp.ats.accounts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.getString("ats_userid", "").equals("") && sharedPref.getString("ats_pwd", "").equals("")) {
            editor.putString("ats_userid", userID);
            editor.putString("ats_pwd", password);
            editor.apply();
        }
    }

    private void showPostExecuteDialog(String title, String message){
        new AlertDialog.Builder(globalContext)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    private String updateUI() {
        return result;
    }

    private enum CodeResponse {
        NotSignedIn
    }

    private enum SignInResponse {
        SignedIn, InvalidCredentials, OutsideSP, Unknown
    }

    private enum SignInType {
        Staff, Student
    }

}
