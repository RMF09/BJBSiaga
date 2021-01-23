package com.rmf.bjbsiaga.admin

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterCabang
import com.rmf.bjbsiaga.data.DataCabang
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_pilih_cabang.*

class PilihCabang : AppCompatActivity(), RVAdapterCabang.ClickListener {

    private var list: ArrayList<DataCabang> = ArrayList()
    private lateinit var adapter:RVAdapterCabang

    lateinit var db : FirebaseFirestore
    lateinit var cabangRef: CollectionReference
    private  val TAG = "PilihCabang"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_cabang)
        initDB()
        setupRV()
        loadData()

        back.setOnClickListener { finish() }

    }

    private fun loadData() {
        cabangRef.get()
            .addOnSuccessListener {
                Log.d(TAG, "loadData: ${it.size()}")
                if(!it.isEmpty){
                    for (dokumen in it){
                        val dataCabang: DataCabang = dokumen.toObject(DataCabang::class.java)
                        dataCabang.documentId = dokumen.id
                        list.add(dataCabang)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "loadData: $it")
            }
    }

    private fun setupRV() {
        rv_data_cabang.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        adapter = RVAdapterCabang(list,this)
        rv_data_cabang.adapter=adapter
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        cabangRef = db.collection(CollectionsFS.CABANG)
    }

    override fun onClickListener(dataCabang: DataCabang, context: Context) {

    }
}