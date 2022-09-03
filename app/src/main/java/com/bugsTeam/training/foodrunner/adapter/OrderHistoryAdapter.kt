package com.bugsTeam.training.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.model.MenuItemModel
import com.bugsTeam.training.foodrunner.model.OrderItemModel
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderHistoryAdapter(val context: Context,val historyArrayList: ArrayList<OrderItemModel>) :
    RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewModel>() {

    class OrderHistoryViewModel(itemView: View) :RecyclerView.ViewHolder(itemView){
        val txtRestaurantName:TextView=itemView.findViewById(R.id.txtHistoryResName)
        val txtOrderDate:TextView=itemView.findViewById(R.id.txtOrderDate)
        val recyclerOrderItem:RecyclerView=itemView.findViewById(R.id.recyclerOrderHistoryDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewModel {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.order_history_row_layout, parent,false)
        return OrderHistoryViewModel(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewModel, position: Int) {
        val orderobject=historyArrayList[position]
        holder.txtRestaurantName.text=orderobject.resName
        holder.txtOrderDate.text=formatDate(orderobject.orderDate)
        setUpRecyclerView(holder.recyclerOrderItem,orderobject.foodItems)
    }

    private fun setUpRecyclerView(recyclerOrderItem: RecyclerView, foodItems: JSONArray) {
        val foodItemList=ArrayList<MenuItemModel>()
        for(i in 0 until foodItems.length()){
            val foodJsonItem=foodItems.getJSONObject(i)
            foodItemList.add(
                MenuItemModel(
                    foodJsonItem.getString("food_item_id").toInt(),
                    foodJsonItem.getString("name"),
                    foodJsonItem.getString("cost").toInt()
                )
            )
        }
        recyclerOrderItem.layoutManager= LinearLayoutManager(context)
        recyclerOrderItem.itemAnimator= DefaultItemAnimator()
        recyclerOrderItem.adapter=MyCartAdapter(context,foodItemList)



    }

    override fun getItemCount(): Int {
        return historyArrayList.size
    }


    private fun formatDate(inputDate:String):String{
        val inputFormatter=SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val inputDateFormat:Date=inputFormatter.parse(inputDate) as Date
        val outputFormatter=SimpleDateFormat("dd/MM/yy", Locale.ENGLISH)
        return outputFormatter.format(inputDateFormat)
    }
}