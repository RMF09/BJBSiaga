package com.rmf.bjbsiaga

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus
import com.rmf.bjbsiaga.data.DataDetailSiklus
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config
import com.rmf.bjbsiaga.util.Config.Companion.ID_SIKLUS
import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import com.rmf.bjbsiaga.util.Config.Companion.jamSekarang
import kotlinx.android.synthetic.main.activity_detail_siklus.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class DetailSiklusActivity : AppCompatActivity() {

    private lateinit var listRuangan : ArrayList<DataRuangan>
    private lateinit var list : ArrayList<DataDetailSiklus>
    private lateinit var adapter : RVAdapterSiklus

    private lateinit var db : FirebaseFirestore
    private lateinit var ruanganRef: CollectionReference
    private lateinit var detailSiklusRef: CollectionReference
    private lateinit var siklusRef: CollectionReference
    private lateinit var storageReference: StorageReference
    private lateinit var siklusTodayRef: CollectionReference

    private val TAG = "DetailSiklusActivity"

    private var idSiklus =""
    private var siklus=0
    private var id : String? = ""
    private var idRuanganTerpilih: String? =""
    private var positionSelected: Int=0
    private var diCheckSelected: Boolean=false
    private var fotoSelected: String?=""

    private lateinit var alertDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView
    private lateinit var textHeader: TextView
    private lateinit var btnOK: Button


    private lateinit var alertDialogCompleteSiklus : AlertDialog
    private lateinit var btnOKCompleteSiklus: Button

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            // Get extra data included in the Intent
            id = intent.getStringExtra("id")
            idRuanganTerpilih = intent.getStringExtra("id_ruangan")
            positionSelected = intent.getIntExtra("position_selected",0)
            diCheckSelected = intent.getBooleanExtra("di_check",false)
            fotoSelected = intent.getStringExtra("foto")

            Log.d(TAG, "onReceive: id: $id, id ruangan terpilih: $idRuanganTerpilih, posisi : $positionSelected, diCheck : $diCheckSelected")
            checkLastData()

        }
    }

    private fun checkLastData() {
        if(id.equals("nothing_checked")){
            btn_open_camera_qr.apply {
                isEnabled=false
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorDisable))
                }
            }

            btn_open_camera.apply {
                isEnabled=false
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorDisable))
                }
            }
        }
        else{
            btn_open_camera_qr.apply {
                isEnabled = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorAccent))
                }
            }

            btn_open_camera.apply {
                isEnabled = true
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorAccent))
                }
            }

            if(!diCheckSelected){
                btn_open_camera.apply {
                    isEnabled=false
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorDisable))
                    }
                }
            }
            else{
                btn_open_camera_qr.apply {
                    isEnabled=false
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        backgroundTintList = ColorStateList.valueOf(getColor(R.color.colorDisable))
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_siklus)

        initDialog()
        initDialogCompleteSiklus()
        idSiklus = intent.getStringExtra("id").toString()
        siklus = intent.getIntExtra("siklus", 0)
        text_header_siklus.text = "Detail Siklus $siklus"

        storageReference = FirebaseStorage.getInstance().getReference("foto")
        initDB()
        setupRV()
        setupAdapter()
        loadResourceRuangan()

        back.setOnClickListener {
            finish()
        }

        btn_open_camera_qr.setOnClickListener {
            IntentIntegrator(this).apply {
                setOrientationLocked(true)
                setBeepEnabled(false)
                captureActivity = CapturePotrait::class.java
                initiateScan()
            }
        }

        btn_open_camera.setOnClickListener {
            startActivityForResult(Intent(this,MainActivity::class.java),1)
        }
    }

    fun siklusBeres(){
        Log.d(TAG, "siklusBeres: id $idSiklus")
        idSiklus.let {
            siklusRef.document(it)
                .update("sudahBeres",true)
                .addOnSuccessListener {
                    Log.d(TAG, "siklusBeres: success")
                    alertDialogCompleteSiklus.show()
                }
                .addOnFailureListener { e->
                    Log.e(TAG, "siklusBeres: gagal $e" )
                }
        }
    }
    
    private fun checkIsDone(){
        val totalData = list.size
        var sudahDicek=0
        for(data in list){
            if(data.diCheck && data.foto.isNotEmpty()){
                sudahDicek++
                Log.d(TAG, "checkIsDone: chek data $sudahDicek")
            }
        }
        if(sudahDicek==totalData){
            Log.d(TAG, "checkIsDone: complete!!!")
            siklusBeres()
        }
    }

    private fun updateData(name: String){
        id?.let {
            detailSiklusRef.document(it)
                .update("foto",name)
                .addOnSuccessListener {
                    Log.d(TAG, "updateData: Berhasil update Data")
                    //loadData()
                }
                .addOnFailureListener { e ->
                    Log.d(TAG, "updateData: Fail : $e")
                }
        }
    }
    private fun checkQR(){
        val dataDetailSiklus = list[positionSelected]
        dataDetailSiklus.diCheck=true
        dataDetailSiklus.pukul = jamSekarang()

        id?.let {
            detailSiklusRef.document(it)
//                .update("diCheck",true)
                .set(dataDetailSiklus, SetOptions.merge())
                .addOnFailureListener { e->
                    Log.e(TAG, "checkQR: $e" )

                }
                .addOnSuccessListener {
                    Log.d(TAG, "checkQR: berhasil")
                    loadData()
                }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun uploadFoto(file: File){
        textHeader.text = "Mengupload foto, mohon tunggu... "
        alertDialog.show()

        val name = "${System.currentTimeMillis()}.jpg"
        val fileReference = storageReference.child(name)

        fileReference.putFile(Uri.fromFile(file))
            .addOnSuccessListener {
                Log.d(TAG, "uploadFoto: success")
                updateData(name)
                textHeader.text= "Berhasil mengupload foto"
                btnOK.isEnabled = true

            }
            .addOnFailureListener {
                Log.e(TAG, "uploadFoto: $it")
                textHeader.text = "Gagal Upload"
            }
            .addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred / it.totalByteCount)
                progressBar.progress = progress.toInt()
                textProgress.text = "${progressBar.progress}%"
                Log.d(TAG, "uploadFoto: progress $progress")
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d(TAG, "onActivityResult: $requestCode")
        if(requestCode==1){
            if(resultCode== RESULT_OK){
                Log.d(TAG, "onActivityResult: Tina kamera photo")
                //Toast.makeText(this,data!!.getStringExtra("msg"),Toast.LENGTH_SHORT).show()
                val file = data!!.extras!!.get("file") as File
                uploadFoto(file)
            }
        }else{
            val result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
            if(result!=null){
                if(result.contents==null){ Log.d(TAG, "onActivityResult: QR Kosong") }
                else{ Log.d(TAG, "onActivityResult: ${result.contents}")
                    try {
                        JSONObject(result.contents).apply {
                            val idRuangan = this.get("idRuangan")
                            if(idRuangan == idRuanganTerpilih){
                                checkQR()
                            }else{ Toast.makeText(this@DetailSiklusActivity,"Salah scan tempat!",Toast.LENGTH_LONG).show() }
                        }
                    }
                    catch (e: Exception){ Log.e(TAG, "onActivityResult: $e" ) }
                }
            }
            else{ super.onActivityResult(requestCode, resultCode, data) }
        }

    }

    override fun onStart() {
        super.onStart()


//        detailSiklusRef.whereEqualTo(TANGGAL_FIELD, dateNow()).whereEqualTo("idSiklus", idSiklus)
//            .addSnapshotListener(this, object : EventListener<QuerySnapshot> {
//                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
//
//                    if (error != null) {
//                        return
//                    }
//                    adapter.selected=true
//                    list.clear()
//                    adapter.notifyDataSetChanged()
//                    for (document in value!!) {
//                        val dataSiklus = document.toObject(DataDetailSiklus::class.java)
//                        list.add(dataSiklus)
//                    }
//                    adapter.notifyDataSetChanged()
//
//                }
//
//            })
    }


    private fun insertData(){
        var i =0
        for (data in listRuangan){
            val dataDetailSiklus = DataDetailSiklus(
                data.namaRuangan,
                "",
                false,
                data.documentId,
                idSiklus,
                dateNow(),
                ""
            )
            detailSiklusRef.document().set(dataDetailSiklus)
                .addOnSuccessListener {
                    i++
                    Log.d(TAG, "insertData: berhasil insert $i")
                    if(i==listRuangan.size-1){
                        loadData()
                    }
                }
                .addOnFailureListener { Log.e(TAG, "insertData: ${it.toString()}") }
        }
    }


    private fun loadData() {
        adapter.selected=false
        detailSiklusRef.whereEqualTo(TANGGAL_FIELD, dateNow()).whereEqualTo(ID_SIKLUS, idSiklus)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    list.clear()
                    adapter.notifyDataSetChanged()
                    Log.e(TAG, "loadData: Ada")
                    for (document in it){
                        val dataSiklus = document.toObject(DataDetailSiklus::class.java)
                        dataSiklus.documentId = document.id
                        list.add(dataSiklus)
                    }
                    adapter.notifyDataSetChanged()
                    checkIsDone()

                }else{
                    Log.e(TAG, "loadData: Kosong, InsertKan!!!")
                    insertData()
                }
            }
            .addOnFailureListener { Log.e(TAG, "loadData: ${it.toString()}") }
    }


    private fun loadResourceRuangan() {
        ruanganRef.get()
            .addOnSuccessListener {
            if(!it.isEmpty){
                for ((i, document) in it.withIndex()){
                    val dataRuangan: DataRuangan = document.toObject(DataRuangan::class.java)
                    listRuangan.add(dataRuangan)
                    listRuangan.get(i).documentId = document.id
                    Log.d(
                        TAG,
                        "loadResourceRuangan: ${listRuangan[i].namaRuangan}, ${listRuangan[i].documentId}"
                    )
                }
                //end for
                loadData()
            }
        }
            .addOnFailureListener { Log.e(TAG, "loadResourceRuangan: ${it.toString()}") }
    }


    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        ruanganRef = db.collection(CollectionsFS.RUANGAN)
        detailSiklusRef = db.collection(CollectionsFS.DETAIL_SIKLUS)
        siklusRef = db.collection(CollectionsFS.SIKLUS)
        this.siklusTodayRef = db.collection(CollectionsFS.SIKLUS)
    }

    private fun setupRV(){
        rv_data_siklus.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun setupAdapter(){
        listRuangan = ArrayList()
        list = ArrayList()
        adapter = RVAdapterSiklus(list)
        rv_data_siklus.adapter= adapter
    }

    private fun initDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_progress,null)

        textHeader = view.findViewById(R.id.text_header_progress)
        progressBar = view.findViewById(R.id.progress_bar)
        textProgress = view.findViewById(R.id.text_progress)
        btnOK = view.findViewById(R.id.btn_ok_dialog_progress)

        builder.setView(view)
        alertDialog = builder.create()
        alertDialog.setCancelable(false)

        btnOK.setOnClickListener {
            alertDialog.dismiss()
            btnOK.isEnabled=false
            loadData()
        }
    }

    fun initDialogCompleteSiklus(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_konfirmasi_ruangan_telah_selesai,null)

        btnOKCompleteSiklus = view.findViewById(R.id.btn_ok_dialog_detail_siklus)

        builder.setView(view)
        alertDialogCompleteSiklus = builder.create()
        alertDialogCompleteSiklus.setCancelable(false)
        alertDialogCompleteSiklus.window?.setWindowAnimations(R.style.DialogAnimation)

        btnOKCompleteSiklus.setOnClickListener {

            alertDialogCompleteSiklus.dismiss()
            Timer("finish",false).schedule(1000){
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(
                broadcastReceiver,
                IntentFilter(Config.ACTION_DATA_DETAIL_SIKLUS)
            )
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }
}