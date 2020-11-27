package com.rmf.bjbsiaga.security

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.DetailSiklusActivity
import com.rmf.bjbsiaga.LoginActivity
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwalBertugas
import com.rmf.bjbsiaga.data.DataSiklus
import com.rmf.bjbsiaga.util.CollectionsFS

import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import com.rmf.bjbsiaga.util.SharedPref
import kotlinx.android.synthetic.main.activity_security_dashboard.*
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var listSiklus: ArrayList<DataSiklus>
    private lateinit var listShiftMalam: ArrayList<String>

    private lateinit var db: FirebaseFirestore
    private lateinit var siklusTodayRef: CollectionReference
    private lateinit var jadwalBertugasRef: CollectionReference

    private var NIK: Long? = 0
    private val TAG = "SecurityDashboardActivi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_dashboard)
        text_nama.text=""

        initListMalam()
        initDialog()
        initDB()

        //check is Login and get nama
        if(SharedPref.getInstance(this)!!.isLoggedIn()){
            SharedPref.getInstance(this)!!.loggedInUser()?.let {
                db.collection(CollectionsFS.SECURITY).document(it)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()){

                            NIK = documentSnapshot.getLong("nik")
                            text_nama.text = documentSnapshot.getString("nama")
                            AnimationUtils.loadAnimation(this,R.anim.fade_in).apply {
                                text_nama.startAnimation(this)
                            }
                            Log.e(TAG, "onCreate: ${documentSnapshot.getString("nama")}" )
                            checkJadwalBertugas()
                        }
                        else{
                            Log.e(TAG, "onCreate: Tidak ada User")
                        }
                    }
            }
        }else{
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }



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

    private fun initListMalam() {
        listShiftMalam = ArrayList()
        listShiftMalam.apply {
            add("19.00")
            add("22.00")
            add("01.00")
            add("04.00")
            add("07.00")
        }
    }

    fun checkJadwalBertugas(){
        jadwalBertugasRef.whereEqualTo("nikPetugas",NIK)
            .whereEqualTo(TANGGAL_FIELD, dateNow())
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(document in it){
                        val dataJadwalBertugas = document.toObject(DataJadwalBertugas::class.java)
                        dataJadwalBertugas.documentId = document.id
                    }
                    loadDataSiklus()
                }
                else{
                    Log.d(TAG, "checkJadwalBertugas: Tidak ada jadwal hari ini")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "checkJadwalBertugas: $it")
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
                        Log.d(TAG, "loadDataSiklus: Siklus ke ${listSiklus.size}")
                        enableButton()
                        ambilWaktu(dataSiklus.pukul)
                    }

                    Log.d(TAG, "loadDataSiklus: ada ${listSiklus.size}")
                }else{
                    Log.d(TAG, "loadDataSiklus: Kosong ${listSiklus.size}")
                    insertSiklusBaru()
                }
            }
    }

    private fun enableButton() {
        when(listSiklus.size){
            1 -> btn_siklus1.isEnabled=true
        }
    }

    private fun insertSiklusBaru() {
        val dataSiklus = DataSiklus(
            "siklus ${listSiklus.size+1}",
            listShiftMalam[listSiklus.size],
            listSiklus.size+1,
            dateNow()
            )
        siklusTodayRef.document().set(dataSiklus)
            .addOnSuccessListener {
                Log.d(TAG, "insertSiklusBaru: Berhasil")
                loadDataSiklus()
            }
    }

    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        siklusTodayRef = db.collection(CollectionsFS.SIKLUS)
        jadwalBertugasRef = db.collection(CollectionsFS.JADWAL_BERTUGAS)
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
    @SuppressLint("SimpleDateFormat")
    fun ambilWaktu(waktu: String){
        try {
            val df = SimpleDateFormat("HH.mm")
            val calendar = Calendar.getInstance()
            calendar.time =df.parse(waktu)
            Log.d(TAG, "ambilWaktu: jam:${calendar.get(Calendar.HOUR_OF_DAY)}, menit:${calendar.get(Calendar.MINUTE)}")

        }
        catch (e: ParseException){
            Log.e(TAG, "ambilWaktu: $e" )
        }
    }
    fun randomWaktu(){

    }


}