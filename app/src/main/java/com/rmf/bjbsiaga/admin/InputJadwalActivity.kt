package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rmf.bjbsiaga.R
import kotlinx.android.synthetic.main.activity_input_jadwal.*

class InputJadwalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_jadwal)

        back.setOnClickListener {
            finish()
        }
    }
}