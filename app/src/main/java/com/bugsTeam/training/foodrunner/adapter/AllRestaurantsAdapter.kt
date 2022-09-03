@file:Suppress("DEPRECATION")

package com.bugsTeam.training.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.activity.RestaurantDetailActivity
import com.bugsTeam.training.foodrunner.database.RestaurantDatabase
import com.bugsTeam.training.foodrunner.database.RestaurantEntity
import com.bugsTeam.training.foodrunner.model.RestaurantsModel
import com.squareup.picasso.Picasso
import java.util.*

@Suppress("DEPRECATION")
class AllRestaurantsAdapter(val context: Context, var restaurants:ArrayList<RestaurantsModel>):
    RecyclerView.Adapter<AllRestaurantsAdapter.AllRestaurantsViewHolder>() {

    class AllRestaurantsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val nameTxtView:TextView=itemView.findViewById(R.id.txtRestaurantName)
        val costTxtView:TextView=itemView.findViewById(R.id.txtCostForTwo)
        val ratingTxtView:TextView=itemView.findViewById(R.id.txtRestaurantRating)
        val restaurantImage:ImageView=itemView.findViewById(R.id.imgRestaurantThumbnail)
        val cardRestaurants:CardView=itemView.findViewById(R.id.cardRestaurant)
        val favImageView:ImageView=itemView.findViewById(R.id.imgIsFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRestaurantsViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_row_layout,parent,false)
        return AllRestaurantsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllRestaurantsViewHolder, position: Int) {
        val resObjects= restaurants.get(position)
        holder.nameTxtView.text=resObjects.name
        holder.costTxtView.text="${resObjects.costForTwo.toString()}/persons"
        holder.ratingTxtView.text=resObjects.rating
        Picasso.get().load(resObjects.imageUrl).error(R.drawable.res_image).into(holder.restaurantImage)

        // this code shows whether the restauraant is stored in fovourtes or not using heart img
        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()
        if (listOfFavourites.isNotEmpty() &&
            listOfFavourites.contains(resObjects.id.toString())) {
            holder.favImageView.setImageResource(R.drawable.ic_action_fav_checked)
        } else {
            holder.favImageView.setImageResource(R.drawable.ic_action_fav)
        }

        val resEntity=RestaurantEntity(resObjects.id,
                resObjects.name,resObjects.rating,resObjects.costForTwo,resObjects.imageUrl)
        val isFav=DBAsyncTask(context,resEntity,1).execute().get()

        holder.cardRestaurants.setOnClickListener {
            val dataBundle=Bundle()
            dataBundle.putInt("id",resObjects.id)
            dataBundle.putString("res_name",resObjects.name)
            dataBundle.putString("isImagFav",isFav.toString())
            val cardIntent= Intent(context,RestaurantDetailActivity::class.java)
            cardIntent.putExtra("data",dataBundle)
            context.startActivity(cardIntent)
        }
        holder.favImageView.setOnClickListener {
            if(!isFav){
                //if Entity is add successfully change favourite image
                if (DBAsyncTask(context,resEntity,2).execute().get()) {
                    holder.favImageView.setImageResource(R.drawable.ic_action_fav_checked)
                    Toast.makeText(context,"${resEntity.name} restaurant Added to favourites",
                        Toast.LENGTH_SHORT).show() }
            }
            else{
                //if Entity is removed successfully change favourite image
                if (DBAsyncTask(context,resEntity,3).execute().get()) {
                    holder.favImageView.setImageResource(R.drawable.ic_action_fav)
                    Toast.makeText(context,"${resEntity.name} restaurant removed From favourites",
                        Toast.LENGTH_SHORT).show()

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    @Suppress("DEPRECATION")
    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){
        /*
        Mode 1->Check DB if the restaurant is or favourite or not
        Mode 2->Save the restaurant into DB as favourite
        Mode 3->Remove the favourite restaurant
         */

        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    //Check DB if the restaurant is or favourite or not
                    val resEntity: RestaurantEntity?=db.restaurantDao().getRestaurantById(
                        restaurantEntity.id.toString())
                    db.close()
                    return resEntity != null
                }
                2->{
                    //Save the restaurant into DB as favourite
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3->{
                    //Remove the favourite restaurant
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

            }
            return false
        }


    }

    class GetAllFavAsyncTask(context: Context): AsyncTask<Void, Void, List<String>>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {
            val listOfDb=db.restaurantDao().getAllRestaurants()
            val listOfFav= arrayListOf<String>()
            for (i in listOfDb){
                listOfFav.add(i.id.toString())
            }
            return listOfFav
        }
    }

    public fun filterList(filteredRestaurants:ArrayList<RestaurantsModel>){
        restaurants=filteredRestaurants
        notifyDataSetChanged()
    }

}