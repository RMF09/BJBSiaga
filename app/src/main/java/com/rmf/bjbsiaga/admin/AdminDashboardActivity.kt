package com.rmf.bjbsiaga.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rmf.bjbsiaga.R
import kotlinx.android.synthetic.main.activity_admin_dashboard.*

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        cardKeluar.setOnClickListener {
            finish()
        }
        cardInput.setOnClickListener {
            startActivity(Intent(this,DataSecurityActivity::class.java))
        }
        cardJadwal.setOnClickListener {
            startActivity(Intent(this,InputJadwalActivity::class.java))
        }
    }
}