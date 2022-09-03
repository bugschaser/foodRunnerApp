package com.bugsTeam.training.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.model.MenuItemModel

class MyCartAdapter(val context: Context, var myCartArrayList: ArrayList<MenuItemModel>):
    RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.my_cart_card_row_layout, parent,false)
        return MyCartViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        var cartObject=myCartArrayList[position]
        holder.txtCartItemName.text=cartObject.name
        holder.txtCartItemCost.text="Rs. ${cartObject.price.toString()}"
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
       return myCartArrayList.size
    }

    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtCartItemName:TextView=itemView.findViewById(R.id.txtCartItemName)
        val txtCartItemCost:TextView=itemView.findViewById(R.id.txtCartPrice)
    }
}