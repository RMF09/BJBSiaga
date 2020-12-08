package com.rmf.bjbsiaga.util

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class Config {
    companion object{
        val ID_SIKLUS= "idSiklus"
        val EMAIL_SUDAH_DIGUNAKAN ="com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account."
        val TGL ="d-MM-yyyy"
        val HARI ="EEEE"
        val JAM ="HH.mm"
        val TANGGAL_FIELD= "tanggal"
        val ACTION_DATA_DETAIL_SIKLUS="data-detail-siklus"
        val USER_LOGIN_ADMIN = "admin"
        val USER_LOGIN_USER ="user"
        val TGL_TIMESTAMP = "EEE MMM dd HH:mm:ss z yyyy"


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
        @SuppressLint("SimpleDateFormat")
        fun Today(): String{
            val sdf = SimpleDateFormat(HARI)
            return sdf.format(Date())
        }

        @SuppressLint("SimpleDateFormat")
        fun convertTanggalTimeStamp(timeStamp: Date?): String{
            val sdf = SimpleDateFormat(TGL)
            return sdf.format(timeStamp)
        }

        @SuppressLint("SimpleDateFormat")
        fun ambilJam(waktu: String): Int{
            return try {

                val df = SimpleDateFormat("HH.mm")
                val calendar = Calendar.getInstance()
                calendar.time =df.parse(waktu)
                calendar.get(Calendar.HOUR_OF_DAY)

            } catch (e: ParseException){
                0
            }
        }

    }
}