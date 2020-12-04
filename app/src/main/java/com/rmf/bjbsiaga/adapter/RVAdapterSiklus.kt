package com.rmf.bjbsiaga.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
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

/**
 * Detail Siklus Adapter
 * */
class RVAdapterSiklus(var list: ArrayList<DataDetailSiklus>) : RecyclerView.Adapter<RVAdapterSiklus.ViewHolder>() {

    var selected=false
    val TAG ="RVAdapterSiklus"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail_siklus,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]
        holder.itemView.apply {
            relativeLayout.setBackgroundColor(0x00000000)
            text_nomer.text = (position+1).toString()
            text_nama_ruangan.text= data.nama
            text_status_ruangan.text = checkStatus(data.diCheck,data.foto,data.pukul)
            icon_keterangan.setImageDrawable(checkIcon(holder.itemView.context,data.diCheck))
            sendDataWithBroadCast(holder.itemView.context,position,holder)
        }
    }

    private fun checkStatus(diCheck: Boolean, foto:String, pukul: String) : String{
        return if(diCheck){
            if(foto.isEmpty()){ "Tekan Tombol kamera untuk foto diri" }
            else{ "Sudah dicek pada pukul $pukul" }
        } else{ "Belum dicek" }
    }

    private fun checkIcon(context: Context, diCheck: Boolean) : Drawable? {
        return when(diCheck){
            true -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_check_24)
            else -> ContextCompat.getDrawable(context,R.drawable.ic_baseline_close_24)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun sendDataWithBroadCast(context: Context, position: Int, holder: ViewHolder){
        val docId = list[position].documentId

        if(!selected && list[position].documentId.isNotEmpty() && list[position].foto==""){
            selected=true

            Log.d(TAG, "sendDataWithBroadCast selected: $selected, documentID $docId")
            holder.itemView.relativeLayout.setBackgroundColor(
                Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.colorAccentDark))))

            Intent(Config.ACTION_DATA_DETAIL_SIKLUS).apply {
                putExtra("id",list[position].documentId)
                putExtra("id_ruangan", list[position].idRuangan)
                putExtra("position_selected",position)
                putExtra("di_check",list[position].diCheck)
                putExtra("foto",list[position].foto)
                LocalBroadcastManager.getInstance(context).sendBroadcast(this)
            }
        }
        if(!selected){
            Intent(Config.ACTION_DATA_DETAIL_SIKLUS).apply {
                putExtra("id","nothing_checked")
                LocalBroadcastManager.getInstance(context).sendBroadcast(this)
            }
        }
    }
}