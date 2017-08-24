package org.sp.attendance.utils

import android.content.Context
import android.os.Build
import com.mcxiaoke.koi.HASH
import com.mcxiaoke.koi.ext.getBatteryStatus
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
        val manufacturer = Build.MANUFACTURER
        val androidVersion = currentVersion()
        val deviceName = Build.DEVICE
        val deviceBrand = Build.BRAND
        val getDeviceFreeSpace = freeSpace()
        val networkOperator = context.networkOperator()
        val batteryStatusIntent = context.getBatteryStatus()
        return deviceTime + manufacturer + androidVersion +
                deviceName + deviceBrand + getDeviceFreeSpace +
                networkOperator + batteryStatusIntent
    }

    fun trimATSCode(message: String): String{
        val firstSevenChar = message.substring(0,7)
        val lastSevenChar = message.substring(57, 64)
        return firstSevenChar + lastSevenChar
    }

}