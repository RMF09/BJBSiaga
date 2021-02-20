package com.rmf.bjbsiaga.ui.ruangan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterCabang
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus.Companion.TAG
import com.rmf.bjbsiaga.admin.DataRuanganActivity
import com.rmf.bjbsiaga.data.DataCabang
import com.rmf.bjbsiaga.util.CheckConnection
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.fragment_ruangan.*
import kotlinx.android.synthetic.main.tidak_ada_koneksi.*

class RuanganFragment: Fragment(), RVAdapterCabang.ClickListener {

    private var list: ArrayList<DataCabang> = ArrayList()
    private lateinit var adapter: RVAdapterCabang

    private lateinit var db : FirebaseFirestore
    private lateinit var cabangRef: CollectionReference

    private lateinit var alertDialog: AlertDialog
    private lateinit var rv: RecyclerView
    private lateinit var btnAdd: FloatingActionButton

    private lateinit var mContext: Context
    private lateinit var btnRefresh: AppCompatButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ruangan,container,false)
        rv = root.findViewById(R.id.rv_data_cabang)
        btnAdd = root.findViewById(R.id.btn_add)
        btnRefresh = root.findViewById(R.id.btn_refresh)
        mContext = root.context

        initDialog(root.context)
        initDB()
        setupRV(root.context)

        //Action Listener
        btnAdd.setOnClickListener { alertDialog.show() }

        btnRefresh.setOnClickListener {
            startToLoad()
        }

        return root
    }

    private fun initDialog(context: Context){
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.dialog_tambah_kcp,null)

        val editNama: EditText = view.findViewById(R.id.edit_nama_cabang)
        val btnTambah: AppCompatButton = view.findViewById(R.id.btn_tambah_dialog_cabang)

        builder.setView(view)
        alertDialog =  builder.create()

        btnTambah.setOnClickListener {
            if(editNama.text.toString().isEmpty()){
                Snackbar.make(it,"Nama Cabang Tidak Boleh Kosong", Snackbar.LENGTH_LONG)
            }
            else{
                tambahCabang(editNama.text.toString())
            }
        }

    }
    private fun tambahCabang(namaCabang: String) {
        val dataCabang = DataCabang(namaCabang,0)

        cabangRef.document().set(dataCabang)
            .addOnSuccessListener {
                Log.d(TAG, "tambahCabang: Berhasil ditambahkan")
                alertDialog.dismiss()
                startToLoad()
            }
            .addOnFailureListener {
                Log.d(TAG, "tambahCabang: Gagal $it")
            }

    }

    private fun loadData() {
        include_tidak_ada_koneksi.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
        rv.visibility = View.VISIBLE
        btnAdd.visibility = View.VISIBLE
        text_belum_ada_data.visibility = View.GONE
        list.clear()
        adapter.notifyDataSetChanged()
        cabangRef.get()
            .addOnSuccessListener {
                Log.d(TAG, "loadData: ${it.size()}")
                if(!it.isEmpty){
                    for (dokumen in it){
                        val dataCabang: DataCabang = dokumen.toObject(DataCabang::class.java)
                        dataCabang.documentId = dokumen.id
                        list.add(dataCabang)
                    }
                    adapter.notifyDataSetChanged()
                    progress_bar.visibility = View.GONE
                }
                else{
                    progress_bar.visibility = View.GONE
                    text_belum_ada_data.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "loadData: $it")
                progress_bar.visibility = View.GONE
            }
    }

    private fun setupRV(context: Context) {
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        adapter = RVAdapterCabang(list,this)
        rv.adapter=adapter
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        cabangRef = db.collection(CollectionsFS.CABANG)
    }

    override fun onClickListener(dataCabang: DataCabang, context: Context) {
        Intent(activity, DataRuanganActivity::class.java).apply {
            putExtra("id_cabang",dataCabang.documentId)
            putExtra("nama_cabang",dataCabang.namaCabang)
            activity?.startActivity(this)
        }
    }

    override fun onResume() {
        super.onResume()
        startToLoad()
    }
    private fun startToLoad(){
        if(CheckConnection.isConnected(mContext)){
            loadData()
        }else{
            include_tidak_ada_koneksi.visibility= View.VISIBLE
            rv.visibility = View.GONE
            btnAdd.visibility = View.GONE
        }
    }
}