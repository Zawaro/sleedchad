package com.zawaro.sleepchad.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringSet(value: String?): List<Int>? {
        return value?.split(",")?.filter { it.isNotEmpty() }?.mapNotNull { it.toIntOrNull() }
    }

    @TypeConverter
    fun toStringSet(list: List<Int>?): String? {
        return list?.joinToString(",")
    }
}