package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataCabang
import kotlinx.android.synthetic.main.item_data_cabang.view.*

class RVAdapterCabang(var list: ArrayList<DataCabang>, val clickListener: ClickListener) : RecyclerView.Adapter<RVAdapterCabang.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_data_cabang,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.text_nomer.text = (position+1).toString()
        holder.itemView.text_nama_cabang.text= data.namaCabang
        holder.itemView.text_jumlah_ruangan.text = checkJumlahRuangan(data.jumlahRuangan)

        holder.itemView.setOnClickListener {
            clickListener.onClickListener(data,it.context)
        }

    }

    private fun checkJumlahRuangan(jumlahRuangan: Int):String{
        return when(jumlahRuangan){
            0-> "Belum ada ruangan"
            else -> "$jumlahRuangan ruangan"
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ClickListener{
        fun onClickListener(dataCabang: DataCabang,context: Context)
    }
}