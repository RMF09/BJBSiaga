package com.rmf.bjbsiaga.util

import android.content.Context
import android.net.ConnectivityManager

class CheckConnection {

    companion object{
        @Suppress("DEPRECATION")
        fun isConnected(context: Context): Boolean{
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            return (wifiCon !=null && wifiCon.isConnected) || (mobileCon!=null && mobileCon.isConnected)
        }
    }
}