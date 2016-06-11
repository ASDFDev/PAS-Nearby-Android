package org.sp.attendance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.DatabaseManager;

/**
 * Created by Daniel Quah on 21/5/2016
 */
public class CodeBroadcastActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
    }

    public void startBroadcast(View view) {
        String code = ((EditText)findViewById((R.id.textCode))).getText().toString();
        CodeManager.setupLecturerEnvironment(this, "Lecturer", code);
        DatabaseManager.openDatabaseForLecturer(code);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        (findViewById(R.id.layout_code_input)).setVisibility(ScrollView.GONE);
        (findViewById(R.id.layout_code_broadcasting)).setVisibility(ScrollView.VISIBLE);
    }

    public void stopBroadcast(View view) {
        CodeManager.destroy();
        (findViewById(R.id.layout_code_input)).setVisibility(ScrollView.VISIBLE);
        (findViewById(R.id.layout_code_broadcasting)).setVisibility(ScrollView.GONE);
        finish();
    }

    // Reserved space for future database management for staff

}