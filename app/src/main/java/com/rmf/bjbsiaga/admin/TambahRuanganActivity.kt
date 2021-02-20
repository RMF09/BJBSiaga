package com.rmf.bjbsiaga.admin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.util.CollectionsFS
import com.rmf.bjbsiaga.util.MapUtils.Companion.getMarkerIcon
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream


class TambahRuanganActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val PERMISSION_LOCATION : Int =1
    private val TAG = "MapsActivity"

    lateinit var markerCenter: Marker
    var lat : Double= 0.0
    var lng : Double= 0.0

    lateinit var db : FirebaseFirestore
    private lateinit var storageReference: StorageReference
    private lateinit var alertDialog: AlertDialog
    
    private var idCabang: String =""
    private var namaCabang: String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        
        idCabang = intent.getStringExtra("id_cabang").toString()
        namaCabang = intent.getStringExtra("nama_cabang").toString()
        Log.d(TAG, "onCreate: id_cabang $idCabang")
        
        initDB()

        var mapViewBundle :Bundle? =null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
        updateGPS()


        back.setOnClickListener {
            finish()
        }
        btn_tambah_data.setOnClickListener {
            if(validate()){
                btn_tambah_data.isEnabled=false
                saveData()
            }
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
                    Intent().apply {
                        putExtra("status",true)
                        setResult(2,this)
                    }
                    finish()
            }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun uploadQR(namaRuangan: String, bitmap: Bitmap){
        val fileReference = storageReference.child(namaRuangan)
        val baos =ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()

        fileReference.putBytes(data)
            .addOnSuccessListener {
                Log.d(TAG, "uploadQR: berhasil")
                showDialog("Tambah data ruangan berhasil", "Berhasil")
                btn_tambah_data.isEnabled=true
            }
            .addOnFailureListener {
                Log.e(TAG, "uploadQR: gagal $it")
                showDialog("Tambah data Ruangan gagal. Silahkan coba lagi dan periksa jaringan Anda stabil", "Kesalahan")
                btn_tambah_data.isEnabled=true
            }
    }

    private fun generateQRCode(idRuangan: String){

        val multiFormatWriter = MultiFormatWriter()
        try {
            val jsonFormat = JSONObject()
            jsonFormat.put("idRuangan", idRuangan)
            Log.d(TAG, "generateQRCode: $jsonFormat")
            val bitMatrix : BitMatrix = multiFormatWriter.encode(
                jsonFormat.toString(),
                BarcodeFormat.QR_CODE,
                200,
                200
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)

            uploadQR(text_input_nama_ruangan.editText?.text.toString(),bitmap)
        }
        catch (je: JSONException){
            Log.e(TAG, "generateQRCode: $je")
        }
        catch (we: WriterException){
            Log.e(TAG, "generateQRCode: $we")
        }
    }

    private fun clearError(){
        text_input_nama_ruangan.error=""
        text_input_koordinat_lokasi.error=""
    }

    private fun validate():Boolean{

        clearError()

        val namaRuangan: String = text_input_nama_ruangan.editText?.text.toString()
        val koordinat: String = text_input_koordinat_lokasi.editText?.text.toString()

        if(namaRuangan.isEmpty()){
            text_input_nama_ruangan.requestFocus()
            text_input_nama_ruangan.error = "Nama Ruangan diperlukan"
            return false
        }
        if(koordinat.isEmpty()){
            text_input_koordinat_lokasi.requestFocus()
            text_input_koordinat_lokasi.error ="Koordinat lokasi diperlukan. Geser marker pada map untuk mendapatkan nilai koordinat"
            return false
        }
        return true
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("qrcode")
    }

    private fun saveData(){

        clearError()
        val namaRuangan: String = text_input_nama_ruangan.editText?.text.toString()

        val id =  db.collection(CollectionsFS.RUANGAN).document().id
        val dataRuangan = DataRuangan(namaRuangan, lat, lng,idCabang,namaCabang)
        dataRuangan.documentId = id
        Log.e(TAG, "saveData: id= $id")
        db.collection(CollectionsFS.RUANGAN).document(id).set(dataRuangan)
            .addOnSuccessListener {
                Log.d(TAG, "saveData: ${dataRuangan.documentId}")
                generateQRCode(id)

            }

            .addOnFailureListener {
                Log.e(TAG, "saveData: ${it}")
                btn_tambah_data.isEnabled=true
            }

    }

    @SuppressLint("SetTextI18n")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Disini"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        mMap.setOnCameraMoveListener {

            val test = mMap.cameraPosition
            markerCenter.position =test.target
            text_input_koordinat_lokasi.editText?.setText("${markerCenter.position.latitude},${markerCenter.position.longitude}")
            Log.d(TAG, "Map Coordinate: " + markerCenter.position)
            lat= markerCenter.position.latitude
            lng= markerCenter.position.longitude
        }
    }

    private fun updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Log.d(
                    TAG,
                    "updateGPS: Lat: ${it?.latitude} Long: ${it?.longitude} accuray : ${it?.accuracy} "
                )
                mMap.clear()
                val myLocation = LatLng(it.latitude, it.longitude)
                val icon =
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_baseline_location_on_24_transparent
                    )?.let { it1 ->
                        getMarkerIcon(it1)
                    }
                markerCenter = mMap.addMarker(
                    MarkerOptions().position(myLocation).title("Marker in Sydney").icon(
                        icon
                    )
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 16f))
            }
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_LOCATION -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    updateGPS()
                } else {
                    Toast.makeText(this, "Location Access Denied", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }
    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }




}