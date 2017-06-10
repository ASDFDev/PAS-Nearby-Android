package org.sp.attendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.sp.attendance.models.NtpModel;
import org.sp.attendance.utils.CodeManager;

import java.util.ArrayList;
import java.util.List;

// This activity is not exposed to the user yet
public class AttendanceActivity extends AppCompatActivity{

    private String studentAccount;

    // TODO: Refractor this!!!
    @Override
    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        NtpModel ntpModel = new NtpModel(this);
        setContentView(R.layout.activity_attendance);
        ListView lv = findViewById(R.id.ListView);
        List<String> studenArrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                studenArrayList);
        DatabaseReference classReference = FirebaseDatabase.getInstance()
                .getReference(ntpModel.getTrueYear())
                .child(ntpModel.getTrueMonth())
                .child(ntpModel.getTrueDay())
                .child("");
        classReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    studentAccount = childSnapshot.getKey();
                    arrayAdapter.notifyDataSetChanged();
                    studenArrayList.add(studentAccount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Database Error! Message: " + databaseError);
            }
        });
        arrayAdapter.notifyDataSetChanged();
        lv.setAdapter(arrayAdapter);
    }

}
