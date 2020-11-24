package com.rmf.bjbsiaga

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus
import com.rmf.bjbsiaga.data.DataDetailSiklus
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config
import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import kotlinx.android.synthetic.main.activity_detail_siklus.*


class DetailSiklusActivity : AppCompatActivity() {

    private lateinit var listRuangan : ArrayList<DataRuangan>
    private lateinit var list : ArrayList<DataDetailSiklus>
    private lateinit var adapter : RVAdapterSiklus

    private lateinit var db : FirebaseFirestore
    private lateinit var ruanganRef: CollectionReference
    private lateinit var siklusRef: CollectionReference

    private val TAG = "DetailSiklusActivity"

    private var idSiklus =""
    private var siklus=0

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val id = intent.getStringExtra("id")

            Toast.makeText(this@DetailSiklusActivity, "IdDocument : $id", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_siklus)

        idSiklus = intent.getStringExtra("id").toString()
        siklus = intent.getIntExtra("siklus", 0)
        text_header_siklus.text = "Detail Siklus $siklus"

        initDB()
        setupRV()
        setupAdapter()
        loadResourceRuangan()

        back.setOnClickListener {
            finish()
        }

        btn_open_camera_qr.setOnClickListener {
            IntentIntegrator(this).apply {
                setOrientationLocked(true)
                captureActivity = CapturePotrait::class.java
                initiateScan()
            }
        }

        btn_open_camera.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){
            if(result.contents==null){
                Log.d(TAG, "onActivityResult: QR Kosong")
            }
            else{
                Log.d(TAG, "onActivityResult: ${result.contents}")
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    override fun onStart() {
        super.onStart()
        siklusRef.whereEqualTo(TANGGAL_FIELD, dateNow()).whereEqualTo("idSiklus", idSiklus)
            .addSnapshotListener(this, object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null) {
                        return
                    }
                    list.clear()
                    adapter.notifyDataSetChanged()
                    for (document in value!!) {
                        val dataSiklus = document.toObject(DataDetailSiklus::class.java)
                        list.add(dataSiklus)
                    }
                    adapter.notifyDataSetChanged()

                }

            })
    }


    private fun insertData(){
        var i =0
        for (data in listRuangan){

            val dataDetailSiklus = DataDetailSiklus(
                data.namaRuangan,
                "",
                false,
                data.documentId,
                idSiklus,
                dateNow()
            )
            siklusRef.document().set(dataDetailSiklus)
                .addOnSuccessListener {
                    i++
                    Log.d(TAG, "insertData: berhasil insert $i")
                    if(i==listRuangan.size-1){
                        loadData()
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "insertData: ${it.toString()}")
                }
        }
    }


    private fun loadData() {
        siklusRef.whereEqualTo(TANGGAL_FIELD, dateNow()).whereEqualTo("idSiklus", idSiklus)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    list.clear()
                    adapter.notifyDataSetChanged()
                    Log.e(TAG, "loadData: Ada")
                    for (document in it){
                        val dataSiklus = document.toObject(DataDetailSiklus::class.java)
                        dataSiklus.documentId = document.id
                        list.add(dataSiklus)
                    }
                    adapter.notifyDataSetChanged()

                }else{
                    Log.e(TAG, "loadData: Kosong, InsertKan!!!")
                    insertData()
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadData: ${it.toString()}")
            }
    }


    private fun loadResourceRuangan() {
        ruanganRef.get()
            .addOnSuccessListener {

            if(!it.isEmpty){
                for ((i, document) in it.withIndex()){
                    val dataRuangan: DataRuangan = document.toObject(DataRuangan::class.java)
                    listRuangan.add(dataRuangan)
                    listRuangan.get(i).documentId = document.id
                    Log.d(
                        TAG,
                        "loadResourceRuangan: ${listRuangan[i].namaRuangan}, ${listRuangan[i].documentId}"
                    )
                }
                //end for
                loadData()
            }
        }
            .addOnFailureListener {
                Log.e(TAG, "loadResourceRuangan: ${it.toString()}")
            }
    }


    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        ruanganRef = db.collection(CollectionsFS.RUANGAN)
        siklusRef = db.collection(CollectionsFS.DETAIL_SIKLUS)
    }

    private fun setupRV(){
        rv_data_siklus.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupAdapter(){
        listRuangan = ArrayList()
        list = ArrayList()

        adapter = RVAdapterSiklus(list)
        rv_data_siklus.adapter= adapter
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(Config.ACTION_DATA_DETAIL_SIKLUS)
            )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }




}