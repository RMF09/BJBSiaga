package com.rmf.bjbsiaga.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.rmf.bjbsiaga.LoginActivity
import com.rmf.bjbsiaga.data.DataUser

class SharedPref(context: Context) {

    val mContext = context
    companion object{

        const val SHARED_PREF_NAME = "com.rmf.bjbsiaga"
        const val NIK = "nik"
        const val ROLE = "role"
        var mInstance: SharedPref? = null

        @Synchronized
        fun getInstance(context: Context?): SharedPref? {
            if (mInstance == null) {
                mInstance = context?.let { SharedPref(it) }
            }
            return mInstance
        }
    }

    fun storeID(nik: Long) {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().apply {
            putLong(NIK, nik)
            apply()
        }

    }

    fun storeRole(role: String?) {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().apply {
            putString(ROLE, role)
            apply()
        }

    }

    //check if user is logged in
    fun isLoggedIn(): Boolean {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(ROLE, null) != null
    }


    //find logged in user
    fun loggedInUser(): Long {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getLong(NIK, 0)
    }

    //find logged in role user
    fun loggedInRole(): String? {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(ROLE, null)
    }

    //Logout user
    fun logout() {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        sharedPreferences.edit().apply {
            clear()
            apply()
            mContext.startActivity(Intent(mContext,LoginActivity::class.java))
        }
    }

}