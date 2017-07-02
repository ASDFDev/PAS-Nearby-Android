package org.sp.attendance.utils.Ntp

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
import java.util.*

class SntpConsumer(private var context: Context){

    private var currentTime: Date? = null

    fun getNtpTime(): Date? {
        val sntpFactory = SntpFactory(context)
        currentTime = sntpFactory.execute().get()
        return currentTime
    }

}