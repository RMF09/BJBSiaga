package com.rmf.bjbsiaga.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterRuangan
import com.rmf.bjbsiaga.adapter.RVAdapterSecurity
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_ruangan.*
import java.io.Serializable


class DataRuanganActivity : AppCompatActivity(), RVAdapterRuangan.ClickListener {
    lateinit var list : ArrayList<DataRuangan>
    lateinit var adapter : RVAdapterRuangan

    lateinit var db : FirebaseFirestore
    lateinit var ruanganRef: CollectionReference

    private  val TAG = "DataRuangan"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_ruangan)
        initDB()
        setupRV()
        setupAdapter()
        loadSecurity()

        btn_add.setOnClickListener {
            startActivity(Intent(this,TambahRuanganActivity::class.java))
        }
        back.setOnClickListener {
            finish()
        }
    }

    fun setupRV(){
        rv_data_ruangan.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    fun setupAdapter(){
        list = ArrayList()
        adapter = RVAdapterRuangan(list,this)
        rv_data_ruangan.adapter =adapter

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
        ruanganRef = db.collection(CollectionsFS.RUANGAN)
    }
    fun loadSecurity(){
        ruanganRef.get()
            .addOnSuccessListener {
                for (document in it){
                    val dataRuangan : DataRuangan = document.toObject(DataRuangan::class.java)
                    list.add(dataRuangan)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onClickListener(dataRuangan: DataRuangan) {
        Intent().apply {
            putExtra("data",dataRuangan as Serializable)
        }
    }
}