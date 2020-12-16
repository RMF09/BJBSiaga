package com.rmf.bjbsiaga.admin


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_input_jadwal.*

class InputJadwalActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private  val TAG = "InputJadwalActivity"
    private lateinit var db : FirebaseFirestore
    private lateinit var namaHari: ArrayList<String>
    private lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_jadwal)

        initListNamaHari()
        initDB()

        back.setOnClickListener {
            finish()
        }
        btn_tambah_data.setOnClickListener {
            btn_tambah_data.isEnabled=false
            checkJadwal(spinner_days.selectedItem.toString(),spinner_shift.selectedItem.toString())
        }

        spinner_shift.onItemSelectedListener = this
    }


    private fun showDialog(message: String, title: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(message)
            setTitle(title)
            setPositiveButton("OK"
            ) { dialog, _ ->
                dialog.dismiss()
            }
        }
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
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

    private fun checkJadwal(hari: String, shift: String){

        db.collection(CollectionsFS.JADWAL).whereEqualTo("hari",hari)
            .whereEqualTo("shift",shift)
            .get()
            .addOnSuccessListener {
               if(it.isEmpty){
                   saveData(spinner_days.selectedItem.toString(),spinner_shift.selectedItem.toString())
               }
                else{
                   Log.e(TAG, "checkJadwal: ada")
                   this.showDialog("Jadwal sudah ada!","Peringatan")
                   btn_tambah_data.isEnabled=true
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "checkJadwal: $it")
                this.showDialog("Harap periksa jaringan Anda!","Kesalahan")
                btn_tambah_data.isEnabled=true
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
        val datajadwal = DataJadwal(hari, shift, checkPriorityDay(hari,shift))

        db.collection(CollectionsFS.JADWAL).document().set(datajadwal)
            .addOnSuccessListener {
                Log.d(TAG, "saveData: berhasil")
                this.showDialog("Jadwal baru berhasil ditambahkan!","Berhasil")
                btn_tambah_data.isEnabled=true
            }
            .addOnFailureListener {
                Log.e(TAG, "saveData: gagal $it" )
                this.showDialog("Harap periksa jaringan Anda!","Kesalahan")
                btn_tambah_data.isEnabled=true
            }
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