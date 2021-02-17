package com.rmf.bjbsiaga.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

@Suppress("DEPRECATION")
class TimeAuto {
    companion object{
        private lateinit var alertDialog: AlertDialog

        fun isEnabled(context: Context){

            if(this::alertDialog.isInitialized && alertDialog.isShowing){
                alertDialog.dismiss()
            }
            if(check(context)){
                val builder = AlertDialog.Builder(context)
                builder.apply {
                    setTitle("Peringatan!")
                    setMessage("Harap waktu otomatis diaktifkan di pengaturan perangkat")
                    setCancelable(false)
                }
                alertDialog =  builder.create()
                alertDialog.show()
            }
        }

        private fun check(context: Context): Boolean{
            return Settings.Global.getInt(context.contentResolver,Settings.Global.AUTO_TIME,0) ==0
        }
    }
}