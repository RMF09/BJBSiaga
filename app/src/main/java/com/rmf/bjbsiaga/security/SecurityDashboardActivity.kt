package com.rmf.bjbsiaga.security

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.rmf.bjbsiaga.DialogSelesaiActivity
import com.rmf.bjbsiaga.LoginActivity
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwalBertugas
import com.rmf.bjbsiaga.data.DataSiklus
import com.rmf.bjbsiaga.data.DataTugasSiaga
import com.rmf.bjbsiaga.util.*
import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import kotlinx.android.synthetic.main.activity_security_dashboard.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SecurityDashboardActivity : AppCompatActivity() {

    private lateinit var idJadwal: String
    private lateinit var idJadwalBertugas: String
    private lateinit var idTugasSiaga: String
    private lateinit var listSiklus: ArrayList<DataSiklus>
    private lateinit var listShiftMalam: ArrayList<String>
    private lateinit var listShiftPagi: ArrayList<String>

    private lateinit var db: FirebaseFirestore
    private lateinit var jadwalRef: CollectionReference
    private lateinit var siklusTodayRef: CollectionReference
    private lateinit var jadwalBertugasRef: CollectionReference
    private lateinit var tugasSiagaRef: CollectionReference

    companion object{
        private var NIK: Long? =0
        private const val TAG = "SecurityDashboardActivi"
    }
    private var pergiKeDetailSiklus =false
    private var membuatSiklusBaru =false

    private var hari: String= ""
    private var shift: String= ""

    private var statusTugasSiaga= false
    private var tanggalTugasSiga= ""
    private var tampilDialogSiklusComplete=false


    private var checkHariKamari  = false
    private var currentCalendar=  Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_dashboard)
        text_nama.text=""

        initListShift()
        initDialog()
        initDB()

        checkPermission()

        //check is Login and get nama
        if(SharedPref.getInstance(this)!!.isLoggedIn()){
            SharedPref.getInstance(this)!!.loggedInUser()?.let {
                db.collection(CollectionsFS.SECURITY).document(it)
                    .get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()){

                            NIK = documentSnapshot.getLong("nik")
                            text_nama.text = documentSnapshot.getString("nama")
                            AnimationUtils.loadAnimation(this, R.anim.fade_in).apply {
                                text_nama.startAnimation(this)
                            }
                            Log.e(TAG, "onCreate: ${documentSnapshot.getString("nama")}")
                            val hariSekarang = Config.Today()
                            checkJadwalBertugas(hariSekarang)
                        }
                        else{
                            Log.e(TAG, "onCreate: Tidak ada User")
                        }
                    }
            }
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
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
    private fun checkPermission(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
//                    if(p0!!.areAllPermissionsGranted()){
//                        start()
//                    }
                }
            })
            .check()
    }

    private fun keDetailSiklus(index: Int){
        if(listSiklus.size >0){
            pergiKeDetailSiklus=true
            Intent(this, DetailSiklusActivity::class.java).apply {
                putExtra("siklus", listSiklus[index].siklusKe)
                putExtra("id", listSiklus[index].documentId)
                startActivity(this)
            }
        }
    }

    private fun initListShift() {
        listShiftMalam = ArrayList()
        listShiftMalam.apply {
            add("19.00")
            add("22.00")
            add("01.00")
            add("04.00")
            add("07.00")
        }
        listShiftPagi = ArrayList()
        listShiftPagi.apply {
            add("7.00")
            add("10.00")
            add("13.00")
            add("16.00")
            add("19.00")
        }
    }


    @SuppressLint("SimpleDateFormat")
    fun setAlarmForLastDataSiklus(){
        val data = listSiklus[listSiklus.size - 1]
        Log.e(TAG, "setAlarmForLastDataSiklus: sudah beres? ${data.sudahBeres}")
        if(!data.sudahBeres){
            try {
                val df = SimpleDateFormat("d-MM-yyyy HH.mm")
                val calendar = Calendar.getInstance()
                val time = "${data.tanggal} ${data.pukul}"
                calendar.time =df.parse(time)
                currentCalendar.apply {
                    set(Calendar.HOUR_OF_DAY,calendar.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE,calendar.get(Calendar.MINUTE))
                }


                NotifAlarm.set(
                    this,
                    currentCalendar)
            }
            catch (e: Exception){
                Log.e(TAG, "setAlarmForLastDataSiklus: $e")
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkJadwalBertugas(day: String){
        Log.d(TAG, "checkJadwalBertugas: nikPetugas : $NIK")

        Log.d(TAG, "checkJadwalBertugas: Hari: $day")
        jadwalBertugasRef.whereEqualTo("nikPetugas", NIK)
            .whereEqualTo("hari", day)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for(document in it){
                        val dataJadwalBertugas = document.toObject(DataJadwalBertugas::class.java)
                        dataJadwalBertugas.documentId = document.id
                        idJadwalBertugas= dataJadwalBertugas.documentId
                        idJadwal = dataJadwalBertugas.idJadwal
                        hari = dataJadwalBertugas.hari
                        shift = dataJadwalBertugas.shift
                    }

                        checkTugasSiaga()
                }
                else{
                    Log.d(TAG, "checkJadwalBertugas: Tidak ada jadwal untuk anda")

                    if(!checkHariKamari){
                        //cek kamari
                        val calendar =  Calendar.getInstance()
                        calendar.add(Calendar.DAY_OF_MONTH,-1)

                        currentCalendar = calendar
                        val namaHari =  SimpleDateFormat("EEEE").format(currentCalendar.time)
                        Log.d(TAG, "checkJadwalBertugas: hari kamari : $namaHari")

                        checkJadwalBertugas(namaHari)
                        checkHariKamari=true

                    }
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "checkJadwalBertugas: $it")
            }
    }



    private fun checkTugasSiaga(){

        //ambil tugas terakhir
        tugasSiagaRef.whereEqualTo("idJadwalBertugas", idJadwalBertugas)
            .orderBy(TANGGAL_FIELD, Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (document in it){
                        val dataTugasSiaga = document.toObject(DataTugasSiaga::class.java)
                        dataTugasSiaga.documentId = document.id
                        idTugasSiaga = dataTugasSiaga.documentId
                        statusTugasSiaga = dataTugasSiaga.statusSudahBeres
                        tanggalTugasSiga = Config.convertTanggalTimeStamp2(dataTugasSiaga.tanggal)
                        Log.d(
                            TAG, "checkTugasSiaga:  ${dataTugasSiaga.tanggal} ${
                                dataTugasSiaga.tanggal?.let { it1 ->
                                    Config.convertTanggalTimeStamp(
                                        it1
                                    )
                                }
                            }"
                        )

                        if(!statusTugasSiaga){
                            Log.d(TAG, "checkTugasSiaga: Sudah Beres : $statusTugasSiaga")
                            if(shift=="Malam" && checkHariKamari){
                                //Kondisi jam 00.00 - 08.00
                                val jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                                if(jam < 8){
                                    loadDataSiklus()
                                }else{
                                    Log.d(TAG, "checkTugasSiaga: Shift Malam Sudah Berakhir ")
                                }
                            }else{
                                loadDataSiklus()
                            }
                        }
                        else{
                            Log.d(TAG, "checkTugasSiaga: Sudah Beres : $statusTugasSiaga")
                            tampilkanBeresBersiaga()
                        }

                    }
                }
                else{
                    Log.d(TAG, "checkTugasSiaga: Tidak ada tugas siaga")
                    addTugasSiaga()
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "checkTugasSiaga: $it ")
            }
    }

    private fun addTugasSiaga(){
        val dataTugasSiaga = DataTugasSiaga(idJadwalBertugas, false, Date())
        tugasSiagaRef.document().set(dataTugasSiaga)
            .addOnSuccessListener {
                Log.d(TAG, "addTugasSiaga: berhasil")
                checkTugasSiaga()
            }
            .addOnFailureListener {
                Log.e(TAG, "addTugasSiaga: $it")
            }
    }

    private fun loadDataSiklus(){

        listSiklus.clear()
        siklusTodayRef
            .whereEqualTo("idTugasSiaga",idTugasSiaga)
            .orderBy("siklusKe", Query.Direction.ASCENDING)
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
//                    pukul = listSiklus[listSiklus.size]
                    enableButton()
                    checkSiklusSudahBeres()
                    setAlarmForLastDataSiklus()
                    Log.d(TAG, "loadDataSiklus: ada ${listSiklus.size}")
                }else{
                    Log.d(TAG, "loadDataSiklus: Kosong ${listSiklus.size}")
                    buatSiklusBaru()
                }
            }
    }

    @SuppressLint("SimpleDateFormat")
    private fun ambilJam(pukul: String):Int{
        val df2 = SimpleDateFormat("HH.mm")
        val calendar = Calendar.getInstance()
        calendar.time = df2.parse(pukul)

        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    @SuppressLint("SimpleDateFormat")
    private fun enableButton() {

        for((i,data) in listSiklus.withIndex()){

            try {
                val df = SimpleDateFormat("d-MM-yyyy HH.mm")
                val calendar = Calendar.getInstance()
                val time = "${data.tanggal} ${data.pukul}"
                calendar.time =df.parse(time)
                //ambil jam
                val hasilJam = ambilJam(data.pukul)
                Log.d(TAG, "enableButton: hasil  jam : $hasilJam ,check kamari $checkHariKamari")

                //Khusus shift malam
                //Kondisi saat 19 - 23, dibesokken jadwalna
                if(!checkHariKamari && shift=="Malam"){
                    if(hasilJam in 0..7){
                        calendar.add(Calendar.DAY_OF_MONTH,1)
                    }
                }

                Log.d(TAG, "enableButton: caclendar : ${calendar.time}, ayena ${Date()}")
                if(calendar.time < Date()){
                    Log.d(TAG, "enableButton: checkWaktu $i")
                    when(i){
                        0 -> btn_siklus1.isEnabled=true
                        1 -> btn_siklus2.isEnabled=true
                        2 -> btn_siklus3.isEnabled=true
                        3 -> btn_siklus4.isEnabled=true
                    }
                }
            }
            catch (e: Exception){
                Log.e(TAG, "enableButton: $e")
            }


//            if(listSiklus[i].pu)
        }

    }

    private fun buatSiklusBaru() {

        val siklusKeBaru =listSiklus.size+1
        Log.d(TAG, "buatSiklusBaru: kadie $siklusKeBaru")

        if(listSiklus.size<5 && !membuatSiklusBaru){
            membuatSiklusBaru=true
            val dataSiklus = DataSiklus(
                "siklus $siklusKeBaru",
                randomWaktu(listSiklus.size),
                siklusKeBaru,
                dateNow(),
                false,
                idTugasSiaga
            )

            siklusTodayRef.document()
                .set(dataSiklus)
                .addOnSuccessListener {
                    Log.d(TAG, "buatSiklusBaru: berhasil")

                    loadDataSiklus()
                }
                .addOnFailureListener { e->
                    Log.e(TAG, "buatSiklusBaru: gagal $e")
                }
        }
    }

//    private fun insertSiklusBaru() {
//
//        val dataSiklus = DataSiklus(
//            "siklus ${listSiklus.size + 1}",
//            randomWaktu(listSiklus.size),
//            listSiklus.size + 1,
//            dateNow(),
//            false,
//            idTugasSiaga
//        )
//        siklusTodayRef.document().set(dataSiklus)
//            .addOnSuccessListener {
//                Log.d(TAG, "insertSiklusBaru: Berhasil")
//                loadDataSiklus()
//            }
//    }

    private fun initDB() {
        listSiklus = ArrayList()
        db = FirebaseFirestore.getInstance()
        siklusTodayRef = db.collection(CollectionsFS.SIKLUS)
        jadwalBertugasRef = db.collection(CollectionsFS.JADWAL_BERTUGAS)
        tugasSiagaRef = db.collection(CollectionsFS.TUGAS_SIAGA)
        jadwalRef = db.collection(CollectionsFS.JADWAL)
    }



    override fun onResume() {
        super.onResume()
        if(pergiKeDetailSiklus){
            membuatSiklusBaru=false
            pergiKeDetailSiklus=false
            loadDataSiklus()
            Log.d(TAG, "onResume:  LoadDataSiklus")
        }

        TimeAuto.isEnabled(this)

    }
    private fun checkSiklusSudahBeres(){
        Log.d(TAG, "checkSiklusSudahBeres: list size ${listSiklus.size} ")

        listSiklus.sortWith(compareBy { it.siklusKe })

        val lastData = listSiklus[listSiklus.size - 1]
        Log.d(
            TAG,
            "checkSiklusSudahBeres: ${lastData.documentId} ${lastData.nama} ${lastData.sudahBeres}"
        )

        for((i, data) in listSiklus.withIndex()){
            Log.d(TAG, "checkSiklusSudahBeres: $i ${data.documentId}")
        }

        if(lastData.sudahBeres && listSiklus.size<4){
            Log.d(TAG, "checkSiklusSudahBeres: ${lastData.documentId}")
            buatSiklusBaru()
        }
        //Final Destination
        if(listSiklus.size==4 && lastData.sudahBeres){
            Log.d(TAG, "checkSiklusSudahBeres: Sudah beres semua! Update status Tugas Siaga! ")
            if(!tampilDialogSiklusComplete){
                tampilkanBeresBersiaga()
            }
            if(!statusTugasSiaga){
                updateStatusTugas()
            }
        }
    }

    private fun tampilkanBeresBersiaga(){
        Intent(this, DialogSelesaiActivity::class.java).apply {
            putExtra("tanggal", tanggalTugasSiga)
            startActivity(this)
        }

        tampilDialogSiklusComplete=true
    }

    private fun updateStatusTugas() {
        tugasSiagaRef.document(idTugasSiaga)
            .update("statusSudahBeres", true)
            .addOnSuccessListener {
                Log.d(TAG, "updateStatusTugas: Berhasil Update Status Tugas Siaga")
                checkTugasSiaga()
            }
            .addOnFailureListener {
                Log.e(TAG, "updateStatusTugas: $it")
            }
    }



    override fun onBackPressed() {

        alertDialogBuilder.show()

    }
    private lateinit var alertDialogBuilder : AlertDialog.Builder
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
            Log.d(
                TAG, "ambilWaktu: jam:${calendar.get(Calendar.HOUR_OF_DAY)}, menit:${
                    calendar.get(
                        Calendar.MINUTE
                    )
                }"
            )
        }
        catch (e: ParseException){
            Log.e(TAG, "ambilWaktu: $e")
        }
    }
    @SuppressLint("SimpleDateFormat")
    fun randomWaktu(siklus: Int): String{

        val listShift = if(shift=="Malam"){
            listShiftMalam
        }else{
            listShiftPagi
        }

        return try {
            val df = SimpleDateFormat("HH.mm")
            val calendar = Calendar.getInstance()
            calendar.time =df.parse(listShift[siklus])
            Log.d(
                TAG, "dari: jam:${calendar.get(Calendar.HOUR_OF_DAY)}, menit:${
                    calendar.get(
                        Calendar.MINUTE
                    )
                }"
            )

            val calendar2 = Calendar.getInstance()

            calendar2.time =df.parse(listShift[siklus + 1])

            calendar2.time =df.parse(listShift[siklus + 1])
            Log.d(
                TAG, "sampai: jam:${calendar2.get(Calendar.HOUR_OF_DAY)}, menit:${
                    calendar.get(
                        Calendar.MINUTE
                    )
                }"
            )

            val randomJamDari = calendar.get(Calendar.HOUR_OF_DAY)
            val randomJamSampai = calendar2.get(Calendar.HOUR_OF_DAY) -1

            Log.d(
                TAG,
                "randomWaktu: dari jam ${calendar.get(Calendar.HOUR_OF_DAY)} - $randomJamSampai"
            )

            val hasilJamSampai: Calendar = Calendar.getInstance()
            hasilJamSampai.apply {
                set(Calendar.HOUR_OF_DAY, randomJamSampai)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            val randomJam = if(randomJamDari==22){
                val listJam :IntArray = intArrayOf(22, 23, 0)
                val index =  Random.nextInt(listJam.size)
                listJam[index]
            }else{
                Random.nextInt(randomJamDari, randomJamSampai)
            }
            val randomMinute = Random.nextInt(0, 30)
            Log.d(TAG, "randomWaktu: hasil random : $randomJam.$randomMinute ")

            "$randomJam.$randomMinute"

        } catch (e: ParseException){
            Log.e(TAG, "ramdomWaktu: $e")
            "0.0"
        }
    }


}