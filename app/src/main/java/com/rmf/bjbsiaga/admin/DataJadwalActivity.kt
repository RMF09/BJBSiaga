package com.rmf.bjbsiaga.admin

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterJadwal
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_jadwal.*
import kotlinx.android.synthetic.main.activity_data_jadwal.back
import kotlinx.android.synthetic.main.activity_data_jadwal.btn_add
import kotlinx.android.synthetic.main.activity_data_jadwal.progress_bar


class DataJadwalActivity : AppCompatActivity(), RVAdapterJadwal.ClickListener {

    lateinit var list : ArrayList<DataJadwal>
    lateinit var adapter : RVAdapterJadwal

    lateinit var db : FirebaseFirestore
    lateinit var jadwalRef: CollectionReference
    private var isLoad =false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_jadwal)

        initDB()
        setupRV()
        setupAdapter()


        btn_add.setOnClickListener {
            startActivity(Intent(this,InputJadwalActivity::class.java))
        }

        back.setOnClickListener {
            finish()
        }

        registerForContextMenu(rv_data_jadwal)

    }

    fun setupRV(){
        rv_data_jadwal.layoutManager = GridLayoutManager(
            this,2
        )
        rv_data_jadwal.setHasFixedSize(true)
//        rv_data_jadwal.layoutManager = LinearLayoutManager(
//            this,
//            LinearLayoutManager.VERTICAL,
//            false
//        )

    }
    fun setupAdapter(){
        list = ArrayList()
        adapter = RVAdapterJadwal(list,this)
        rv_data_jadwal.adapter =adapter

    }
    fun initDB(){
        db = FirebaseFirestore.getInstance()
        jadwalRef = db.collection(CollectionsFS.JADWAL)
    }
    private fun loadJadwal(){
        progress_bar.visibility = View.VISIBLE
        list.clear()
        adapter.notifyDataSetChanged()
        isLoad=true
        jadwalRef.orderBy("priority", Query.Direction.ASCENDING).get()
            .addOnSuccessListener {
                for (document in it){
                    val dataJadwal : DataJadwal = document.toObject(DataJadwal::class.java)
                    dataJadwal.documentId = document.id
                    list.add(dataJadwal)
                }
                adapter.notifyDataSetChanged()
                isLoad=false
//                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e("loadJadwal", "loadJadwal: $it" )
                isLoad=false
//                progress_bar.visibility = View.GONE
            }
    }

    override fun onResume() {
        super.onResume()
        if(!isLoad) loadJadwal()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val position: Int

        try {
            position = adapter.position
        }
        catch (e: Exception){
            return super.onContextItemSelected(item)
        }
        return when(item.itemId){
            R.id.hapus ->{
                Log.d("ContextMenu", "onContextItemSelected: Hapus $position")
                hapusItem(position)
                return true
                }
            else ->
                super.onContextItemSelected(item)
        }
    }
    private fun hapusItem(position: Int){
        jadwalRef.document(list[position].documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "berhasil dihapus", Toast.LENGTH_SHORT).show()
                list.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
            .addOnFailureListener {
                Log.e("hapus", "hapusItem: $it" )
                Toast.makeText(this, "gagal dihapus", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onClickListener(dataJadwal: DataJadwal, context: Context) {
        Intent(this,DetailJadwal::class.java).apply {
            putExtra("id",dataJadwal.documentId)
            putExtra("data",dataJadwal)
            startActivity(this)
        }
    }


}