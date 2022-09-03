package com.bugsTeam.training.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Insert
    fun insertOrder(cardEntities: CartEntity)

    @Delete
    fun deleteOrder(cardEntities: CartEntity)

    @Query("SELECT * FROM orders")
    fun getAllOrders():List<CartEntity>

    @Query("DELETE FROM orders WHERE resId=:resId")
    fun deleteOrders(resId:String)

}