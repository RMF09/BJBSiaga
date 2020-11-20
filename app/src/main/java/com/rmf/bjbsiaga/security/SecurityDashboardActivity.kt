package com.rmf.bjbsiaga.security

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rmf.bjbsiaga.R
import kotlinx.android.synthetic.main.activity_security_dashboard.*

class SecurityDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_dashboard)

        btn_siklus1.setOnClickListener {
            when(btn_siklus1.isEnabled){
                true -> btn_siklus1.isEnabled=false
                else -> btn_siklus1.isEnabled=true
            }
        }

        btn_siklus2.setOnClickListener {
            when(btn_siklus2.isEnabled){
                true -> btn_siklus2.isEnabled=false
                else -> btn_siklus2.isEnabled=true
            }
        }

        btn_siklus3.setOnClickListener {
            when(btn_siklus3.isEnabled){
                true -> btn_siklus3.isEnabled=false
                else -> btn_siklus3.isEnabled=true
            }
        }

        btn_siklus4.setOnClickListener {
            when(btn_siklus4.isEnabled){
                true -> btn_siklus4.isEnabled=false
                else -> btn_siklus4.isEnabled=true
            }
        }
    }
}