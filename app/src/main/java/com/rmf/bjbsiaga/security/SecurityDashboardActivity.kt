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
import com.rmf.bjbsiaga.util.NotifAlarm
import com.rmf.bjbsiaga.util.SharedPref
import kotlinx.android.synthetic.main.activity_security_dashboard.*

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.random.Random

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var idJadwalBertugas: String
    private lateinit var listSiklus: ArrayList<DataSiklus>
    private lateinit var listShiftMalam: ArrayList<String>

    private lateinit var db: FirebaseFirestore
    private lateinit var siklusTodayRef: CollectionReference
    private lateinit var jadwalBertugasRef: CollectionReference

    private var NIK: Long? = 0
    private val TAG = "SecurityDashboardActivi"
    private var pergiKeDetailSiklus =false
    private var membuatSiklusBaru =false

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
            this.keDetailSiklus(0)
        }

        btn_siklus2.setOnClickListener {
            this.keDetailSiklus(1)
        }

        btn_siklus3.setOnClickListener {
            this.keDetailSiklus(2)
        }

        btn_siklus4.setOnClickListener {
            this.keDetailSiklus(3)
        }
    }

    private fun keDetailSiklus(index: Int){
        if(listSiklus.size >0){
            pergiKeDetailSiklus=true
            val intent = Intent(this,DetailSiklusActivity::class.java)
            intent.putExtra("siklus", listSiklus[index].siklusKe)
            intent.putExtra("id", listSiklus[index].documentId)
            startActivity(intent)
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

    @SuppressLint("SimpleDateFormat")
    fun setAlarmForLastDataSiklus(){
        val data = listSiklus[listSiklus.size-1]
        Log.e(TAG, "setAlarmForLastDataSiklus: sudah beres? ${data.sudahBeres}" )
        if(!data.sudahBeres){
            try {
                val df = SimpleDateFormat("HH.mm")
                val calendar = Calendar.getInstance()
                calendar.time =df.parse(data.pukul)

                NotifAlarm.set(this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE))
            }
            catch (e: Exception){
                Log.e(TAG, "setAlarmForLastDataSiklus: $e")
            }
        }
    }

    fun checkJadwalBertugas(){
        Log.d(TAG, "checkJadwalBertugas: nikPetugas : $NIK, tanggal: ${dateNow()}")
        jadwalBertugasRef.whereEqualTo("nikPetugas",NIK)
            .whereEqualTo(TANGGAL_FIELD, dateNow())
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(document in it){
                        val dataJadwalBertugas = document.toObject(DataJadwalBertugas::class.java)
                        dataJadwalBertugas.documentId = document.id
                        idJadwalBertugas= dataJadwalBertugas.documentId
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
        listSiklus.clear()
        siklusTodayRef.whereEqualTo(TANGGAL_FIELD,dateNow())
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (document in it){
                        val dataSiklus = document.toObject(DataSiklus::class.java)
                        dataSiklus.documentId = document.id
                        listSiklus.add(dataSiklus)
                        Log.d(TAG, "loadDataSiklus: Siklus ke ${listSiklus.size}")
                        ambilWaktu(dataSiklus.pukul)

                    }
                    enableButton()
                    checkSiklusSudahBeres()
                    setAlarmForLastDataSiklus()
                    Log.d(TAG, "loadDataSiklus: ada ${listSiklus.size}")
                }else{
                    Log.d(TAG, "loadDataSiklus: Kosong ${listSiklus.size}")
                    insertSiklusBaru()
                }
            }
    }

    private fun enableButton() {
        for(i in 1..listSiklus.size){
            when(i){
                1 -> btn_siklus1.isEnabled=true
                2 -> btn_siklus2.isEnabled=true
                3 -> btn_siklus3.isEnabled=true
                4 -> btn_siklus4.isEnabled=true
            }
        }

    }

    private fun insertSiklusBaru() {
        val dataSiklus = DataSiklus(
            "siklus ${listSiklus.size+1}",
            randomWaktu(listSiklus.size),
            listSiklus.size+1,
            dateNow(),
            false,
            idJadwalBertugas
            )
        siklusTodayRef.document().set(dataSiklus)
            .addOnSuccessListener {
                Log.d(TAG, "insertSiklusBaru: Berhasil")
                loadDataSiklus()
            }
    }

    private fun initDB() {
        listSiklus = ArrayList()
        db = FirebaseFirestore.getInstance()
        siklusTodayRef = db.collection(CollectionsFS.SIKLUS)
        jadwalBertugasRef = db.collection(CollectionsFS.JADWAL_BERTUGAS)
    }

    override fun onResume() {
        super.onResume()
        if(pergiKeDetailSiklus){
            membuatSiklusBaru=false
            pergiKeDetailSiklus=false
            loadDataSiklus()
            Log.d(TAG, "onResume:  LoadDataSiklus")
        }

    }
    private fun checkSiklusSudahBeres(){
        Log.d(TAG, "checkSiklusSudahBeres: list size ${listSiklus.size} ")

        listSiklus.sortWith(compareBy { it.siklusKe })

        val lastData = listSiklus[listSiklus.size-1]
        Log.d(TAG, "checkSiklusSudahBeres: ${lastData.documentId} ${lastData.nama} ${lastData.sudahBeres}")

        for((i, data) in listSiklus.withIndex()){
            Log.d(TAG, "checkSiklusSudahBeres: $i ${data.documentId}")
        }

        if(lastData.sudahBeres && listSiklus.size<4){
            Log.d(TAG, "checkSiklusSudahBeres: ${lastData.documentId}")
            buatSiklusBaru()
        }
    }

    private fun buatSiklusBaru() {

        val siklusKeBaru =listSiklus.size+1
        Log.d(TAG, "buatSiklusBaru: kadie $siklusKeBaru")

        if(listSiklus.size<5 && !membuatSiklusBaru){
            membuatSiklusBaru=true
            val dataSiklus = DataSiklus(
                "siklus ${siklusKeBaru}",
                randomWaktu(listSiklus.size),
                siklusKeBaru,
                dateNow(),
                false,idJadwalBertugas)
            siklusTodayRef.document()
                .set(dataSiklus)
                .addOnSuccessListener {
                    Log.d(TAG, "buatSiklusBaru: berhasil")

                    loadDataSiklus()
                }
                .addOnFailureListener {e->
                    Log.e(TAG, "buatSiklusBaru: gagal $e")
                }
        }
    }

    override fun onBackPressed() {

        alertDialogBuilder.show()

    }
    lateinit var alertDialogBuilder : AlertDialog.Builder
    private fun initDialog(){
        alertDialogBuilder = AlertDialog.Builder(this).apply {
            setTitle("Keluar Aplikasi")
            setMessage("Anda yakin ingin keluar?")
            setPositiveButton("Ya"){ _, _ ->
                super.onBackPressed()
            }
            setNegativeButton("Tidak"){ dialog, _ ->
                dialog.dismiss()
            }
        }

    }
    @SuppressLint("SimpleDateFormat")
    private fun ambilWaktu(waktu: String){
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
    @SuppressLint("SimpleDateFormat")
    fun randomWaktu(siklus: Int): String{
        return try {
            val df = SimpleDateFormat("HH.mm")
            val calendar = Calendar.getInstance()
            calendar.time =df.parse(listShiftMalam[siklus])
            Log.d(TAG, "dari: jam:${calendar.get(Calendar.HOUR_OF_DAY)}, menit:${calendar.get(Calendar.MINUTE)}")

            val calendar2 = Calendar.getInstance()

            calendar2.time =df.parse(listShiftMalam[siklus+1])

            calendar2.time =df.parse(listShiftMalam[siklus+1])
            Log.d(TAG, "sampai: jam:${calendar2.get(Calendar.HOUR_OF_DAY)}, menit:${calendar.get(Calendar.MINUTE)}")

            val randomJamDari = calendar.get(Calendar.HOUR_OF_DAY)
            val randomJamSampai = calendar2.get(Calendar.HOUR_OF_DAY) -1

            Log.d(TAG, "randomWaktu: dari jam ${calendar.get(Calendar.HOUR_OF_DAY)} - $randomJamSampai")

            val hasilJamSampai: Calendar = Calendar.getInstance()
            hasilJamSampai.apply {
                set(Calendar.HOUR_OF_DAY, randomJamSampai)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val randomJam = if(randomJamDari==22){
                val listJam :IntArray = intArrayOf(22,23,0)
                val index =  Random.nextInt(listJam.size)
                listJam[index]
            }else{
                Random.nextInt(randomJamDari,randomJamSampai)
            }
            val randomMinute = Random.nextInt(0,30)
            Log.d(TAG, "randomWaktu: hasil random : $randomJam.$randomMinute ")

            "$randomJam.$randomMinute"

        } catch (e: ParseException){
            Log.e(TAG, "ramdomWaktu: $e" )
            "0.0"
        }
    }


}