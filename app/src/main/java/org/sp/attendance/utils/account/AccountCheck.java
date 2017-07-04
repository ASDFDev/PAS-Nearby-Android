package org.sp.attendance.utils.account;


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

import org.sp.attendance.utils.account.spice.SpiceManager;

public class AccountCheck {

    private static String account;

    // I can't believe this class is necessary.....

    public static String areWeDemoAccountOrSpiceAccount(){
        // if Account manager is null, it means we are logged in via SPICE
        if(TempAccountManager.loggedInUserID == null){
           account = SpiceManager.loggedInUser;
        } else if(SpiceManager.loggedInUser == null){
            account = TempAccountManager.loggedInUserID;
        }
        return account;
    }

}
