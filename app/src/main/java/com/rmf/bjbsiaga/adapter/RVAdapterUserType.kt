package com.rmf.bjbsiaga.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rmf.bjbsiaga.R
import com.rmf.bjbsiaga.data.DataUserType
import kotlinx.android.synthetic.main.item_data_user_type.view.*

class RVAdapterUserType(var list: ArrayList<DataUserType>, val clickListener: ClickListener) : RecyclerView.Adapter<RVAdapterUserType.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_data_user_type,parent,false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        holder.itemView.text_nomer.text = (position+1).toString()
        holder.itemView.text_nama_user_type.text = data.userType

        holder.itemView.setOnClickListener {
           clickListener.onClickListener(data)
        }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ClickListener{
        fun onClickListener(dataUserType: DataUserType)
    }
}