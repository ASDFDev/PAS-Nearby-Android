package org.sp.attendance;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import org.sp.attendance.utils.AccountsManager;
import org.sp.attendance.utils.CodeManager;

/**
 * Created by Daniel Quah on 21/5/2016
 */
public class CodeReceiveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        // TODO: Implement student ID for sending/receiving
        CodeManager.setupStudentEnvironment(this, AccountsManager.loggedInUserID);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        CodeManager.destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        CodeManager.destroy();
    }

    public void stopReceive(View view) {
        CodeManager.destroy();
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
                    .setPositiveButton(R.string.continue_prompt, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CodeManager.resolvingPermissionError = false;
                        }
                    })
                    .create()
                    .show();
            if (resultCode == Activity.RESULT_OK) {
            } else if (resultCode == Activity.RESULT_CANCELED) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_permission)
                        .setMessage(R.string.error_nearby_access_still_denied)
                        .setCancelable(false)
                        .setPositiveButton(R.string.continue_prompt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create()
                        .show();
            } else {
            }
        }
    }
}