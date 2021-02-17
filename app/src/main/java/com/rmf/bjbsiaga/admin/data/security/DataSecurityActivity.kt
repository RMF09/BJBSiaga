package com.rmf.bjbsiaga.admin.data.security

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterSecurity
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_security.*
import kotlin.collections.ArrayList


class DataSecurityActivity : AppCompatActivity(), RVAdapterSecurity.ClickListener {

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
        adapter = RVAdapterSecurity(list,this)
        rv_data_security.adapter =adapter

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
//        val settings = FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true).build()
//        db.firestoreSettings =settings

        securityRef = db.collection(CollectionsFS.SECURITY)
    }
    fun loadSecurity(){
        progress_bar.visibility = View.VISIBLE
        list.clear()
        adapter.notifyDataSetChanged()
        securityRef.get()
            .addOnSuccessListener {
                for (document in it){
                    val dataSecurity : DataSecurity = document.toObject(DataSecurity::class.java)
                    dataSecurity.documentId = document.id
                    Log.d(TAG, "loadSecurity: ${document.id}")
                    list.add(dataSecurity)
                }
                adapter.notifyDataSetChanged()
                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e(TAG, "loadSecurity: $it" )
                progress_bar.visibility = View.GONE
            }
    }

    override fun onClickListener(dataSecurity: DataSecurity, context: Context) {
        Intent(this, DetailSecurity::class.java).apply {
            putExtra("data",dataSecurity)
            putExtra("id",dataSecurity.documentId)
            context.startActivity(this)
        }
    }

    override fun onResume() {
        super.onResume()
        loadSecurity()
    }
}