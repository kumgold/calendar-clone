package com.goldcompany.apps.data.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson

class LongConverter {
    @TypeConverter
    fun listToJson(value: List<Long>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<Long>? {
        return Gson().fromJson(value, Array<Long>::class.java)?.toList()
    }
}