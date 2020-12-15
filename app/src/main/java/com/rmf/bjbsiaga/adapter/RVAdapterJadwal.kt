package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataJadwal
import kotlinx.android.synthetic.main.item_jadwal.view.*

class RVAdapterJadwal(var list: ArrayList<DataJadwal>) : RecyclerView.Adapter<RVAdapterJadwal.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_jadwal,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.apply {
            text_nomer.text = (position+1).toString()
            text_nama_hari.text = data.hari
            text_shift.text = data.shift
            icon_keterangan.setImageDrawable(checkIcon(this.context,data.shift))
        }
    }
    private fun checkIcon(context: Context, shift: String) : Drawable? {
        return when(shift){
            "Pagi" -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_wb_sunny_24)
             else -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_nights_stay_24)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}