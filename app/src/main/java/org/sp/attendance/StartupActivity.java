package org.sp.attendance;

/**
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.sp.attendance.utils.CodeManager;
import org.sp.attendance.utils.NtpManager;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NtpManager.queryNtpServer();
        //TODO: Check saved credentials
        if (!CodeManager.isDestroyed) {
            CodeManager.destroy();
        }
        Intent loginIntent = new Intent(this, ATSLoginActivity.class);
        startActivity(loginIntent);
    }

}
