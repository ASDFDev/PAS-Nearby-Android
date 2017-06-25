package org.sp.attendance.utils.account.spice;


/* This file was originally part of ATS_NativeUI. It was adapted to suit the needs of this project.
 * ATS_NativeUI is also licensed under GPLv3. You can find the original code here:
 * https://github.com/Minatosan/ATS_NativeUI
 *
 * Copyright 2017 Daniel Quah and Justin Xin
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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.github.ybq.android.spinkit.style.Wave;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sp.attendance.R;
import org.sp.attendance.ui.CodeBroadcastActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ConnectionManager extends AsyncTask<String, Integer, String> {

    /*
     Account manager for student.
     This interacts with Singapore Poly's student database. Because this is an unofficial API, the code is highly
     experimental in nature and *WILL* break without notice.
    */

    private ProgressDialog progressDialog;
    private Context context;
    private String result;
    private SignInResponse signInState;
    private CodeResponse codeState;
    private String connectionType;
    private String atsHost = "myats.sp.edu.sg";
    private String atsPublicURL = "https://" + atsHost + "/";
    private String atsLoginURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL";
    private String atsLoginPostURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL?cmd=login&languageCd=ENG";
    private String atsCodeURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL";
    public static String loggedInUser;

    public ConnectionManager(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        connectionType = params[0];
        switch (connectionType) {
            case "SignInOnly":
                try {
                    String userID = params[1];
                    String password = params[2];
                    signIn(userID, password);
                } catch (Exception e) {
                    result = (context.getResources().getString((R.string.error_unknown)) + e.toString() + "\n\nWeb trace: \n" + result);
                    signInState = SignInResponse.Unknown;
                }
                break;
            default:
                result = "Invalid connection type";
                signInState = null;
                codeState = null;
                break;
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        Wave mWaveDrawable = new Wave();
        mWaveDrawable.setBounds(0, 0, 100, 100);
        mWaveDrawable.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        progressDialog.setIndeterminateDrawable(mWaveDrawable);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(String result1) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            switch (connectionType) {
                case "SignInOnly":
                    if (signInState.equals(SignInResponse.SignedIn)) {
                        Intent codeInputIntent = new Intent(context, CodeBroadcastActivity.class);
                        context.startActivity(codeInputIntent);
                        updateUI();
                    } else if (signInState.equals(SignInResponse.InvalidCredentials)) {
                        showDialog(context.getResources().getString(R.string.title_sign_in_failed),
                                context.getResources().getString(R.string.error_credentials_invalid));
                    } else if (signInState.equals(SignInResponse.OutsideSP)) {
                        showDialog(context.getResources().getString(R.string.title_sign_in_failed),
                                context.getResources().getString(R.string.error_outside_sp));
                    } else if (signInState.equals(null) && codeState.equals(CodeResponse.NotSignedIn)) {
                        showDialog(context.getResources().getString(R.string.title_code_failed),
                                context.getResources().getString(R.string.error_outside_sp));
                    } else {
                        showDialog(context.getResources().getString(R.string.title_code),
                                context.getResources().getString(R.string.error_unknown) + "\n\n" + result);
                    }
                    break;
                default:
                    showDialog(context.getResources().getString(R.string.title_internal), "");
                    break;
            }
        } catch (Exception e) {
            showDialog(context.getResources().getString(R.string.error_unknown),"");
        }
    }

    private void signIn(String userID, String password) throws Exception {
        BufferedReader bufferedReader;
        URL publicURL = new URL(atsPublicURL);
        URL loginPostURL = new URL(atsLoginPostURL);
        URL codeURL = new URL(atsCodeURL);
        StringBuffer stringBuffer;
        //Prepare environment
        CookiesManager.clearCookies();
        CookiesManager.isCookiesStored = false;
        //Get login page
        HttpsURLConnection connection = (HttpsURLConnection) publicURL.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.setRequestProperty("Accept-Language", "en,en-GB;q=0.8,ja;q=0.6");
        if (CookiesManager.isCookiesStored = true) {
            for (String cookie : CookiesManager.getCookies()) {
                connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
            }
        }
        bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String loginPageContents;
        stringBuffer = new StringBuffer();
        while ((loginPageContents = bufferedReader.readLine()) != null) {
            stringBuffer.append(loginPageContents);
        }
        bufferedReader.close();
        CookiesManager.setCookies((connection.getHeaderFields().get("Set-Cookie")));
        //TODO: Parse page and find string instead
        if (stringBuffer.toString().contains("Please connect to SPStudent wifi profile before accessing ATS")) {
            result = stringBuffer.toString();
            signInState = SignInResponse.OutsideSP;
        } else if (stringBuffer.toString().contains("PeopleSoft Enterprise Sign-in")) {
            //Get login params
            Document formPage = Jsoup.parse(stringBuffer.toString());
            Elements inputElements = formPage.getElementsByTag("input");
            List<String> paramList = new ArrayList<>();
            for (Element inputElement : inputElements) {
                String key = inputElement.attr("name");
                String value = inputElement.attr("value");
                switch (key) {
                    case "userid":
                        value = userID;
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                        break;
                    case "pwd":
                        value = password;
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                        break;
                    case "timezoneOffset":
                        value = "-480";
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                        break;
                }
            }
            StringBuilder postParamsRaw = new StringBuilder();
            for (String param : paramList) {
                if (postParamsRaw.length() == 0) {
                    postParamsRaw.append(param);
                } else {
                    postParamsRaw.append("&").append(param);
                }
            }
            String postParams = postParamsRaw.toString();
            //Send login data
            connection = (HttpsURLConnection) loginPostURL.openConnection();
            connection.setUseCaches(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Host", atsHost);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en,en-GB;q=0.8,ja;q=0.6");
            if (CookiesManager.isCookiesStored = true) {
                for (String cookie : CookiesManager.getCookies()) {
                    connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }
            connection.setRequestProperty("Connection", "keep-alive");
            connection.setRequestProperty("Origin", atsPublicURL);
            connection.setRequestProperty("Referer", atsLoginURL);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(postParams);
            outputStream.flush();
            outputStream.close();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            stringBuffer = new StringBuffer();
            while ((inputLine = bufferedReader.readLine()) != null) {
                stringBuffer.append(inputLine);
            }
            bufferedReader.close();
            //Get login result
            connection = (HttpsURLConnection) codeURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "en,en-GB;q=0.8,ja;q=0.6");
            if (CookiesManager.isCookiesStored = true) {
                for (String cookie : CookiesManager.getCookies()) {
                    connection.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
                }
            }
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String codePageContents;
            stringBuffer = new StringBuffer();
            while ((codePageContents = bufferedReader.readLine()) != null) {
                stringBuffer.append(codePageContents);
            }
            bufferedReader.close();
            if (stringBuffer.toString().contains("PeopleSoft Enterprise Sign-in")) {
                result = stringBuffer.toString();
                signInState = SignInResponse.InvalidCredentials;
            } else if (stringBuffer.toString().contains("Attendance Code Submission")) {
                CookiesManager.isCookiesStored = true;
                result = stringBuffer.toString();
                signInState = SignInResponse.SignedIn;
                loggedInUser = userID.toUpperCase();
            } else {
                result = stringBuffer.toString();
                signInState = SignInResponse.Unknown;
            }
        } else {
            result = ("Invalid page type");
            signInState = SignInResponse.Unknown;
        }
    }

    private void showDialog(String title, String message){
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

    private enum CodeResponse {
        NotSignedIn
    }

    private enum SignInResponse {
        SignedIn, InvalidCredentials, OutsideSP, Unknown
    }


}
