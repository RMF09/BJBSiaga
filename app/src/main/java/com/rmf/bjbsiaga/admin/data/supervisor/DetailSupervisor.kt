package com.rmf.bjbsiaga.admin.data.supervisor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_detail_supervisor.*

class DetailSupervisor : AppCompatActivity() {

    companion object{
        const val TAG= "DetailSuperVis"
        const val REQUEST_CODE =9911
    }
    private lateinit var id: String
    private lateinit var dataUser: DataUser

    private lateinit var db: FirebaseFirestore
    private lateinit var userRef: CollectionReference

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_supervisor)

        initDB()
        dataUser = intent.getParcelableExtra("data")!!
        dataUser.apply {
            text_nama_lengkap.text = nama
            text_nik.text = nik.toString()
        }
        id =  intent.getStringExtra("id")!!
        Log.d(TAG, "onCreate: id: $id ")

        edit.setOnClickListener {
            Intent(this,EditSupervisorActivity::class.java).apply {
                putExtra("id",id)
                putExtra("data",dataUser)
                startActivityForResult(this, REQUEST_CODE)
            }
        }

        delete.setOnClickListener { showHapusDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: $resultCode")
        if(requestCode == REQUEST_CODE){
            dataUser = data?.getParcelableExtra("data")!!
            text_nama_lengkap.text = dataUser.nama
            text_nik.text = dataUser.nik.toString()
        }
    }

    private fun showHapusDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Konfirmasi Hapus Data")
            setMessage("Anda yakin ingin menghapus data supervisor ini?")
            setPositiveButton("Ya"
            ) { dialog, _->
                dialog.dismiss()
                hapusData()
            }
            setNegativeButton("Batal"
            ){ dialog, _-> dialog.dismiss() }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun hapusData() {
        userRef.document(id).delete()
            .addOnSuccessListener {
                showDialog("Data Supervisor berhasil dihapus","Berhasil")
            }
            .addOnFailureListener {
                showDialog("Data Supervisor gagal dihapus","Kesalahan")
            }
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        userRef = db.collection(CollectionsFS.USER)
    }
    private fun showDialog(message: String, title: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton("OK"
            ) { dialog, _ ->
                dialog.dismiss()
                if(title == "Berhasil")
                    finish()
            }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }
}