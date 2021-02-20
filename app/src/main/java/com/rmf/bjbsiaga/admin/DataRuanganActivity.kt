package com.rmf.bjbsiaga.admin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterRuangan
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_ruangan.*
import kotlinx.android.synthetic.main.activity_data_ruangan.back
import kotlinx.android.synthetic.main.activity_data_ruangan.btn_add
import kotlinx.android.synthetic.main.activity_data_ruangan.progress_bar


class DataRuanganActivity : AppCompatActivity(), RVAdapterRuangan.ClickListener {
    lateinit var list : ArrayList<DataRuangan>
    lateinit var adapter : RVAdapterRuangan

    lateinit var db : FirebaseFirestore
    lateinit var ruanganRef: CollectionReference
    lateinit var cabangRef: CollectionReference

    private val TAG = "DataRuangan"
    private var idCabang: String=""
    private var namaCabang: String=""

    private lateinit var alertDialog: AlertDialog
    private var berhasilDihapus = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_ruangan)

        idCabang = intent.getStringExtra("id_cabang").toString()
        namaCabang = intent.getStringExtra("nama_cabang").toString()
        header_text.text = "Data Ruangan $namaCabang"
        initDialogHapus()
        initDB()
        setupRV()
        setupAdapter()

        loadDataRuangan(false)

        btn_add.setOnClickListener {
            Intent(this,TambahRuanganActivity::class.java).apply {
                putExtra("id_cabang",idCabang)
                putExtra("nama_cabang",namaCabang)
                startActivityForResult(this,2)
            }
        }

        btn_delete.setOnClickListener {
            setMessageForDialog("","Anda yakin ingin menghapus Cabang $namaCabang beserta data ruangannya?")
            alertDialog.show()
        }

        back.setOnClickListener {
            finish()
        }
    }

    private fun initDialogHapus(){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Hapus Cabang")
            setMessage("Anda yakin ingin menghapus namacabang?")
            setPositiveButton("Ya"){dialog, which->
                //do nothing
            }
            setNegativeButton("Batal"){dialog,_->
                dialog.dismiss()
            }
        }
        alertDialog = builder.create()
        alertDialog.setCancelable(false)

        alertDialog.setOnShowListener {
            val button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                if(!berhasilDihapus){
                    setMessageForDialog("","Harap tunggu...")
                    alertDialog.apply {
                        getButton(AlertDialog.BUTTON_POSITIVE).visibility = View.GONE
                        getButton(AlertDialog.BUTTON_NEGATIVE).visibility = View.GONE
                    }

                    hapusCabang()

                }else{
                    alertDialog.dismiss()
                    finish()
                }
            }
        }
    }

    private fun setMessageForDialog(title: String="", message: String){
        if(title!=""){
            alertDialog.setTitle(title)
        }
        alertDialog.setMessage(message)
    }
    private fun hapusCabang(){
        val query: Query = ruanganRef.whereEqualTo("idCabang",idCabang)
        query.get().addOnCompleteListener {
            if(it.isSuccessful){
                for (data in it.result){
                    ruanganRef.document(data.id).delete()
                }
                cabangRef.document(idCabang).delete()
                    .addOnSuccessListener {
                        setMessageForDialog("Berhasil dihapus","Data Cabang $namaCabang berhasil dihapus")
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).visibility=View.GONE
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).visibility=View.VISIBLE
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).text = "OK"
                        berhasilDihapus=true
                    }
                    .addOnFailureListener {ex->
                        Log.e(TAG, "hapusCabang: $ex")
                    }
            }
        }.addOnFailureListener {
            Log.e(TAG, "hapusCabang: $it" )
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
        progress_bar.visibility = View.VISIBLE
        text_belum_ada_data.visibility = View.GONE

        list.clear()
        ruanganRef.whereEqualTo("idCabang",idCabang)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (document in it){
                        val dataRuangan : DataRuangan = document.toObject(DataRuangan::class.java)
                        dataRuangan.documentId = document.id
                        list.add(dataRuangan)
                    }
                    adapter.notifyDataSetChanged()
                    progress_bar.visibility = View.GONE
                    if(statusTambah){
                        updateCountRuanganCabang(list.size)
                    }
                }else{
                    text_belum_ada_data.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadDataRuangan: $it" )
                progress_bar.visibility = View.GONE
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