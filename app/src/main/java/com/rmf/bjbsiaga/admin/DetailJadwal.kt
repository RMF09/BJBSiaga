package com.rmf.bjbsiaga.admin

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterJadwalSecurity
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.data.DataJadwalBertugas
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_detail_jadwal.*

class DetailJadwal : AppCompatActivity() {
    private lateinit var id: String
    private lateinit var dataJadwal: DataJadwal
    private val TAG = "DetailJadwal"

    private lateinit var adapter: RVAdapterJadwalSecurity
    private lateinit var list: ArrayList<DataJadwalBertugas>

    lateinit var db : FirebaseFirestore
    lateinit var jadwalBertugasRef: CollectionReference

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_jadwal)

        id = intent.getStringExtra("id").toString()
        dataJadwal = intent.getParcelableExtra("data")!!

        initDB()
        setupRV()

        Log.d(TAG, "onCreate: documentID = $id, data :  ${dataJadwal.hari}")
        header_text.text = "Detail Jadwal Hari ${dataJadwal.hari}"

        back.setOnClickListener { finish() }

        val dataUnitKerja = arrayOf("KCP Cipanas","KCP Cianjur")
        val adapterSpinner = ArrayAdapter<String>(
            this,
            R.layout.spinner_selected_item,
            dataUnitKerja)
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown)

        spinner_unit_kerja.adapter = adapterSpinner
        adapterSpinner.notifyDataSetChanged()

        spinner_unit_kerja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d(TAG, "onItemSelected: ${parent?.selectedItem}")
                loadData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        jadwalBertugasRef = db.collection(CollectionsFS.JADWAL_BERTUGAS)
    }

    private fun setupRV(){
        list = ArrayList()
        rv_daftar_security.layoutManager= LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        adapter = RVAdapterJadwalSecurity(list)
        rv_daftar_security.adapter= adapter
    }
    private fun loadData(){
        list.clear()
        adapter.notifyDataSetChanged()
        jadwalBertugasRef.whereEqualTo("unitKerja",spinner_unit_kerja.selectedItem.toString())
            .whereEqualTo("shift",dataJadwal.shift).get()
            .addOnSuccessListener {
                for(document in it){
                    val dataJadwalBertugas: DataJadwalBertugas = document.toObject(DataJadwalBertugas::class.java)
                    dataJadwalBertugas.documentId = document.id
                    list.add(dataJadwalBertugas)
                }
                adapter.notifyDataSetChanged()
            }.addOnFailureListener {

                Log.e(TAG, "loadData: $it")
            }
    }


}