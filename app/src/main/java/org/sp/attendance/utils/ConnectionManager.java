package org.sp.attendance.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sp.attendance.ATSLoginActivity;
import org.sp.attendance.CodeBroadcastActivity;
import org.sp.attendance.CodeReceiveActivity;
import org.sp.attendance.R;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by HexGate on 9/5/16.
 */

public class ConnectionManager extends AsyncTask<String, Integer, String> {

    public static HttpsURLConnection connection;
    private ProgressDialog progressDialog;
    private Context globalContext;
    private String result;
    private SignInResponse signInState;
    private CodeResponse codeState;
    private SignInType signInType;
    private String connectionType;
    private String atsHost = "myats.sp.edu.sg";
    private String atsPublicURL = "https://" + atsHost + "/";
    private String atsLoginURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL";
    private String atsLoginPostURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL?cmd=login&languageCd=ENG";
    private String atsCodeURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/c/A_STDNT_ATTENDANCE.A_ATS_STDNT_SBMIT.GBL";
    private String atsCodePostURL = "https://" + atsHost + "/psc/cs90atstd/EMPLOYEE/HRMS/s/WEBLIB_A_ATS.ISCRIPT1.FieldFormula.IScript_SubmitAttendance";

    public ConnectionManager(Context context) {
        globalContext = context;
    }

