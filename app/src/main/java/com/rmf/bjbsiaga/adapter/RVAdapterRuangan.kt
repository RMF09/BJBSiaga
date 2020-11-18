package com.rmf.bjbsiaga.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataRuangan
import com.rmf.bjbsiaga.data.DataSecurity
import kotlinx.android.synthetic.main.item_data_ruangan.view.*


class RVAdapterRuangan(var list: ArrayList<DataRuangan>) : RecyclerView.Adapter<RVAdapterRuangan.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_data_ruangan,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.text_nomer.text = (position+1).toString()
        holder.itemView.text_nama_ruangan.text= data.namaRuangan
        holder.itemView.text_keterangan_ruangan.text= data.keterangan
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}