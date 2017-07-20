
package org.sp.attendance.ui.intro

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

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.View
import com.mcxiaoke.koi.ext.delayed
import com.mcxiaoke.koi.ext.startActivity
import org.sp.attendance.R
import org.sp.attendance.R.anim.abc_fade_in
import org.sp.attendance.R.anim.abc_fade_out
import org.sp.attendance.models.DateTime
import org.sp.attendance.service.sntp.SntpConsumer
import org.sp.attendance.ui.ATSLoginActivity
import org.sp.attendance.utils.CacheManager

class SplashScreen: AppCompatActivity() {

    var sntpConsumer = SntpConsumer(this)
    var cacheManager = CacheManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        val handler = Handler()
        cacheManager.storeTimeStampCache(DateTime.getTrueCalendarToString(sntpConsumer.getNtpTime()))
        handler.delayed(1500, { startActivity<ATSLoginActivity>() })
        overridePendingTransition(abc_fade_in, abc_fade_out)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val flags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = flags
        }
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

}