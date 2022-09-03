package com.bugsTeam.training.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.FETCH_PREVIOUS_ORDERS
import com.bugsTeam.training.foodrunner.KEY_ID
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.adapter.OrderHistoryAdapter
import com.bugsTeam.training.foodrunner.model.OrderItemModel
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerHistory: RecyclerView
    lateinit var orderHistoryAdapter: OrderHistoryAdapter
    var orderHistoryList=arrayListOf<OrderItemModel>()
    private lateinit var relativeHistory: RelativeLayout
    private lateinit var rlNoHistory: RelativeLayout
    private lateinit var rlLoading: RelativeLayout
    private var userId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val historyView= inflater.inflate(R.layout.fragment_order_history, container, false)

        setUpUIWidgets(historyView)

        val sharedPref=(activity as FragmentActivity).getSharedPreferences(
            getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE)

        userId=sharedPref.getString(KEY_ID,"") as String

        fetchData()

        return historyView
    }

    private fun setUpUIWidgets(view: View) {
        recyclerHistory=view.findViewById(R.id.recyclerOrderHistory)
        relativeHistory=view.findViewById(R.id.rlHistory)
        rlNoHistory=view.findViewById(R.id.rlNoHistory)
        rlLoading = view.findViewById(R.id.rlLoading) as RelativeLayout
        rlLoading.visibility = View.VISIBLE
    }

    private fun fetchData() {
        if(ConnectionManager().isNetworkAvailable(activity as Context)){
            val queue = Volley.newRequestQueue(activity as Context)
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, FETCH_PREVIOUS_ORDERS+userId,null,
                Response.Listener { response ->
                    rlLoading.visibility=View.GONE
                    try {
                        val data=response.getJSONObject("data")
                        val success =data.getBoolean("success")

                        if(success){
                            val readArray=data.getJSONArray("data")
                            if (readArray.length() == 0) {
                                relativeHistory.visibility=View.GONE
                                rlNoHistory.visibility=View.VISIBLE
                            }
                            else{
                                for(i in 0 until readArray.length()){
                                    val resObject=readArray.getJSONObject(i)
                                    val orderItemModel=OrderItemModel(
                                        resObject.getString("order_id").toInt(),
                                        resObject.getString("restaurant_name"),
                                        resObject.getString("order_placed_at"),
                                        resObject.getJSONArray("food_items")
                                    )
                                    orderHistoryList.add(orderItemModel)
                                    if(orderHistoryList.isEmpty()){
                                        relativeHistory.visibility=View.GONE
                                        rlNoHistory.visibility=View.VISIBLE

                                    }else {
                                        if (activity != null) {
                                            orderHistoryAdapter =
                                                OrderHistoryAdapter(
                                                    activity as Context,
                                                    orderHistoryList
                                                )
                                            recyclerHistory.layoutManager =
                                                LinearLayoutManager(activity)
                                            recyclerHistory.itemAnimator = DefaultItemAnimator()
                                            recyclerHistory.adapter = orderHistoryAdapter
                                            recyclerHistory.setHasFixedSize(true)
                                        }
                                        else{
                                            queue.cancelAll(this::class.java.simpleName)
                                        }
                                    }
                                }
                            }

                        }

                    }catch (e: JSONException){
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    println("Error is $it")
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
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

}