package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.rmf.bjbsiaga.R
import kotlinx.android.synthetic.main.activity_input_jadwal.*

class InputJadwalActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private  val TAG = "InputJadwalActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_jadwal)

        back.setOnClickListener {
            finish()
        }

        spinner_shift.onItemSelectedListener = this

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        Log.d(TAG, "onItemSelected: ${parent?.getItemAtPosition(position)}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}