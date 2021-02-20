package com.rmf.bjbsiaga.security

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus
import com.rmf.bjbsiaga.data.DataDetailSiklus
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.Config
import com.rmf.bjbsiaga.util.Config.Companion.ID_SIKLUS
import com.rmf.bjbsiaga.util.Config.Companion.TANGGAL_FIELD
import com.rmf.bjbsiaga.util.Config.Companion.dateNow
import com.rmf.bjbsiaga.util.Config.Companion.jamSekarang
import com.rmf.bjbsiaga.util.MapUtils.Companion.getMarkerIcon
import com.rmf.bjbsiaga.util.RMFRequestCode
import kotlinx.android.synthetic.main.activity_detail_siklus.*
import kotlinx.android.synthetic.main.activity_detail_siklus.back
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class DetailSiklusActivity : AppCompatActivity(),OnMapReadyCallback{

    private lateinit var listRuangan : ArrayList<DataRuangan>
    private lateinit var list : ArrayList<DataDetailSiklus>
    private lateinit var adapter : RVAdapterSiklus

    private lateinit var db : FirebaseFirestore
    private lateinit var ruanganRef: CollectionReference
    private lateinit var detailSiklusRef: CollectionReference
    private lateinit var siklusRef: CollectionReference
    private lateinit var storageReference: StorageReference
    private lateinit var siklusTodayRef: CollectionReference

    companion object{

        const val TAG = "DetailSiklusActivity"
        const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    private var idSiklus =""
    private var siklus=0
    private var id : String? = ""
    private var idRuanganTerpilih: String? =""
    private var positionSelected: Int=0
    private var diCheckSelected: Boolean=false
    private var fotoSelected: String?=""
    private var namaLokasiSelected:String?=""

    private lateinit var alertDialog: AlertDialog
    private lateinit var progressBar: ProgressBar
    private lateinit var textProgress: TextView
    private lateinit var textHeader: TextView
    private lateinit var btnOK: Button

    private lateinit var alertDialogCompleteSiklus : AlertDialog
    private lateinit var btnOKCompleteSiklus: Button

    private var latRuanganTerpilih =0.0
    private var lngRuanganTerpilih =0.0
    private var latMyLocation =0.0
    private var lngMyLocation =0.0
    private lateinit var mMap: GoogleMap
    private var dalamJangkauanRuangan =false

    private lateinit var alertDialogDiluarJangkauan: AlertDialog

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var unitKerja=""

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {

            // Get extra data included in the Intent
            id = intent.getStringExtra("id")
            idRuanganTerpilih = intent.getStringExtra("id_ruangan")
            positionSelected = intent.getIntExtra("position_selected", 0)
            diCheckSelected = intent.getBooleanExtra("di_check", false)
            fotoSelected = intent.getStringExtra("foto")
            namaLokasiSelected = intent.getStringExtra("nama_lokasi")

            Log.d(
                TAG,
                "onReceive: id: $id, id ruangan terpilih: $idRuanganTerpilih, posisi : $positionSelected, diCheck : $diCheckSelected"
            )
            checkLastData()
            getCoordinatRuangan()

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

        var mapViewBundle :Bundle? =null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        btn_open_camera.isEnabled=false
        btn_open_camera_qr.isEnabled=false


        mapView2.onCreate(mapViewBundle)
        mapView2.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchMyLocation()

        initDialog()
        initDialogCompleteSiklus()


        idSiklus = intent.getStringExtra("id").toString()
        siklus = intent.getIntExtra("siklus", 0)
        unitKerja = intent.getStringExtra("unit_kerja").toString()

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
            if(dalamJangkauanRuangan){
                IntentIntegrator(this).apply {
                    setOrientationLocked(true)
                    setBeepEnabled(false)
                    captureActivity = CapturePotrait::class.java
                    initiateScan()
                }
            }else{
                namaLokasiSelected?.let { it1 -> showDialogDiluarJangkauan(it1) }
            }
        }

        btn_open_camera.setOnClickListener {
            startActivityForResult(Intent(this, Camera::class.java), 1)
        }
    }

    private fun siklusBeres(){
        Log.d(TAG, "siklusBeres: id $idSiklus")
        idSiklus.let {
            siklusRef.document(it)
                .update("sudahBeres", true)
                .addOnSuccessListener {
                    Log.d(TAG, "siklusBeres: success")
                    alertDialogCompleteSiklus.show()
                }
                .addOnFailureListener { e->
                    Log.e(TAG, "siklusBeres: gagal $e")
                }
        }
    }

    private fun getCoordinatRuangan(){
        idRuanganTerpilih?.let {
            ruanganRef.document(it)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if(documentSnapshot.exists()){
                        val dataRuangan = documentSnapshot.toObject(DataRuangan::class.java)
                        latRuanganTerpilih = dataRuangan!!.lat
                        lngRuanganTerpilih = dataRuangan.lng

                        Log.d(TAG, "getCoordinatRuangan: $latRuanganTerpilih, $lngRuanganTerpilih")
                        mapView2.getMapAsync(this)
                    }
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
                .update("foto", name)
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
                    Log.e(TAG, "checkQR: $e")

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
                file.delete()

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
        if(requestCode==RMFRequestCode.REQUEST_CODE_FROM_CAMERA){
            if(resultCode== RESULT_OK){
                Log.d(TAG, "onActivityResult: Tina kamera photo")
                //Toast.makeText(this,data!!.getStringExtra("msg"),Toast.LENGTH_SHORT).show()
                val file = data!!.extras!!.get("file") as File
                uploadFoto(file)
            }
        }else{
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result!=null){
                if(result.contents==null){ Log.d(TAG, "onActivityResult: QR Kosong") }
                else{ Log.d(TAG, "onActivityResult: ${result.contents}")
                    try {
                        JSONObject(result.contents).apply {
                            val idRuangan = this.get("idRuangan")
                            if(idRuangan == idRuanganTerpilih){
                                checkQR()
                            }else{ Toast.makeText(
                                this@DetailSiklusActivity,
                                "Salah scan tempat!",
                                Toast.LENGTH_LONG
                            ).show() }
                        }
                    }
                    catch (e: Exception){ Log.e(TAG, "onActivityResult: $e") }
                }
            }
            else{ super.onActivityResult(requestCode, resultCode, data) }
        }

    }

    override fun onStop() {
        super.onStop()
        mapView2.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView2.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView2.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        mapView2.onStart()


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
                .addOnFailureListener { Log.e(TAG, "insertData: $it") }
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
            .addOnFailureListener { Log.e(TAG, "loadData: $it") }
    }


    private fun loadResourceRuangan() {
        ruanganRef.whereEqualTo("nama_cabang",unitKerja).get()
            .addOnSuccessListener {
            if(!it.isEmpty){
                for ((i, document) in it.withIndex()){
                    val dataRuangan: DataRuangan = document.toObject(DataRuangan::class.java)
                    listRuangan.add(dataRuangan)
                    listRuangan[i].documentId = document.id
                    Log.d(
                        TAG,
                        "loadResourceRuangan: ${listRuangan[i].namaRuangan}, ${listRuangan[i].documentId}"
                    )
                }
                //end for
                loadData()
            }
        }
            .addOnFailureListener { Log.e(TAG, "loadResourceRuangan: $it") }
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

    private fun showDialogDiluarJangkauan(namaLokasi : String){
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle("Peringatan!")
            setMessage("Tidak dapat memindai QRCode, Anda diluar jangkauan ruangan $namaLokasi")
            setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
            setCancelable(false)
        }
        alertDialogDiluarJangkauan = builder.create()
        alertDialogDiluarJangkauan.show()
    }

    private fun initDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_progress, null)

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

    private fun initDialogCompleteSiklus(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_konfirmasi_ruangan_telah_selesai, null)

        btnOKCompleteSiklus = view.findViewById(R.id.btn_ok_dialog_detail_siklus)

        builder.setView(view)
        alertDialogCompleteSiklus = builder.create()
        alertDialogCompleteSiklus.setCancelable(false)
        alertDialogCompleteSiklus.window?.setWindowAnimations(R.style.DialogAnimation)

        btnOKCompleteSiklus.setOnClickListener {

            alertDialogCompleteSiklus.dismiss()
            Timer("finish", false).schedule(1000){
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
        mapView2.onPause()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onPause()
        mapView2.onPause()
    }

    override fun onMapReady(p0: GoogleMap) {

        mMap = p0
        mMap.clear()
        Log.d(TAG, "onMapReady: ADA")
        val myLocation = LatLng(latMyLocation, lngMyLocation)
        mMap.addMarker(MarkerOptions().position(myLocation).title("Disini"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18f))

        val roomLocation = LatLng(latRuanganTerpilih, lngRuanganTerpilih)
        val icon =
            ContextCompat.getDrawable(this,
                R.drawable.ic_baseline_location_on_24
            )?.let { it1 ->
                getMarkerIcon(it1)
            }
        mMap.addMarker(MarkerOptions().position(roomLocation).title("Ruangan").icon(icon))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(roomLocation, 18f))

        //Circle
        val circleOptions = CircleOptions()
        circleOptions.apply {
            center(roomLocation)
            radius(5.0)
            fillColor(Color.parseColor("#6817A2B8"))
            strokeWidth(1f)
            strokeColor(Color.parseColor("#17a2b8"))
        }
//        1km radius = 100.0 double
        //1m radius = 0.1 double

        mMap.addCircle(circleOptions)

        val jarak = FloatArray(2)
        Location.distanceBetween(myLocation.latitude,myLocation.longitude,circleOptions.center.latitude,circleOptions.center.longitude,jarak)

        dalamJangkauanRuangan = jarak[0] <= circleOptions.radius

        Log.d(TAG, "onMapReady: dalamJangkauan : $dalamJangkauanRuangan")
        

    }

    private fun fetchMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                val myLocation = LatLng(location.latitude, location.longitude)
                Log.d(TAG, "fetchMyLocation: ${myLocation.latitude}, ${myLocation.longitude}")
                latMyLocation = myLocation.latitude
                lngMyLocation = myLocation.longitude
                mapView2.getMapAsync(this)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView2.onSaveInstanceState(mapViewBundle)
    }



}