package com.bugsTeam.training.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class CartEntity (
        @PrimaryKey val resId:String,
        @ColumnInfo(name="food_items")val foodItem:String
   )