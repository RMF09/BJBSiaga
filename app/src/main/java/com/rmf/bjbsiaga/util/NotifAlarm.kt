package com.rmf.bjbsiaga.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.rmf.bjbsiaga.services.AlarmReceiver
import java.util.*

class NotifAlarm {

    companion object{

        fun set(applicationContext: Context,calendar: Calendar){
            Log.d("setAlarm", "set: ${calendar.get(Calendar.HOUR_OF_DAY)}.${calendar.get(Calendar.MINUTE)}")
            val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, 0)

            val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            Log.d("setAlarm", "${calendar.time}, ${Date()}")
//            if (calendar.time < Date()) {
//                //sudah terlewat besok aja
//                calendar.add(Calendar.DAY_OF_MONTH,1)
//                Log.d("setAlarm", "set: kalewat tambah hari")
//            }
            Log.d("setAlarm", "set: alarm!")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                manager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                )
            }

        }
    }
}