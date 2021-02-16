package com.rmf.bjbsiaga.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterUser
import com.rmf.bjbsiaga.data.DataUser
import com.rmf.bjbsiaga.util.CollectionsFS
import kotlinx.android.synthetic.main.activity_data_supervisor.*

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
    }

    private fun setupRV(){
        rv_data_supervisor.apply {
            layoutManager =LinearLayoutManager(this@DataSupervisorActivity,LinearLayoutManager.VERTICAL,false)
            adapterUser = RVAdapterUser(list,this@DataSupervisorActivity)
            adapter =adapterUser
        }
    }

    private fun loadData(){
        list.clear()
        adapterUser.notifyDataSetChanged()
        isLoad=true

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
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "loadData: $it" )
                isLoad = false
            }
    }

    override fun onResume() {
        super.onResume()
        if(!isLoad) loadData()
    }

    private fun initDB(){
        db = FirebaseFirestore.getInstance()
        userRef = db.collection(CollectionsFS.USER)

    }

    override fun onClickListener(dataUser: DataUser) {

    }
}