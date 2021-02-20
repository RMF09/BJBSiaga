package com.rmf.bjbsiaga.admin.data.supervisor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterUser
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.util.CheckConnection
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_supervisor.*
import kotlinx.android.synthetic.main.activity_data_supervisor.back
import kotlinx.android.synthetic.main.activity_data_supervisor.btn_add
import kotlinx.android.synthetic.main.activity_data_supervisor.include_tidak_ada_koneksi
import kotlinx.android.synthetic.main.activity_data_supervisor.progress_bar
import kotlinx.android.synthetic.main.tidak_ada_koneksi.*

class DataSupervisorActivity : AppCompatActivity(), RVAdapterUser.ClickListener {

    private var list:ArrayList<DataUser> = ArrayList()
    private lateinit var adapterUser: RVAdapterUser

    private lateinit var db: FirebaseFirestore
    private lateinit var userRef: CollectionReference
    private var isLoad =false

    companion object{
        const val TAG = "DataSupervisor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_supervisor)
        initDB()
        setupRV()

        btn_add.setOnClickListener {
            startActivity(Intent(this, TambahDataSupervisorActivity::class.java))
        }
        back.setOnClickListener { finish() }

        btn_refresh.setOnClickListener {
            startToLoad()
        }
    }

    private fun setupRV(){
        rv_data_supervisor.apply {
            layoutManager =LinearLayoutManager(this@DataSupervisorActivity,LinearLayoutManager.VERTICAL,false)
            adapterUser = RVAdapterUser(list,this@DataSupervisorActivity)
            adapter =adapterUser
        }
    }

    private fun loadData(){
        include_tidak_ada_koneksi.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE

        rv_data_supervisor.visibility= View.VISIBLE
        btn_add.visibility= View.VISIBLE

        list.clear()
        adapterUser.notifyDataSetChanged()
        isLoad=true

        text_belum_ada_data.visibility = View.GONE

        userRef.whereEqualTo("userType",CollectionsFS.SUPERVISOR)
            .get()
            .addOnSuccessListener {
                if(!it.isEmpty){
                    for (data in it){
                        val dataUser = data.toObject(DataUser::class.java)
                        dataUser.documentId= data.id
                        list.add(dataUser)
                    }
                    adapterUser.notifyDataSetChanged()
                    isLoad=false
                    progress_bar.visibility = View.GONE
                }else{
                    text_belum_ada_data.visibility = View.VISIBLE
                    progress_bar.visibility = View.GONE
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadData: $it" )
                isLoad = false
                progress_bar.visibility = View.GONE
            }
    }

    override fun onResume() {
        super.onResume()
        startToLoad()
    }

    private fun startToLoad(){
        if(CheckConnection.isConnected(this) && !isLoad){
            loadData()
        }else{
            include_tidak_ada_koneksi.visibility = View.VISIBLE
            rv_data_supervisor.visibility= View.GONE
            btn_add.visibility= View.GONE
        }
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        userRef = db.collection(CollectionsFS.USER)

    }

    override fun onClickListener(dataUser: DataUser) {
        Intent(this, DetailSupervisor::class.java).apply {
            putExtra("data",dataUser)
            putExtra("id",dataUser.documentId)
            startActivity(this)
        }
    }
}