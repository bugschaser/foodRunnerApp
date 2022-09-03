package com.bugsTeam.training.foodrunner.model

import org.json.JSONArray

data class OrderItemModel (
        val orderID:Int,
        val resName:String,
        val orderDate:String,
        val foodItems:JSONArray
)