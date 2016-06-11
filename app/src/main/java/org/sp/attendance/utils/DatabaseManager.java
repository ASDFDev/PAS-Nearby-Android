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


    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference reference = database.getInstance().getReference();

    public DatabaseManager(Context context) {
        deviceHardwareID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /*
        Student device operations
     */

    private static String deviceHardwareID;
    private static Boolean studentSubmitted;

    public static Boolean checkStudentDevice(String message, String userID) {
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
                        studentSubmitted = true;
                    }
                });
        if (studentSubmitted = false) {
            return true;
        } else {
            return false;
        }
    }

    /*
        Lecturer device operations
     */

    public static void openDatabaseForLecturer(String code) {
        String classCode = generateClassCode();
        reference.child(classCode).push();
    }

    private static void closeDatabaseForLecturer(String classCode) {
        reference.child(classCode).removeValue();
    }

    public static String generateMessage(String code, String classCode) {
        String message = (classCode + "|" + code);
        return message;
    }

    private static  String generateClassCode() {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 18; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static void removeEntry(String deviceHardwareID) {
        //TODO: Manually remove entry for student devices in the event of conflict
    }


}
