package com.rmf.bjbsiaga.ui.user

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.adapter.RVAdapterUserType
import com.rmf.bjbsiaga.admin.data.security.DataSecurityActivity
import com.rmf.bjbsiaga.admin.DataSupervisorActivity
import com.rmf.bjbsiaga.data.DataUserType

class DataUserFragment : Fragment(),RVAdapterUserType.ClickListener  {
    private lateinit var list : ArrayList<DataUserType>
    private lateinit var adapter : RVAdapterUserType

    private lateinit var rv: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root= inflater.inflate(R.layout.fragment_data_user, container, false)
        rv=  root.findViewById(R.id.rv_data_security)

        setupRV(root.context)
        setupAdapter()

        return root
    }

    fun setupRV(context: Context){
        rv.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
    }
    fun setupAdapter(){
        list = ArrayList()
        adapter = RVAdapterUserType(list,this)
        rv.adapter =adapter

        list.add(DataUserType("Security"))
        list.add(DataUserType("Supervisor"))

        adapter.notifyDataSetChanged()

    }


    override fun onClickListener(dataUserType: DataUserType) {
        when(dataUserType.userType){
            "Security" -> { startActivity(Intent(activity, DataSecurityActivity::class.java)) }
            else ->{startActivity(Intent(activity,DataSupervisorActivity::class.java)) }
        }
    }
}