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
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import org.sp.attendance.R

class SlideIntro : IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        isFullscreen
        super.onCreate(savedInstanceState)
        !isButtonCtaVisible
        isButtonNextVisible
        isButtonBackVisible
        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK

        addSlide(SimpleSlide.Builder()
                .title(R.string.app_name)
                .description("Presence checking using Nearby API")
                .background(R.color.color_startup_screen1)
                .backgroundDark(R.color.color_dark_startup_screen1)
                .scrollable(true)
                .build())

        addSlide(SimpleSlide.Builder()
                .title("Class management")
                .description("Manage your student's attendance in real time!")
                .image(R.drawable.ic_people_black_50dp)
                .canGoBackward(true)
                .background(R.color.color_startup_screen2)
                .backgroundDark(R.color.color_dark_startup_screen2)
                .scrollable(true)
                .build())

        addSlide(SimpleSlide.Builder()
                .title("Device independent time")
                .description("0 configuration required!")
                .image(R.drawable.ic_access_time_black_24dp)
                .background(R.color.color_startup_screen3)
                .backgroundDark(R.color.color_dark_startup_screen3)
                .canGoBackward(true)
                .scrollable(true)
                .build())
    }
}
