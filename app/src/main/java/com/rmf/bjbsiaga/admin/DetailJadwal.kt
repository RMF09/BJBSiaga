package com.rmf.bjbsiaga.admin

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterJadwalSecurity
import com.rmf.bjbsiaga.adapter.RVAdapterSecurity
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.data.DataJadwalBertugas
import com.rmf.bjbsiaga.data.DataSecurity
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_detail_jadwal.*
import kotlinx.android.synthetic.main.dialog_add_person.*
import java.util.*
import kotlin.collections.ArrayList

class DetailJadwal : AppCompatActivity(), RVAdapterSecurity.ClickListener {
    private lateinit var id: String
    private lateinit var dataJadwal: DataJadwal
    private val TAG = "DetailJadwal"

    private lateinit var adapter: RVAdapterJadwalSecurity
    private lateinit var list: ArrayList<DataJadwalBertugas>

    private lateinit var db : FirebaseFirestore
    private lateinit var jadwalBertugasRef: CollectionReference
    private lateinit var securityRef: CollectionReference

    private lateinit var alertDialog: AlertDialog

    private var listPersonAdd: ArrayList<DataSecurity> = ArrayList()
    private val allDataPerson: ArrayList<DataSecurity> =ArrayList()
    private var initialLoadPersonAdd =false
    private lateinit var adapterSecurity: RVAdapterSecurity
    private var unitKerjaDipilih = ""

    private var listPersonSelected: ArrayList<DataSecurity> = ArrayList()

    private lateinit var textSecurityTerpilih: TextView
    private var beresTambahSecurity = false
    private lateinit var btnTambahSecurity: AppCompatButton

    private var jumlahSemuaSecurity =0

