package com.rmf.bjbsiaga

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

class TambahRuanganActivity : AppCompatActivity() {

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    val PERMISSION_LOCATION : Int =1
    private  val TAG = "TambahRuanganActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_ruangan)

        setUpLocation()
        updateGPS()
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
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
           ){

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Lat: ${it.latitude} Long: ${it.longitude} ",Toast.LENGTH_LONG)
                    .show()
                Log.d(TAG, "updateGPS: Lat: ${it.latitude} Long: ${it.longitude} accuray : ${it.accuracy} ")
            }
        }
        else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),PERMISSION_LOCATION)
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
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    updateGPS()
                } else {
                    Toast.makeText(this,"Location Access Denied", Toast.LENGTH_LONG).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }


}