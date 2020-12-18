package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataRuangan
import kotlinx.android.synthetic.main.activity_detail_ruangan.*

class DetailRuangan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_ruangan)

        val dataRuangan = intent.getParcelableExtra<DataRuangan>("data")

        text_nama_ruangan.text = dataRuangan?.namaRuangan
        text_koordinat_lokasi.text = dataRuangan?.documentId

    }
}