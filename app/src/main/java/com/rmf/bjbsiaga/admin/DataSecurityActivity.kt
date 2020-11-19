package com.rmf.bjbsiaga.admin

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterSecurity
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.services.AlarmReceiver
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_security.*
import java.util.*
import kotlin.collections.ArrayList


class DataSecurityActivity : AppCompatActivity() {

    lateinit var list : ArrayList<DataSecurity>
    lateinit var adapter : RVAdapterSecurity

    lateinit var db : FirebaseFirestore
    lateinit var securityRef: CollectionReference


    private  val TAG = "DataSecurity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_security)

        initDB()
        setupRV()
        setupAdapter()


        startAt10()
        loadSecurity()

        back.setOnClickListener {
            finish()
        }
        btn_add.setOnClickListener {
            startActivity(Intent(this, TambahDataSecurityActivity::class.java))
        }
    }
    fun setupRV(){
        rv_data_security.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    fun setupAdapter(){
        list = ArrayList()
        adapter = RVAdapterSecurity(list)
        rv_data_security.adapter =adapter

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
//        val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
//        db.firestoreSettings =settings

        securityRef = db.collection(CollectionsFS.SECURITY)
    }
    fun loadSecurity(){
        securityRef.whereEqualTo("nama","Ilham").get()
            .addOnSuccessListener {
                for (document in it){
                    val dataSecurity : DataSecurity = document.toObject(DataSecurity::class.java)
                    Log.d(TAG, "loadSecurity: ${dataSecurity.email}")
                    list.add(dataSecurity)
                }
                adapter.notifyDataSetChanged()
            }
    }

    fun startAt10() {
        val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, alarmIntent, 0)

        val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 5)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.time < Date()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent
            )
        }
    }
}