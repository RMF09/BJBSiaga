package com.rmf.bjbsiaga.ui.jadwal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterJadwal
import com.rmf.bjbsiaga.adapter.RVAdapterSiklus.Companion.TAG
import com.rmf.bjbsiaga.admin.DetailJadwal
import com.rmf.bjbsiaga.admin.InputJadwalActivity
import com.rmf.bjbsiaga.data.DataJadwal
import com.rmf.bjbsiaga.util.CheckConnection
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_jadwal.*
import kotlinx.android.synthetic.main.fragment_ruangan.*
import kotlinx.android.synthetic.main.fragment_ruangan.progress_bar

class JadwalFragment : Fragment(), RVAdapterJadwal.ClickListener {

    lateinit var list : ArrayList<DataJadwal>
    lateinit var adapter : RVAdapterJadwal

    private lateinit var db : FirebaseFirestore
    private lateinit var jadwalRef: CollectionReference
    private var isLoad =false

    private lateinit var rv: RecyclerView
    private lateinit var btnAdd: FloatingActionButton
    private lateinit var mContext: Context

    private lateinit var includeTidakAdaKoneksi: View
    private lateinit var btnRefresh:AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_jadwal, container, false)

        initDB()
        //SetupRV
        rv = root.findViewById(R.id.rv_data_jadwal)
        btnAdd = root.findViewById(R.id.btn_add)
        btnRefresh = root.findViewById(R.id.btn_refresh)
        includeTidakAdaKoneksi = root.findViewById(R.id.include_tidak_ada_koneksi)
        mContext = root.context

        setupRV(root.context)

        //Action Listener
        btnAdd.setOnClickListener {
            activity?.startActivity(Intent(activity, InputJadwalActivity::class.java))
        }
        btnRefresh.setOnClickListener {
            startToLoad()
        }

        return root
    }

    private fun setupRV(context: Context){
        rv.apply {
            layoutManager = GridLayoutManager(
                context,2
            )
            setHasFixedSize(true)
            list = ArrayList()
            this@JadwalFragment.adapter = RVAdapterJadwal(list,this@JadwalFragment)
            this.adapter = this@JadwalFragment.adapter
        }


    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        jadwalRef = db.collection(CollectionsFS.JADWAL)
    }

    override fun onClickListener(dataJadwal: DataJadwal, context: Context) {
        Intent(activity, DetailJadwal::class.java).apply {
            putExtra("id",dataJadwal.documentId)
            putExtra("data",dataJadwal)
            activity?.startActivity(this)
        }

    }

    override fun onResume() {
        super.onResume()
        startToLoad()
    }
    private fun startToLoad(){
        if(CheckConnection.isConnected(mContext) && !isLoad){
            loadJadwal()
        }else{
            includeTidakAdaKoneksi.visibility = View.VISIBLE
            rv.visibility= View.GONE
            btnAdd.visibility= View.GONE
        }
    }

    private fun loadJadwal(){
        progress_bar.visibility = View.VISIBLE
        includeTidakAdaKoneksi.visibility = View.GONE

        rv.visibility= View.VISIBLE
        btnAdd.visibility= View.VISIBLE

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
                progress_bar.visibility = View.GONE
            }
            .addOnFailureListener {
                Log.e("loadJadwal", "loadJadwal: $it" )
                isLoad=false
                progress_bar.visibility = View.GONE
            }
    }

}