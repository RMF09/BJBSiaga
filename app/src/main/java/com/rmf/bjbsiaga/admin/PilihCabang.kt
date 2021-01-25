package com.rmf.bjbsiaga.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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

    private lateinit var db : FirebaseFirestore
    private lateinit var cabangRef: CollectionReference
    private val TAG = "PilihCabang"

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_cabang)

        initDialog()
        initDB()
        setupRV()

        back.setOnClickListener { finish() }

        btn_add.setOnClickListener{ alertDialog.show()}
    }

    private fun initDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_tambah_kcp,null)

        val editNama: EditText = view.findViewById(R.id.edit_nama_cabang)
        val btnTambah: AppCompatButton = view.findViewById(R.id.btn_tambah_dialog_cabang)

        builder.setView(view)
        alertDialog =  builder.create()

        btnTambah.setOnClickListener {
           if(editNama.text.toString().isEmpty()){
               Snackbar.make(it,"Nama Cabang Tidak Boleh Kosong", Snackbar.LENGTH_LONG)
           }
           else{
               tambahCabang(editNama.text.toString())
           }
        }
    }

    private fun tambahCabang(namaCabang: String) {
        val dataCabang = DataCabang(namaCabang,0)

        cabangRef.document().set(dataCabang)
            .addOnSuccessListener {
                Log.d(TAG, "tambahCabang: Berhasil ditambahkan")
            }
            .addOnFailureListener {
                Log.d(TAG, "tambahCabang: Gagal $it")
            }

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
        Intent(this,DataRuanganActivity::class.java).apply {
            putExtra("id_cabang",dataCabang.documentId)
            putExtra("nama_cabang",dataCabang.namaCabang)
            startActivity(this)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}