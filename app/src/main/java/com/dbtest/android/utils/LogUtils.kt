package com.dbtest.android.utils

import android.util.Log
import androidx.viewbinding.BuildConfig

fun loggerDebug(tag: String, message: String) {
    if (BuildConfig.DEBUG)
        Log.d(tag, message)
}

fun loggerException(tag: String, throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, "Error: ", throwable)
    }
}