    @Override
    protected String doInBackground(String... params) {
        connectionType = params[0];
        if (connectionType.equals("SignInOnly")) {
            try {
                String userID = params[1];
                String password = params[2];
                if (userID.startsWith("s")) {
                    signInType = SignInType.Staff;
                } else if (userID.startsWith("p")) {
                    signInType = SignInType.Student;
                } else {
                    signInType = SignInType.Guest;
                }
                signInState = SignInResponse.SignedIn;
                // Bypass sign-in for now
                // signIn(userID, password);
            } catch (Exception e) {
                result = (globalContext.getResources().getString((R.string.error_unknown)));
                signInState = SignInResponse.Unknown;
            }
        } else if (connectionType.equals("CodeOnly")) {
            try {
                String code = params[1];
                result = code;
                codeState = CodeResponse.Unknown;
                // Bypass code submission for now
                // submitCode(code);
            } catch (Exception e) {
                result = (globalContext.getResources().getString((R.string.error_unknown)));
                codeState = CodeResponse.Unknown;
            }
        } else {
            result = globalContext.getResources().getString((R.string.error_connection_invalid));
            signInState = null;
            codeState = null;
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
                    new AlertDialog.Builder(globalContext)
                            .setTitle(R.string.title_sign_in_failed)
                            .setMessage(R.string.error_credentials_invalid)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                } else if (signInState.equals(SignInResponse.OutsideSP)) {
                    new AlertDialog.Builder(globalContext)
                            .setTitle(R.string.title_sign_in_failed)
                            .setMessage(R.string.error_outside_sp)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                } else if (signInState.equals(null) && codeState.equals(CodeResponse.NotSignedIn)) {
                    new AlertDialog.Builder(globalContext)
                            .setTitle(R.string.title_code_failed)
                            .setMessage(R.string.error_timed_out)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                } else {
                    new AlertDialog.Builder(globalContext)
                            .setTitle(R.string.title_code)
                            .setMessage(globalContext.getResources().getString(R.string.error_unknown) + "\n\n" + result)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                }
            } else if (connectionType.equals("CodeOnly")) {
                if (codeState.equals(CodeResponse.Submitted)) {
                    new AlertDialog.Builder(globalContext)
                            .setTitle(R.string.title_code_success)
                            .setMessage(result)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                } else {
                    String dialogMessage;
                    String dialogTitle;
                    if (codeState.equals(CodeResponse.AlreadySubmitted)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_already_submitted);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else if (codeState.equals(CodeResponse.InvalidCode)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_code_invalid);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else if (codeState.equals(CodeResponse.NotEnrolled)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_code_unenrolled);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else if (codeState.equals(CodeResponse.OutsideSP)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_outside_sp);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else if (codeState.equals(CodeResponse.ClassEnded)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_code_ended);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else if (codeState.equals(CodeResponse.NotSignedIn)) {
                        dialogMessage = globalContext.getResources().getString(R.string.error_credentials_timed_out);
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    } else {
                        if (result.equals(globalContext.getResources().getString((R.string.error_code_disappeared)))) {
                            dialogMessage = globalContext.getResources().getString(R.string.error_code_disappeared);
                        } else {
                            dialogMessage = (globalContext.getResources().getString(R.string.error_unknown) + "\n" + result);
                        }
                        dialogTitle = globalContext.getResources().getString(R.string.title_code_failed);
                    }
                    new AlertDialog.Builder(globalContext)
                            .setTitle(dialogTitle)
                            .setMessage(dialogMessage)
                            .setCancelable(false)
                            .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (codeState.equals(CodeResponse.NotSignedIn)) {
                                        Intent codeInputIntent = new Intent(globalContext, ATSLoginActivity.class);
                                        globalContext.startActivity(codeInputIntent);
                                        ((Activity) globalContext).finish();
                                    }
                                }
                            })
                            .create()
                            .show();
                }
            } else {
                new AlertDialog.Builder(globalContext)
                        .setTitle(R.string.title_internal)
                        .setCancelable(false)
                        .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
            }
        } catch (Exception e) {
            new AlertDialog.Builder(globalContext)
                    .setMessage(e.toString())
                    .setCancelable(false)
                    .setPositiveButton(globalContext.getResources().getString(R.string.dismiss), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create()
                    .show();
        }
    }

    public void saveCredentials(String userID, String password) {
        SharedPreferences sharedPref = globalContext.getSharedPreferences("org.sp.ats.accounts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        if (sharedPref.getString("ats_userid", "").equals("") && sharedPref.getString("ats_pwd", "").equals("")) {
            editor.putString("ats_userid", userID);
            editor.putString("ats_pwd", password);
            editor.commit();
        }
    }

    private void signIn(String userID, String password) throws Exception {
        if (signInType == SignInType.Student) {
            BufferedReader bufferedReader;
            URL publicURL = new URL(atsPublicURL);
            URL loginPostURL = new URL(atsLoginPostURL);
            URL codeURL = new URL(atsCodeURL);
            StringBuffer stringBuffer;
            //Prepare environment
            CookiesManager.clearCookies();
            CookiesManager.isCookiesStored = false;
            //Get login page
            connection = (HttpsURLConnection) publicURL.openConnection();
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
                List<String> paramList = new ArrayList<String>();
                for (Element inputElement : inputElements) {
                    String key = inputElement.attr("name");
                    String value = inputElement.attr("value");
                    if (key.equals("userid")) {
                        value = userID;
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                    } else if (key.equals("pwd")) {
                        value = password;
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                    } else if (key.equals("timezoneOffset")) {
                        value = "-480";
                        paramList.add(key + "=" + URLEncoder.encode(value, "UTF-8"));
                    } else {
                    }
                }
                StringBuilder postParamsRaw = new StringBuilder();
                for (String param : paramList) {
                    if (postParamsRaw.length() == 0) {
                        postParamsRaw.append(param);
                    } else {
                        postParamsRaw.append("&" + param);
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
                    saveCredentials(userID, password);
                    signInState = SignInResponse.SignedIn;
                } else {
                    result = stringBuffer.toString();
                    signInState = SignInResponse.Unknown;
                }
            } else {
                result = ("Invalid page type");
                signInState = SignInResponse.Unknown;
            }
        } else {
            //TODO: Staff/guest sign in
            result = null;
            signInState = SignInResponse.SignedIn;
        }
    }

    private void submitCode(String code) throws Exception {
        BufferedReader bufferedReader;
        URL publicURL = new URL(atsPublicURL);
        URL codePostURL = new URL(atsCodePostURL);
        StringBuffer stringBuffer;
        //Get code page
        connection = (HttpsURLConnection) publicURL.openConnection();
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
        Document formCheckPage = Jsoup.parse(stringBuffer.toString());
        Element formCheckPageTitle = formCheckPage.getElementById("PSPAGETITLE");
        if (formCheckPageTitle.text().contains("Please connect to SPStudent wifi profile before accessing ATS")) {
            result = stringBuffer.toString();
            codeState = CodeResponse.OutsideSP;
        } else if (formCheckPageTitle.text().contains("PeopleSoft Enterprise Sign-in")) {
            result = stringBuffer.toString();
            signInState = null;
            codeState = CodeResponse.NotSignedIn;
        } else if (formCheckPageTitle.text().contains("Attendance Code")) {
            //Get new page
            connection = (HttpsURLConnection) codePostURL.openConnection();
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
            String codePageNewContents;
            stringBuffer = new StringBuffer();
            while ((codePageNewContents = bufferedReader.readLine()) != null) {
                stringBuffer.append(codePageNewContents);
            }
            bufferedReader.close();
            Document formPage = Jsoup.parse(stringBuffer.toString());
            Elements inputElements = formPage.getElementsByTag("input");
            List<String> paramList = new ArrayList<>();
            for (Element inputElement : inputElements) {
                System.out.println(inputElement.toString());
                String key = inputElement.attr("name");
                if (key.equals("ERROR") || key.equals("RECHECK") || key.equals("VALIDIP") ||
                        key.equals("STUDENTID") || key.equals("MODULECLASS") || key.equals("ATTDATE") ||
                        key.equals("CLASS_ST_TIME") || key.equals("CLASS_ED_TIME") || key.equals("TRANSACID") ||
                        key.equals("SUBMITTIME") || key.equals("TRANSAC_ST_TIME") || key.equals("NAVKEY")) {
                    paramList.add(key + "=" + URLEncoder.encode(inputElement.attr("value"), "UTF-8"));
                } else if (key.equals("ATT_CODE")) {
                    paramList.add(key + "=" + URLEncoder.encode(code, "UTF-8"));
                }
            }
            StringBuilder postParamsRaw = new StringBuilder();
            for (String param : paramList) {
                if (postParamsRaw.length() == 0) {
                    postParamsRaw.append(param);
                } else {
                    postParamsRaw.append("&" + param);
                }
            }
            String postParams = postParamsRaw.toString();
            //Send code data
            connection = (HttpsURLConnection) codePostURL.openConnection();
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
            connection.setRequestProperty("Referer", atsCodePostURL);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(postParams.length()));
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(postParams);
            outputStream.flush();
            outputStream.close();
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String codePostResultPage;
            stringBuffer = new StringBuffer();
            while ((codePostResultPage = bufferedReader.readLine()) != null) {
                stringBuffer.append(codePostResultPage);
            }
            bufferedReader.close();
            //Check for errors
            if (stringBuffer.toString().contains(" Invalid attendance code,")) {
                codeState = CodeResponse.InvalidCode;
            } else if (stringBuffer.toString().contains("PeopleSoft Enterprise Sign-in")) {
                signInState = null;
                codeState = CodeResponse.NotSignedIn;
            } else if (stringBuffer.toString().contains(" You have already submitted your attendance")) {
                codeState = CodeResponse.AlreadySubmitted;
            } else if (stringBuffer.toString().contains(" has ended.")) {
                codeState = CodeResponse.ClassEnded;
            } else if (stringBuffer.toString().contains("You are not registered in ")) {
                codeState = CodeResponse.NotEnrolled;
            } else {
                try {
                    Document resultPage = Jsoup.parse(stringBuffer.toString());
                    Boolean transactionIdPresent = !resultPage.getElementById("TRANSACID").attr("value").equals(null);
                    if (transactionIdPresent == true) {
                        Elements resultElements = resultPage.getElementsByTag("input");
                        String resultParsed = "Attendance Summary\n";
                        for (Element resultElement : resultElements) {
                            String key = resultElement.attr("name");
                            String value = resultElement.attr("value");
                            if (key.equals("STUDENTID")) {
                                resultParsed = (resultParsed + "\nStudent ID: " + value);
                            } else if (key.equals("MODULECLASS")) {
                                resultParsed = (resultParsed + "\nModule: " + value);
                            } else if (key.equals("TRANSACID")) {
                                resultParsed = (resultParsed + "\nTransaction ID: " + value);
                            } else if (key.equals("SUBMITTIME")) {
                                resultParsed = (resultParsed + "\nSubmission time: " + value);
                            }
                        }
                        resultParsed = (resultParsed + "\n\nNote that if you have submitted your attendance after the 15 minutes grace, your attendance will be marked as absent. ");
                        result = resultParsed;
                        codeState = CodeResponse.Submitted;
                    } else {
                        result = stringBuffer.toString();
                        codeState = CodeResponse.Unknown;
                    }
                } catch (Exception e) {
                    result = (e.toString() + "\n\nWeb trace: \n" + stringBuffer.toString());
                    codeState = CodeResponse.Unknown;
                }
            }
        } else {
            result = ("Invalid page type: \n" + stringBuffer.toString());
            codeState = CodeResponse.Unknown;
        }
    }

    private String updateUI() {
        return result;
    }

    private enum CodeResponse {
        Submitted, InvalidCode, NotEnrolled, AlreadySubmitted, ClassEnded, NotSignedIn, OutsideSP, Unknown
    }

    private enum SignInResponse {
        SignedIn, InvalidCredentials, OutsideSP, Unknown
    }

    private enum SignInType {
        Staff, Student, Guest
    }

}