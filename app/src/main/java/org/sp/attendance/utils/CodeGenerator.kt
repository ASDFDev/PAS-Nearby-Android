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

import android.content.Context
import android.os.Build
import android.provider.Settings
import com.mcxiaoke.koi.HASH
import com.mcxiaoke.koi.ext.networkOperator
import com.mcxiaoke.koi.utils.currentVersion
import com.mcxiaoke.koi.utils.freeSpace


class CodeGenerator {

    fun generateATSCode(message: String): String{
        val encryptString = HASH.sha256(message)
        return encryptString
    }

    // Yes, this is overkill...
    fun getMessageToGenerate(context: Context): String{
        val deviceTime = DateTime.getDeviceCurrentTime()
        val epochTime = DateTime.getEpochTime()
        val deviceName = Build.DEVICE
        val deviceBrand = Build.BRAND
        val manufacturer = Build.MANUFACTURER
        val buildTime = Build.TIME
        val androidVersion = currentVersion()
        val getDeviceFreeSpace = freeSpace()
        val networkOperator = context.networkOperator()
        val deviceID = Settings.Secure.ANDROID_ID
        return deviceTime + epochTime + deviceName + deviceBrand + manufacturer + buildTime +
                androidVersion + getDeviceFreeSpace + networkOperator + deviceID
    }

    fun trimATSCode(message: String): String{
        val firstSevenChar = message.substring(0,7)
        val lastSevenChar = message.substring(57, 64)
        return firstSevenChar + lastSevenChar
    }
}