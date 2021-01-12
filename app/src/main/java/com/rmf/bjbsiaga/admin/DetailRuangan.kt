package com.rmf.bjbsiaga.admin

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataRuangan
import kotlinx.android.synthetic.main.activity_detail_ruangan.*
import java.io.File

@Suppress("DEPRECATION")
class DetailRuangan : AppCompatActivity() {
    private lateinit var storage: FirebaseStorage
    private  val TAG = "DetailRuangan"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_ruangan)
        btn_unduh_qrcode.isEnabled=false

        val dataRuangan = intent.getParcelableExtra<DataRuangan>("data")

        text_nama_ruangan.text = dataRuangan?.namaRuangan

        val lat = dataRuangan?.lat.toString()
        val lng = dataRuangan?.lng.toString()
        text_koordinat_lokasi.text = "$lat, $lng"

        back.setOnClickListener { finish() }

        btn_unduh_qrcode.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        dataRuangan?.namaRuangan?.let { it1 -> download(it1) }
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(this@DetailRuangan, "Harap Izinkan Penyimpanan", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        p1?.continuePermissionRequest()
                    }

                }).check()

        }

        if(!dataRuangan?.namaRuangan.isNullOrEmpty()){
            dataRuangan?.namaRuangan?.let { initStorage(it) }
        }
    }

    private fun download(namaRuangan: String){
        val imageRef =
            storage.reference
                .child("qrcode")
                .child(namaRuangan)

        val rootPath = File(Environment.getExternalStorageDirectory(), "BJBSiaga QRCODE")
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }
        val localFile = File(rootPath, "${namaRuangan}.png")


        imageRef.getFile(localFile).addOnSuccessListener {

            Log.d(TAG, "onCreate: berhasil download ke lokal ${it.totalByteCount}")


        }.addOnFailureListener{
            Log.e(TAG, "onCreate: $it")

        }
    }



    private fun initStorage(namaRuangan: String){
        storage = FirebaseStorage.getInstance()
        val imageRef = storage.reference
            .child("qrcode")
            .child(namaRuangan)

        //1MB
        imageRef.getBytes(1024 * 1024)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                btn_unduh_qrcode.isEnabled=true
                Glide.with(this).load(bitmap).into(imageQR)
            }
            .addOnFailureListener {
                btn_unduh_qrcode.isEnabled=false
            }

    }
}