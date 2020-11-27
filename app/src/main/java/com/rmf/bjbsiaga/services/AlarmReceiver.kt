package com.rmf.bjbsiaga.services

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Im Running", Toast.LENGTH_SHORT).show()
        val notifyID = 1
        val CHANNEL_ID = "alarm_security" // The id of the channel.
        val name: CharSequence = "notif alarm" // The user-visible name of the channel.

//        val notificationIntent = Intent(context, Absensi::class.java)
//        //        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        val pendingIntent = PendingIntent.getActivity(context, 99, notificationIntent, 0)

        val mNotificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
        mBuilder.apply {
            setContentTitle(String(Character.toChars(0x26A0)) + " Waktunya untuk cek ruangan")
            setContentText("Tap untuk memulai cek ruangan")
            setLargeIcon(
                BitmapFactory.decodeResource(
                    context.getResources(),R.drawable.ic_input_add
                )
            )
            setSmallIcon(R.drawable.ic_input_add)
            setAutoCancel(true)
        }
//        mBuilder.setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(notifyID, mBuilder.build())
    }

}