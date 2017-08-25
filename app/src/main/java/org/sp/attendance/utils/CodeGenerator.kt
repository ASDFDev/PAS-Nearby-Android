package org.sp.attendance.utils

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