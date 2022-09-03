package com.bugsTeam.training.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.model.MenuItemModel

class RestaurantDetailAdapter(val context: Context, private val menuList: ArrayList<MenuItemModel>,
                              private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RestaurantDetailAdapter.AllMenuViewHolder>() {

    companion object{
        var cardEmpty=true
    }

    class AllMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtSNo:TextView=itemView.findViewById(R.id.txtSNo)
        val txtItemName:TextView=itemView.findViewById(R.id.txtItemName)
        val txtItemCost:TextView=itemView.findViewById(R.id.txtItemCost)
        val btnAddToCart: Button =itemView.findViewById(R.id.btnAddToCart)
        val btnRemoveFromCart: Button = itemView.findViewById(R.id.btnRemoveFromCart)
    }

    interface OnItemClickListener{
        fun onAddItemClick(menuItem:MenuItemModel)
        fun onRemoveItemClick(menuItem:MenuItemModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllMenuViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_card_row_layout,parent,false)
        return RestaurantDetailAdapter.AllMenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllMenuViewHolder, position: Int) {
        val menuObjects= menuList[position]
        holder.txtSNo.text="${position+1}"
        holder.txtItemName.text=menuObjects.name
        holder.txtItemCost.text=menuObjects.price.toString()
        holder.btnAddToCart.setOnClickListener {
            holder.btnAddToCart.visibility=View.GONE
            holder.btnRemoveFromCart.visibility=View.VISIBLE
            listener.onAddItemClick(menuObjects)
        }
        holder.btnRemoveFromCart.setOnClickListener {
            holder.btnRemoveFromCart.visibility=View.GONE
            holder.btnAddToCart.visibility=View.VISIBLE
            listener.onRemoveItemClick(menuObjects)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}