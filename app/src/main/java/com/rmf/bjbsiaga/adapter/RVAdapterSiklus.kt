package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataDetailSiklus
import com.rmf.bjbsiaga.util.Config
import kotlinx.android.synthetic.main.item_detail_siklus.view.*


class RVAdapterSiklus(var list: ArrayList<DataDetailSiklus>) : RecyclerView.Adapter<RVAdapterSiklus.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail_siklus,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]

        holder.itemView.apply {
            text_nomer.text = (position+1).toString()
            text_nama_ruangan.text= data.nama
            text_status_ruangan.text = checkStatus(data.diCheck,data.pukul)
            icon_keterangan.setImageDrawable(checkIcon(holder.itemView.context,data.diCheck))
            sendDataWithBroadCast(holder.itemView.context,position,holder)
        }


    }

    fun checkStatus(diCheck: Boolean,pukul: String) : String{
        return when(diCheck){
            true -> "Sudah dicek pada pukul $pukul"
            else -> "Belum dicek"
        }
    }

    fun checkIcon(context: Context, diCheck: Boolean) : Drawable? {
        return when(diCheck){
            true -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_check_24)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_close_24)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun sendDataWithBroadCast(context: Context, position: Int, holder: ViewHolder){
        if(position==0 && list[position].documentId.isNotEmpty()){

            holder.itemView.relativeLayout.setBackgroundColor(
                Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.colorAccentDark))))

            Intent(Config.ACTION_DATA_DETAIL_SIKLUS).apply {
                putExtra("id",list[position].documentId)
                LocalBroadcastManager.getInstance(context).sendBroadcast(this)
            }

        }
    }
}