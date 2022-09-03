package com.bugsTeam.training.foodrunner.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.FETCH_RESTAURANTS
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.adapter.AllRestaurantsAdapter
import com.bugsTeam.training.foodrunner.model.RestaurantsModel
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment : Fragment() {


    private lateinit var progressLayout: RelativeLayout
    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var restaurantsAdapter: AllRestaurantsAdapter
    private var restaurantList=arrayListOf<RestaurantsModel>()
    private lateinit var etSearch:EditText
    private var previousMenuItem:MenuItem?=null


    // this the comparator based on the price
    private var costComparator=Comparator<RestaurantsModel>{ resObject1, resObject2 ->
        val priceOne=resObject1.costForTwo
        val priceTwo=resObject2.costForTwo
        if (priceOne.compareTo(priceTwo) == 0) {
            ratingComparator.compare(resObject1, resObject2)
        } else {
            priceOne.compareTo(priceTwo)
        }
    }

    // this the comparator based on the price
    private var ratingComparator=Comparator<RestaurantsModel>{ resObject1, resObject2 ->
        val priceOne=resObject1.rating.toFloat()
        val priceTwo=resObject2.rating.toFloat()
        if (priceOne.compareTo(priceTwo) == 0) {
            val costOne=resObject1.costForTwo
            val costTwo=resObject2.costForTwo
            costOne.compareTo(costTwo)
        } else {
            priceOne.compareTo(priceTwo)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE
        //This method fetch data from internet in form of json using volley libraries
        fetchData(view)

        //search operation are performed here
        etSearch=view.findViewById(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }

        })

        return view
    }

    @SuppressLint("DefaultLocale")
    private fun filter(text: String) {
        val filteredList= arrayListOf<RestaurantsModel>()
        for (s in restaurantList) {
            //if the existing elements contains the search input
            if (s.name.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filteredList.add(s)
            }
        }
        restaurantsAdapter.filterList(filteredList)
    }

    private fun fetchData(view: View) {
        recyclerRestaurant=view.findViewById(R.id.recyclerRestaurants)
        if(ConnectionManager().isNetworkAvailable(activity as Context)){
            val queue = Volley.newRequestQueue(activity as Context)
            val jsonObjectRequest = object : JsonObjectRequest(
                Method.GET, FETCH_RESTAURANTS, null,
                Response.Listener { response ->
                    progressLayout.visibility=View.GONE
                    try {
                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val readArray = data.getJSONArray("data")
                            for (i in 0 until readArray.length()) {
                                val resObject = readArray.getJSONObject(i)
                                val restaurants = RestaurantsModel(
                                    resObject.getString("id").toInt(),
                                    resObject.getString("name"),
                                    resObject.getString("rating"),
                                    resObject.getString("cost_for_one").toInt(),
                                    resObject.getString("image_url")
                                )
                                restaurantList.add(restaurants)
                                if (activity != null) {
                                    restaurantsAdapter = AllRestaurantsAdapter(
                                        activity as Context,
                                        restaurantList
                                    )
                                    recyclerRestaurant.layoutManager = LinearLayoutManager(activity)
                                    recyclerRestaurant.itemAnimator = DefaultItemAnimator()
                                    recyclerRestaurant.adapter = restaurantsAdapter
                                    recyclerRestaurant.setHasFixedSize(true)
                                }
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    println("Error is $it")
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String, String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="9bf534118365f1"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val builder = AlertDialog.Builder(activity as Context)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            builder.create()
            builder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id= item.itemId
        if (previousMenuItem != null) {
            previousMenuItem?.isChecked = false
        }
        item.isChecked = true

        when (id) {
            R.id.costLowToHigh -> {
                Collections.sort(restaurantList, costComparator)
                restaurantsAdapter.notifyDataSetChanged()
            }
            R.id.costHighToLow -> {
                Collections.sort(restaurantList, costComparator)
                restaurantList.reverse()
                restaurantsAdapter.notifyDataSetChanged()
            }
            R.id.rating -> {
                Collections.sort(restaurantList, ratingComparator)
                restaurantList.reverse()
                restaurantsAdapter.notifyDataSetChanged()
            }
        }
        previousMenuItem = item
        return super.onOptionsItemSelected(item)
    }
}