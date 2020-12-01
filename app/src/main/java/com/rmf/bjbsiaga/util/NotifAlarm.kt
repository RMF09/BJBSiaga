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

        fun set(applicationContext: Context,jam: Int, menit: Int){
            Log.d("setAlarm", "set: $jam.$menit")
            val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, 0)

            val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, jam)
            calendar.set(Calendar.MINUTE, menit)
            calendar.set(Calendar.SECOND, 0)
            if (calendar.time < Date()) {
                //sudah terlewat besok aja
                Log.d("setAlarm", "set: kalewat")
            }else{
                Log.d("setAlarm", "set: alarm!")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    manager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
                    )
                }

            }

        }
    }
}