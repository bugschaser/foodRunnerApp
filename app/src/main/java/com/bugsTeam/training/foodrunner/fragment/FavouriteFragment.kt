@file:Suppress("DEPRECATION")

package com.bugsTeam.training.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.adapter.AllRestaurantsAdapter
import com.bugsTeam.training.foodrunner.database.RestaurantDatabase
import com.bugsTeam.training.foodrunner.database.RestaurantEntity
import com.bugsTeam.training.foodrunner.model.RestaurantsModel


class FavouriteFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var restaurantsAdapter: AllRestaurantsAdapter
    var restaurantList=arrayListOf<RestaurantsModel>()
    private lateinit var relativeFavourites: RelativeLayout
    private lateinit var rlNoFavourites: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val favView=inflater.inflate(R.layout.fragment_favourite, container, false)

        relativeFavourites = favView.findViewById(R.id.rlFavourites)
        rlNoFavourites = favView.findViewById(R.id.rlNoFavorites)
        recyclerFavourite=favView.findViewById(R.id.recyclerFavRestaurant)

        setUpFavFragment()
        return favView
    }

    private fun setUpFavFragment() {
    val backgroundList=FavAsynctask(activity as Context).execute().get()
        if(backgroundList.isEmpty()){
            relativeFavourites.visibility=View.GONE
            rlNoFavourites.visibility=View.VISIBLE
        }
        else{
            relativeFavourites.visibility=View.VISIBLE
            rlNoFavourites.visibility=View.GONE
            for(i in backgroundList){
                restaurantList.add(
                    RestaurantsModel(
                        i.id,
                        i.name,
                        i.rating,
                        i.costForTwo,
                        i.imageUrl
                    )
                )
            }
            restaurantsAdapter= AllRestaurantsAdapter(activity as Context,restaurantList)
            recyclerFavourite.layoutManager= LinearLayoutManager(activity)
            recyclerFavourite.itemAnimator= DefaultItemAnimator()
            recyclerFavourite.adapter=restaurantsAdapter
            recyclerFavourite.setHasFixedSize(true)
        }
    }

    class FavAsynctask(context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val resList= db.restaurantDao().getAllRestaurants()
            db.close()
            return resList
        }
    }


}