    private lateinit var textKeterangan: TextView
    private lateinit var editCariSecurity: EditText

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_jadwal)

        id = intent.getStringExtra("id").toString()
        dataJadwal = intent.getParcelableExtra("data")!!

        initDB()
        setupRV()
        initialDialog()

        Log.d(TAG, "onCreate: documentID = $id, data :  ${dataJadwal.hari}")
        header_text.text = "Detail Jadwal Hari ${dataJadwal.hari}"

        loadDataUnitKerja()

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
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btn_add_person.setOnClickListener {
            listPersonSelected.clear()
            alertDialog.show()
            btnTambahSecurity.text="Tambah"
            btnTambahSecurity.isEnabled=false
            textSecurityTerpilih.text=""
            beresTambahSecurity=false
            textKeterangan.text =""

            loadDataSecurity()
        }
        back.setOnClickListener { finish() }
    }

    private fun loadDataUnitKerja() {
        val dataUnitKerja = arrayOf("KCP Cipanas","KCP Cianjur")
        val adapterSpinner = ArrayAdapter<String>(
            this,
            R.layout.spinner_selected_item,
            dataUnitKerja)
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown)

        spinner_unit_kerja.adapter = adapterSpinner
        adapterSpinner.notifyDataSetChanged()
    }

    private fun initDB() {
        db = FirebaseFirestore.getInstance()
        jadwalBertugasRef = db.collection(CollectionsFS.JADWAL_BERTUGAS)
        securityRef = db.collection(CollectionsFS.SECURITY)
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
        jadwalBertugasRef.whereEqualTo("idJadwal",id)
            .whereEqualTo("unitKerja",spinner_unit_kerja.selectedItem.toString()).get()
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

    @SuppressLint("InflateParams")
    private fun initialDialog(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_add_person,null)


        var rvListPerson: RecyclerView
        view.apply {
            editCariSecurity = findViewById(R.id.edit_cari_security)
            btnTambahSecurity = findViewById(R.id.btn_tambah)
            rvListPerson =  findViewById(R.id.rv_add_person_list)
            textSecurityTerpilih = findViewById(R.id.text_security_terpilih)
            textKeterangan = findViewById(R.id.keterangan)
            builder.setView(this)
        }
        alertDialog = builder.create()
        alertDialog.setCancelable(true)

        //Setup RV & List
        adapterSecurity = RVAdapterSecurity(listPersonAdd,this)

        rvListPerson.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvListPerson.adapter =adapterSecurity

        //Action UI
        editCariSecurity.doAfterTextChanged {
            if(it?.length!! >=1){

                Log.d(TAG, "doAfterTextChanged: $it")
                cariSecurity(it.toString())
            }
            else if(it.isEmpty()){
                listPersonAdd.clear()
                listPersonAdd.addAll(allDataPerson)
                adapterSecurity.notifyDataSetChanged()
                Log.d(TAG, "do after Text Changed: ${it.length}  ")

            }
        }
        btnTambahSecurity.setOnClickListener {
            if(beresTambahSecurity){
                alertDialog.dismiss()
                loadData()
            }
            else{
                alertDialog.setCancelable(false)
                textSecurityTerpilih.text = "Harap tunggu..."
                addPersonSelectedToJadwal(0)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataSecurity(){

        var beda=false
        if(!initialLoadPersonAdd || unitKerjaDipilih!=spinner_unit_kerja.selectedItem.toString()){
            allDataPerson.clear()
            unitKerjaDipilih = spinner_unit_kerja.selectedItem.toString()
            beda=true
        }

        listPersonAdd.clear()
        adapterSecurity.notifyDataSetChanged()
        securityRef.whereEqualTo("unitKerja",spinner_unit_kerja.selectedItem.toString())
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "loadDataSecurity: ada : ${it.size()} ")
                jumlahSemuaSecurity =it.size()
                if(!it.isEmpty){
                    for(document in it){
                        val dataSecurity: DataSecurity = document.toObject(DataSecurity::class.java)
                        dataSecurity.documentId = document.id
                        listPersonAdd.add(dataSecurity)
                        Log.d(TAG, "loadDataSecurity: ${dataSecurity.documentId}")

                        if(!initialLoadPersonAdd || beda){
                            allDataPerson.add(dataSecurity)
                        }
                    }
                    filterDataSecurity()
                    adapterSecurity.notifyDataSetChanged()
                    initialLoadPersonAdd=true
                    btnTambahSecurity.isEnabled=true
                }
               else{
                   //Tidak Ada Data Security
                   textKeterangan.text =
                       "Tidak Ada Data Security di ${spinner_unit_kerja.selectedItem}"
                    btnTambahSecurity.text = "Ok"
                    beresTambahSecurity = true
                    btnTambahSecurity.isEnabled=true
               }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadDataSecurity: $it")
                alertDialog.dismiss()
                Toast.makeText(this, "Kesalahan mengambil data, Harap coba lagi", Toast.LENGTH_SHORT).show()
            }
    }
    private fun filterDataSecurity(){
        Log.d(TAG, "filterDataSecurity: list size ${listPersonAdd.size} ${list.size}")

        //check position
        var jumlahItemDihapus = 0
        for (dataPetugasTerjadwal in list){
            val iterator = listPersonAdd.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                if (dataPetugasTerjadwal.nikPetugas == item.nik) {
                    iterator.remove()
                    jumlahItemDihapus++
                }
            }
        }
        adapterSecurity.notifyDataSetChanged()
        Log.d(TAG, "filterDataSecurity: hasil filter, list size ${listPersonAdd.size} list allDataPerson.size = ${allDataPerson.size}")

        if(jumlahItemDihapus==allDataPerson.size){
            textKeterangan.text = "Semua data security telah dijadwalkan disini"
            beresTambahSecurity=true
            btnTambahSecurity.text="Ok"
        }
    }
    private fun cariSecurity(nama: String){
        listPersonAdd.clear()
        adapterSecurity.notifyDataSetChanged()
        Log.d(TAG, "cariSecurity! $nama allDataPerson size = ${allDataPerson.size}")

        for (data in allDataPerson) {
            if(data.nama.toLowerCase(Locale.ROOT).contains(nama.toLowerCase(Locale.ROOT))) {
                listPersonAdd.add(data)
                Log.d(TAG, "cariSecurity: ketemu $nama")
            }
        }

        Log.d(TAG, "cariSecurity: size ${listPersonAdd.size}")

        adapterSecurity.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onClickListener(dataSecurity: DataSecurity, context: Context) {
        if(dataSecurity.terpilih){
            dataSecurity.terpilih = false
            adapterSecurity.notifyDataSetChanged()
            listPersonSelected.remove(dataSecurity)
            Log.d(TAG, "onClickListener: unselected : ${listPersonSelected.size}")
        }
        else{
            //check Data di list person selected
            dataSecurity.terpilih = true
            adapterSecurity.notifyDataSetChanged()
            Log.d(TAG, "onClickListener: diklik")
            var kosong=true
            for(data in listPersonSelected){
                if(data.nik== dataSecurity.nik){
                    kosong = false
                    Log.d(TAG, "cariSecurity: kosong $kosong ${dataSecurity.nama} ${dataSecurity.nik}")
                }
            }
            //Akhir Cek lagi
            if(kosong){
                listPersonSelected.add(dataSecurity)
                Log.d(TAG, "onClickListener: ${dataSecurity.nama} dipilih ${listPersonSelected.size} ")
            }
        }

        if(listPersonSelected.size==0){
            textSecurityTerpilih.text =""
        }else{
            textSecurityTerpilih.text = "${listPersonSelected.size} Security terpilih"
        }

        Log.d(TAG, "onClickListener: person selected: $listPersonSelected")

    }

    private fun addPersonSelectedToJadwal(position: Int){
       val dataSecurityTerpilih = listPersonSelected[position]

       val dataJadwalBertugas = DataJadwalBertugas(
           id,
           dataSecurityTerpilih.nik,
           dataSecurityTerpilih.nama,
           dataJadwal.shift,
           dataJadwal.hari,dataSecurityTerpilih.unitKerja)

       jadwalBertugasRef.document().set(dataJadwalBertugas)
           .addOnSuccessListener {
               val posNext = position +1
               if(posNext<=listPersonSelected.size-1){
                   addPersonSelectedToJadwal(posNext)
               }
               else{
                   //beres
                   beresTambahSecurity=true
                   btnTambahSecurity.text= "OK"
                   textSecurityTerpilih.text = "Berhasil ditambahkan"
                   alertDialog.setCancelable(true)
               }
           }
           .addOnFailureListener {
               Log.e(TAG, "addPersonSelectedToJadwal: $it" )
               alertDialog.setCancelable(true)
               alertDialog.dismiss()
               beresTambahSecurity =false
           }
    }


}