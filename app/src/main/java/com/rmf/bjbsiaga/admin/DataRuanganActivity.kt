package com.rmf.bjbsiaga.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterRuangan
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_ruangan.*




class DataRuanganActivity : AppCompatActivity(), RVAdapterRuangan.ClickListener {
    lateinit var list : ArrayList<DataRuangan>
    lateinit var adapter : RVAdapterRuangan

    lateinit var db : FirebaseFirestore
    lateinit var ruanganRef: CollectionReference
    lateinit var cabangRef: CollectionReference

    private  val TAG = "DataRuangan"
    private var idCabang: String=""
    private var namaCabang: String=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_ruangan)

        idCabang = intent.getStringExtra("id_cabang").toString()
        namaCabang = intent.getStringExtra("nama_cabang").toString()
        header_text.text = "Data Ruangan $namaCabang"

        initDB()
        setupRV()
        setupAdapter()

        loadDataRuangan(false)

        btn_add.setOnClickListener {
            Intent(this,TambahRuanganActivity::class.java).apply {
                putExtra("id_cabang",idCabang)
                startActivityForResult(this,2)
            }
        }
        back.setOnClickListener {
            finish()
        }
    }

    private fun setupRV(){
        rv_data_ruangan.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    private fun setupAdapter(){
        list = ArrayList()
        adapter = RVAdapterRuangan(list,this)
        rv_data_ruangan.adapter =adapter

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
        ruanganRef = db.collection(CollectionsFS.RUANGAN)
        cabangRef = db.collection(CollectionsFS.CABANG)
    }
    fun loadDataRuangan(statusTambah: Boolean){
        list.clear()
        ruanganRef.whereEqualTo("idCabang",idCabang)
            .get()
            .addOnSuccessListener {
                for (document in it){
                    val dataRuangan : DataRuangan = document.toObject(DataRuangan::class.java)
                    dataRuangan.documentId = document.id
                    list.add(dataRuangan)
                }
                adapter.notifyDataSetChanged()

                if(statusTambah){
                    updateCountRuanganCabang(list.size)
                }
            }
    }

    override fun onClickListener(dataRuangan: DataRuangan, context: Context) {
        Intent(this,DetailRuangan::class.java).apply {
            Log.d(TAG, "onClickListener id: ${dataRuangan.documentId}")
            putExtra("id",dataRuangan.documentId)
            putExtra("data",dataRuangan)
            startActivityForResult(this,2)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==2){
            val statusTambahRuangan = data?.getBooleanExtra("status",false)

            statusTambahRuangan?.let { loadDataRuangan(it) }
            Log.d(TAG, "onActivityResult: status : $statusTambahRuangan")
        }
    }

    private fun updateCountRuanganCabang(size: Int){
        Log.d(TAG, "updateCountRuanganCabang: jumlah Ruangan = $size")
        cabangRef.document(idCabang).update("jumlahRuangan",size)
            .addOnSuccessListener {
                Log.d(TAG, "updateCountRuanganCabang: Berhasil")
            }
            .addOnFailureListener {
                Log.e(TAG, "updateCountRuanganCabang: Gagal $it")
            }
    }
}