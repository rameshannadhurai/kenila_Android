package com.dbtest.android.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.ParseException
import android.os.Build
import androidx.annotation.NonNull
import com.google.gson.JsonParseException
import com.dbtest.android.R
import retrofit2.HttpException
import java.net.ConnectException
import java.net.UnknownHostException

object NetworkHelper {
    private val TAG = NetworkHelper::class.java.simpleName
    fun isNetworkConnected(@NonNull context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }

        return result
    }

    fun handleThrowable(t: Throwable): Int {
        val stringId: Int
        when (t) {
            is UnknownHostException -> {
                stringId = R.string.unknown_host_exception
            }
            is ConnectException -> {
                stringId = R.string.your_in_offline
            }
            is ParseException, is JsonParseException -> {
                stringId = R.string.parser_exception
            }
            is HttpException -> {
                when (t.code()) {
                    ConstantFields.BAD_REQUEST -> {
                        stringId = R.string.bad_request
                    }
                    ConstantFields.INTERNAL_SERVER_ERROR -> {
                        stringId = R.string.internal_server_exception
                    }
                    else -> {
                        stringId = R.string.un_caught_exception
                    }
                }
            }
            else -> {
                loggerException(TAG, t)
                stringId = R.string.un_caught_exception
            }
        }
        return stringId
    }

}