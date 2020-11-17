package com.rmf.bjbsiaga

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_LOCATION : Int =1
    private  val TAG = "MapsActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

        var mapViewBundle :Bundle? =null
        if(savedInstanceState!=null){
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }

        mapView.onCreate(mapViewBundle)

        mapView.getMapAsync(this)

        updateGPS()

        val fieldList = arrayOf(Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.NAME)




    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(p0?.position))
            }

            override fun onMarkerDragEnd(p0: Marker?) {

            }

        })
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
                Toast.makeText(
                    this,
                    "Lat: ${it.latitude} Long: ${it.longitude} ", Toast.LENGTH_LONG
                )
                    .show()
                Log.d(
                    TAG,
                    "updateGPS: Lat: ${it.latitude} Long: ${it.longitude} accuray : ${it.accuracy} "
                )
               mMap.clear()
                val myLocation = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(myLocation).title("Marker in Sydney").draggable(true))
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

    fun place(){
        if(!Places.isInitialized()){
            Places.initialize(this, R.string.google_api_key.toString())
            // Create a new Places client instance.

            // Create a new Places client instance.
            val placesClient = Places.createClient(this)
        }
    }

}