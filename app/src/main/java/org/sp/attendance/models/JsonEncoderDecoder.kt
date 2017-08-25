package org.sp.attendance.models

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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import org.sp.attendance.utils.AccountCheck
import java.util.*

class JsonEncoderDecoder{

    fun jsonToEncode(attendanceCode: String, timeStamp: Date): MessageModel {
        val messageModel = MessageModel()
        messageModel.username = AccountCheck.areWeDemoAccountOrSpiceAccount()
        messageModel.atsCode = attendanceCode
        messageModel.timeStamp = timeStamp
        return messageModel
    }

    fun encodeJsonToString(jsonToEncode: MessageModel): String{
        val gson = Gson()
        return gson.toJson(jsonToEncode)
    }

    fun decodeJsonATS(atsCode: String): JsonElement{
        val gson = Gson()
        val jsonObject = gson.fromJson(atsCode, JsonObject::class.java)
        return jsonObject.get("atsCode")
    }

    fun decodeJsonDate(timeStamp: String): JsonElement{
        val gson = Gson()
        val jsonObject = gson.fromJson(timeStamp, JsonObject::class.java)
        return jsonObject.get("timeStamp")
    }

    fun decodeJsonAccount(username: String): JsonElement{
        val gson = Gson()
        val jsonObject = gson.fromJson(username, JsonObject::class.java)
        return jsonObject.get("username")
    }

}