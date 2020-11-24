package com.rmf.bjbsiaga.security

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.DetailSiklusActivity
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSiklus
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import com.rmf.bjbsiaga.util.SharedPref
import kotlinx.android.synthetic.main.activity_security_dashboard.*

class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var listSiklus: ArrayList<DataSiklus>
    private lateinit var db : FirebaseFirestore
    private lateinit var siklusTodayRef: CollectionReference

    private var nama=""
    private  val TAG = "SecurityDashboardActivi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_dashboard)

        //check is Login
        if(SharedPref.getInstance(this)!!.isLoggedIn()){
            text_nama.text = SharedPref.getInstance(this)!!.loggedInUser()
        }

        initDialog()
        initDB()
        loadDataSiklus()

        btn_siklus1.setOnClickListener {
            if(listSiklus.size >0){
                val intent = Intent(this,DetailSiklusActivity::class.java)
                intent.putExtra("siklus", listSiklus[0].siklusKe)
                intent.putExtra("id", listSiklus[0].documentId)
                startActivity(intent)
            }
        }

        btn_siklus2.setOnClickListener {
            when(btn_siklus2.isEnabled){
                true -> btn_siklus2.isEnabled=false
                else -> btn_siklus2.isEnabled=true
            }
        }

        btn_siklus3.setOnClickListener {
            when(btn_siklus3.isEnabled){
                true -> btn_siklus3.isEnabled=false
                else -> btn_siklus3.isEnabled=true
            }
        }

        btn_siklus4.setOnClickListener {
            when(btn_siklus4.isEnabled){
                true -> btn_siklus4.isEnabled=false
                else -> btn_siklus4.isEnabled=true
            }
        }
    }

    private fun loadDataSiklus(){
        listSiklus = ArrayList()
        siklusTodayRef.whereEqualTo(TANGGAL_FIELD,dateNow())
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (document in it){
                        val dataSiklus = document.toObject(DataSiklus::class.java)
                        dataSiklus.documentId = document.id
                        listSiklus.add(dataSiklus)
                    }

                    Log.d(TAG, "loadDataSiklus: ada ${listSiklus.size}")
                }else{
                    Log.d(TAG, "loadDataSiklus: Kosong")
                }
            }
    }
    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        siklusTodayRef = db.collection(CollectionsFS.SIKLUS)
    }

    override fun onBackPressed() {

        alertDialogBuilder.show()

    }
    lateinit var alertDialogBuilder : AlertDialog.Builder
    fun initDialog(){
        alertDialogBuilder = AlertDialog.Builder(this).apply {
            setTitle("Keluar Aplikasi")
            setMessage("Anda yakin ingin keluar?")
            setPositiveButton("Ya"){dialog, which ->
                finish()
            }
            setNegativeButton("Tidak"){dialog, which ->
                dialog.dismiss()
            }
        }

    }

}