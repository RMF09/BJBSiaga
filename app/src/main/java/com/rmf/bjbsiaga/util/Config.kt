package com.rmf.bjbsiaga.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Config {
    companion object{
        val ID_SIKLUS= "idSiklus"
        val EMAIL_SUDAH_DIGUNAKAN ="com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."
        val TGL ="d-MM-yyyy"
        val JAM ="HH.mm"
        val TANGGAL_FIELD= "tanggal"
        val ACTION_DATA_DETAIL_SIKLUS="data-detail-siklus"
        val USER_LOGIN_ADMIN = "admin"
        val USER_LOGIN_USER ="user"

        @SuppressLint("SimpleDateFormat")
        fun dateNow(): String{
            val sdf = SimpleDateFormat(TGL)
            return sdf.format(Date())
        }
        @SuppressLint("SimpleDateFormat")
        fun jamSekarang(): String{
            val sdf = SimpleDateFormat(JAM)
            return sdf.format(Date())
        }

    }
}