package com.rmf.bjbsiaga.admin

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_maps.btn_tambah_data



class TambahRuanganActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_LOCATION : Int =1
    private  val TAG = "MapsActivity"

    lateinit var markerCenter: Marker
    var lat : Double= 0.0
    var lng : Double= 0.0

    lateinit var db : FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

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
            if(validate(it)){
                saveData(it)
            }
        }

    }
    fun validate(view: View):Boolean{
        val namaRuangan: String = edit_nama_ruangan.text.toString()
        val keterangan: String? = edit_keterangan_ruangan.text.toString()
        val koordinat: String = edit_koordinat_lokasi.text.toString()

        if(namaRuangan.isEmpty()){
            edit_nama_ruangan.requestFocus()
            Snackbar.make(view,"Nama Ruangan diperlukan", Snackbar.LENGTH_LONG).show()
            return false
        }
        if(koordinat.isEmpty()){
            edit_koordinat_lokasi.requestFocus()
            Snackbar.make(view,"Koordinat lokasi diperlukan. Geser marker pada map untuk mendapatkan nilai koordinat", Snackbar.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
    }

    fun saveData(view: View){

        val namaRuangan: String = edit_nama_ruangan.text.toString()
        val keterangan: String = edit_keterangan_ruangan.text.toString()

        val dataRuangan = DataRuangan(namaRuangan,keterangan,lat, lng)

        db.collection(CollectionsFS.RUANGAN).document().set(dataRuangan)
            .addOnSuccessListener {
                Snackbar.make(view,"Data berhasil disimpan",Snackbar.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Snackbar.make(view,"Data gagal disimpan",Snackbar.LENGTH_LONG).show()
                Log.e(TAG, "saveData: ${it.toString()}" )
            }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        mMap.setOnCameraMoveListener {

            val test = mMap.cameraPosition
            markerCenter.position =test.target
            edit_koordinat_lokasi.setText("${markerCenter.position.latitude},${markerCenter.position.longitude}")
            Log.d(TAG, "Map Coordinate: " + markerCenter.position);
            lat= markerCenter.position.latitude
            lng= markerCenter.position.longitude
        }
    }

    fun setUpLocation(){
        locationRequest= LocationRequest()
        locationRequest.apply {
            interval=30000
            fastestInterval=3000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    fun updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//                Toast.makeText(
//                    this,
//                    "Lat: ${it.latitude} Long: ${it.longitude} ", Toast.LENGTH_LONG
//                )
//                    .show()
                Log.d(
                    TAG,
                    "updateGPS: Lat: ${it.latitude} Long: ${it.longitude} accuray : ${it.accuracy} "
                )
               mMap.clear()
                val myLocation = LatLng(it.latitude, it.longitude)

                val icon =
                    ContextCompat.getDrawable(this,
                        R.drawable.ic_baseline_location_on_24_transparent
                    )?.let { it1 ->
                        getMarkerIcon(
                            it1
                        )
                    }
                markerCenter = mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney").icon(icon))
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
        mapView.onResume();
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


    fun getMarkerIcon(drawable : Drawable) : BitmapDescriptor{
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight,Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0,0,drawable.intrinsicHeight,drawable.intrinsicHeight)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}