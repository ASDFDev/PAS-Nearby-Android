package org.sp.attendance.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by HexGate on 8/6/2016.
 */

public class DatabaseManager {


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference reference = database.getInstance().getReference();

    Context globalContext;

    /*
        Student device checks
     */

    String deviceHardwareID;
    Boolean studentSubmitted;

    public DatabaseManager(Context context) {
        deviceHardwareID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        globalContext = context;
    }

    public Boolean checkStudentDevice(String message, String userID) {
        String[] messageParsed = message.split("|");
        final String classCode = messageParsed[0];
        String attendanceCode = messageParsed[1];
        reference.child(classCode).child(deviceHardwareID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            // Device exists, check if submission is valid
                            String databaseValue = dataSnapshot.getValue().toString();
                            if (databaseValue != null) {
                                studentSubmitted = true;
                            } else {
                                studentSubmitted = false;
                            }
                        } else {
                            studentSubmitted = false;
                                reference.child(classCode).child(deviceHardwareID).push();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        new AlertDialog.Builder(globalContext)
                                .setTitle(R.string.error_firebase_verify_failed)
                                .setCancelable(false)
                                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create().show();
                        studentSubmitted = true;
                    }
                });
        if (studentSubmitted = false) {
            return true;
        } else {
            return false;
        }
    }

    public void openDatabaseForLecturer(String code) {
        String classCode = generateClassCode();
        reference.child(classCode).push();
    }

    public String generateMessage(String code, String classCode) {
        String message = (classCode + "|" + code);
        return message;
    }

    private String generateClassCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 18; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}
