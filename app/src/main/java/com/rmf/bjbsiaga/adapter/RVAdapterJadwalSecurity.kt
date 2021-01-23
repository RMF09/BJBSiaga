package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwalBertugas
import com.rmf.bjbsiaga.data.DataSecurity
import kotlinx.android.synthetic.main.item_jadwal_security.view.*

class RVAdapterJadwalSecurity(var list: ArrayList<DataJadwalBertugas>, val hapusClickListener: HapusClickListener) : RecyclerView.Adapter<RVAdapterJadwalSecurity.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal_security,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.text_nomer.text = (position+1).toString()
        holder.itemView.text_nama.text=data.nama
        holder.itemView.text_shift.text=data.shift
        holder.itemView.image_shift.setImageDrawable(checkIcon(holder.itemView.context,data.shift))

        holder.itemView.img_hapus.setOnClickListener {
            hapusClickListener.onClickListener(data)
        }
    }

    private fun checkIcon(context: Context, shift: String) : Drawable? {
        return when(shift){
            "Pagi" -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_wb_sunny_24)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_nights_stay_24)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface HapusClickListener{
        fun onClickListener(dataJadwalBertugas: DataJadwalBertugas)
    }

}