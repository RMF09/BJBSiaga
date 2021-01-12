package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataSecurity
import kotlinx.android.synthetic.main.item_data_security.view.*

class RVAdapterSecurity(var list: ArrayList<DataSecurity>, val clickListener: ClickListener) : RecyclerView.Adapter<RVAdapterSecurity.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_data_security,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.text_nomer.text = (position+1).toString()
        holder.itemView.text_nama.text=data.nama
        holder.itemView.text_email.text=data.email

        holder.itemView.setOnClickListener {
           clickListener.onClickListener(data,it.context)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ClickListener{
        fun onClickListener(dataSecurity: DataSecurity,context: Context)
    }
}