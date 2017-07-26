
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
import android.view.animation.AlphaAnimation
import android.widget.TextView
import com.mcxiaoke.koi.ext.delayed
import com.mcxiaoke.koi.ext.find
import com.mcxiaoke.koi.ext.startActivity
import org.sp.attendance.R
import org.sp.attendance.R.anim.abc_fade_in
import org.sp.attendance.R.anim.abc_fade_out
import org.sp.attendance.ui.ATSLoginActivity

class SplashScreen: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        val handler = Handler()
        textViewAnimation()
        handler.delayed(2300, {
            startActivity<ATSLoginActivity>() })
        overridePendingTransition(abc_fade_in, abc_fade_out)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            val flags =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            window.decorView.systemUiVisibility = flags
        }
    }

    fun textViewAnimation(){
        val textView = find<TextView>(R.id.textView_splash)
        val fadeIn = AlphaAnimation(0.0f , 7.0f)
        textView.startAnimation(fadeIn)
        fadeIn.duration = 3500;
    }

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

}