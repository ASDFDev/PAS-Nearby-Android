package org.sp.attendance.utils


/*
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

import android.util.Log

import org.sp.attendance.account.spice.SpiceManager
import org.sp.attendance.account.TempAccountManager

object AccountCheck {

    private var account: String? = null
    private val TAG = "AccountCheck"

    // I can't believe this class is necessary.....

    fun areWeDemoAccountOrSpiceAccount(): String? {
        // if Account manager is null, it means we are logged in via SPICE
        if (TempAccountManager.loggedInUserID == null) {
            Log.i(TAG, "Hard coded account")
            account = SpiceManager.loggedInUser
        } else if (SpiceManager.loggedInUser == null) {
            Log.i(TAG, "SPICE")
            account = TempAccountManager.loggedInUserID
        }
        return account
    }

}
