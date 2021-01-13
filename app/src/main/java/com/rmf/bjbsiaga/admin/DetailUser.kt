package com.rmf.bjbsiaga.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_detail_user.*

class DetailUser : AppCompatActivity() {
    private  val TAG = "DetailUser"
    private lateinit var id : String
    private lateinit var dataSecurity: DataSecurity

    lateinit var db : FirebaseFirestore
    lateinit var securityRef: CollectionReference
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

        initDB()

        dataSecurity = intent.getParcelableExtra<DataSecurity>("data").apply {
            text_nama_lengkap.text = this?.nama
            nik.text = this?.nik.toString()
            email.text = this?.email
            phone.text = this?.noWA.toString()
            unit_kerja.text = this?.unitKerja
        }!!
        id = intent.getStringExtra("id").toString()

        Log.d(TAG, "onCreate: Document ID = $id")
        back.setOnClickListener {
            finish()
        }

        edit.setOnClickListener {
            Intent(this,EditUser::class.java).apply {
                putExtra("id",id)
                putExtra("data",dataSecurity)
                startActivity(this)
            }
        }
        delete.setOnClickListener { showHapusDialog() }
    }

    private fun showHapusDialog(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage("Anda yakin ingin menghapus data security ini?")
            setTitle("Konfirmasi Hapus Data")
            setPositiveButton("Ya"
            ) { dialog, _ ->
                dialog.dismiss()
                hapusData()
            }
            setNegativeButton("Batal"
            ){ dialog, _ -> dialog.dismiss() }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        securityRef = db.collection(CollectionsFS.SECURITY)
    }

    private fun hapusData() {
        securityRef.document(id)
            .delete()
            .addOnSuccessListener {
                showDialog("Data Security berhasil dihapus","Berhasil")
            }
            .addOnFailureListener {
                showDialog("Data Security gagal dihapus","Kesalahan")
            }
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