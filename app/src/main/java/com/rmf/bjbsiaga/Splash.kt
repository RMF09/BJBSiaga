package com.rmf.bjbsiaga

import android.Manifest
import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                   p1?.continuePermissionRequest()
                }

                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if(p0!!.areAllPermissionsGranted()){
                        start()
                    }
                }
            })
            .check()
    }
    fun start(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}