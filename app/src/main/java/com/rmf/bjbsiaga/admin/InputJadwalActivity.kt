package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_input_jadwal.*

class InputJadwalActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private  val TAG = "InputJadwalActivity"
    private lateinit var db : FirebaseFirestore
    private lateinit var namaHari: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_jadwal)
        initListNamaHari()
        initDB()

        back.setOnClickListener {
            finish()
        }
        btn_tambah_data.setOnClickListener {
            saveData(spinner_days.selectedItem.toString(),spinner_shift.selectedItem.toString())
        }

        spinner_shift.onItemSelectedListener = this
    }
    private fun initListNamaHari(){
        namaHari = ArrayList()
        namaHari.apply {
            add("Senin")
            add("Selasa")
            add("Rabu")
            add("Kamis")
            add("Jumat")
            add("Sabtu")
            add("Minggu")
        }
    }

    private fun checkPriorityDay(hari: String,shift: String): Int{
        var priority = 0


        for ((i, item) in namaHari.withIndex()) {
            Log.d(TAG, "checkPriorityDay: $priority, i=$i")
            if (item == hari) {
                if (shift == "Malam") {
                    priority += 1
                    Log.d(TAG, "checkPriorityDay: malam $priority")
                }
                break
            }
            priority +=2
            Log.d(TAG, "checkPriorityDay: Kadie")

        }
        return priority+1
    }

    private fun saveData(hari: String, shift: String) {
        Log.d(TAG, "saveData: $hari, $shift, ${checkPriorityDay(hari,shift)}")
//        val datajadwal = DataJadwal(hari, shift)
//
//        db.collection(CollectionsFS.JADWAL).document().set(datajadwal)
//            .addOnSuccessListener {
//                Log.d(TAG, "saveData: berhasil")
//            }
//            .addOnFailureListener {
//                Log.e(TAG, "saveData: gagal $it" )
//            }
    }

    fun initDB(){
        db = FirebaseFirestore.getInstance()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        Log.d(TAG, "onItemSelected: ${parent?.getItemAtPosition(position)}")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}