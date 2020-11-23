package com.rmf.bjbsiaga

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.data.DataSiklus
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_detail_siklus.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DetailSiklusActivity : AppCompatActivity() {

    private lateinit var listRuangan : ArrayList<DataRuangan>
    private lateinit var list : ArrayList<DataSiklus>
    private lateinit var adapter : RVAdapterSiklus

    private lateinit var db : FirebaseFirestore
    private lateinit var ruanganRef: CollectionReference
    private lateinit var siklusRef: CollectionReference

    private  val TAG = "DetailSiklusActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_siklus)

        initDB()
        setupRV()
        setupAdapter()
        loadResourceRuangan()

        back.setOnClickListener {
            finish()
        }


    }



    private fun insertData(){
        var i =0
        for (data in listRuangan){

            val dataSiklus = DataSiklus(data.namaRuangan,"",false,data.documentId,dateNow())
            siklusRef.document().set(dataSiklus)
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
        siklusRef.whereEqualTo("tanggal",dateNow())
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    Log.e(TAG, "loadData: Ada")
                    for (document in it){
                        val dataSiklus = document.toObject(DataSiklus::class.java)
                        list.add(dataSiklus)
                    }
                    adapter.notifyDataSetChanged()

                }else{
                    Log.e(TAG, "loadData: Kosong, InsertKan!!!" )
                    insertData()
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadData: ${it.toString()}" )
            }
    }
    @SuppressLint("SimpleDateFormat")
    private fun dateNow(): String{
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())
    }

    private fun loadResourceRuangan() {
        ruanganRef.get()
            .addOnSuccessListener {

            if(!it.isEmpty){
                for ((i, document) in it.withIndex()){
                    val dataRuangan: DataRuangan = document.toObject(DataRuangan::class.java)
                    listRuangan.add(dataRuangan)
                    listRuangan.get(i).documentId = document.id
                    Log.d(TAG, "loadResourceRuangan: ${listRuangan[i].namaRuangan}, ${listRuangan[i].documentId}")
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

}