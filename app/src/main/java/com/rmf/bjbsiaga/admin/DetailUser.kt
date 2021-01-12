package com.rmf.bjbsiaga.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import kotlinx.android.synthetic.main.activity_detail_user.*

class DetailUser : AppCompatActivity() {
    private  val TAG = "DetailUser"
    private lateinit var id : String
    private lateinit var dataSecurity: DataSecurity

    lateinit var db : FirebaseFirestore
    lateinit var securityRef: CollectionReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user)

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
    }

}