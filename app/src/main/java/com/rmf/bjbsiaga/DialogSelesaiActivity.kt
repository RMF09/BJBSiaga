package com.rmf.bjbsiaga

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_dialog_selesai.*

class DialogSelesaiActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog_selesai)

        val tanggalTugasSiaga = intent.getStringExtra("tanggal")

        text_title_complete.text = "Tugas Siaga Selesai!"
        text_keterangan.text = "Tugas Siaga Anda pada hari $tanggalTugasSiaga telah selesai"

        btn_ok_dialog_detail_siklus.setOnClickListener {
            finish()
        }
    }
}