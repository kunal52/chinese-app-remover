package com.kunal.chineaseappuninstaller.util

import android.content.Context

class SharedPreferenceUtil {

    companion object {


        fun saveLastUpdatedTime(context: Context, time: Long) {
            val sharedPreferences =
                context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            sharedPreferences.edit().putLong(LAST_UPDATED_DATA, time).apply()
        }

        fun getLastUpdatedTime(context: Context): Long {
            val sharedPreferences =
                context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            return sharedPreferences.getLong(LAST_UPDATED_DATA, System.currentTimeMillis())
        }

    }
}