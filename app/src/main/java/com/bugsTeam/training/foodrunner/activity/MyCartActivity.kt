@file:Suppress("DEPRECATION")

package com.bugsTeam.training.foodrunner.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.KEY_ID
import com.bugsTeam.training.foodrunner.PLACE_ORDER
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.adapter.MyCartAdapter
import com.bugsTeam.training.foodrunner.adapter.RestaurantDetailAdapter
import com.bugsTeam.training.foodrunner.database.CartEntity
import com.bugsTeam.training.foodrunner.database.RestaurantDatabase
import com.bugsTeam.training.foodrunner.model.MenuItemModel
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MyCartActivity : AppCompatActivity() {

    private lateinit var toolbar:Toolbar
    private lateinit var txtRestaurantName:TextView
    private lateinit var recyclerCartMenu:RecyclerView
    private lateinit var btnPlaceOrder :Button
    private lateinit var myCartAdapter: MyCartAdapter
    private var resId: Int? = 0
    private var resName: String = ""
    private var menuListInfo= arrayListOf<MenuItemModel>()
    private var sum=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        setUpUIWidget()

        setUpToolBar()

        if(intent!=null){
            val bundle=intent.getBundleExtra("data")
            resId=bundle?.getInt("id",0) as Int
            resName= bundle.getString("res_name") as String
            txtRestaurantName.text=resName
        }

        setUpCartMenu()

        btnPlaceOrder.setOnClickListener {
            onPlaceOrderClick()
        }

    }

    private fun setUpCartMenu() {
        menuListInfo.clear()
        val dbList:List<CartEntity> = getAllCartOrders(baseContext).execute().get()
        for(element in dbList){
            menuListInfo.addAll(
                Gson().fromJson(element.foodItem,Array<MenuItemModel>::class.java).asList()
            )
        }
        for(i in 0 until menuListInfo.size){
            sum += menuListInfo[i].price
        }
       if(!menuListInfo.isEmpty()) {
           myCartAdapter=MyCartAdapter(this@MyCartActivity,menuListInfo)
           recyclerCartMenu.layoutManager= LinearLayoutManager(this@MyCartActivity)
           recyclerCartMenu.itemAnimator= DefaultItemAnimator()
           recyclerCartMenu.adapter=myCartAdapter
           recyclerCartMenu.setHasFixedSize(true)
       }
        btnPlaceOrder.text="Place Order(Total: Rs. ${sum})"
    }

    private fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title=getString(R.string.my_cart)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpUIWidget() {
         toolbar=findViewById(R.id.toolbarMyCart)
         txtRestaurantName=findViewById(R.id.txtCartRestaurantName)
         recyclerCartMenu =findViewById(R.id.recyclerCartMenu)
         btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true 
    }

    override fun onBackPressed() {
        val clearAsync=clearAllCartOrders(baseContext,resId.toString()).execute().get()
        if(clearAsync){
            RestaurantDetailAdapter.cardEmpty=true
        }
        super.onBackPressed()
    }

    fun onPlaceOrderClick(){
        val sharedPreferences=this.getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
        val userId=sharedPreferences.getString(KEY_ID,"") as String
        if(ConnectionManager().isNetworkAvailable(this@MyCartActivity)) {
            val queue = Volley.newRequestQueue(this@MyCartActivity)
            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("restaurant_id", resId.toString())
            jsonParams.put("total_cost", sum.toString())
            val jsonArray = JSONArray()
            for (i in 0 until menuListInfo.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", menuListInfo[i].id.toString())
                jsonArray.put(i, foodId)
            }
            jsonParams.put("food", jsonArray)
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, PLACE_ORDER, jsonParams,
                Response.Listener { response ->
                    try {
                        println("Response is $response")
                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val clearAsync=clearAllCartOrders(baseContext,resId.toString()).execute().get()
                            if(clearAsync){
                                RestaurantDetailAdapter.cardEmpty=true
                               openSuccessPage()
                            }
                            else{
                                Toast.makeText(baseContext,
                                    "Some error occurred",
                                    Toast.LENGTH_LONG).show()
                            }
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    println("Error is $it")
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "b312a26fe9bf61"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val builder = AlertDialog.Builder(this@MyCartActivity)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(this@MyCartActivity)
            }
            builder.create()
            builder.show()
        }

    }

    private fun openSuccessPage() {
        val dialog : Dialog = Dialog(this@MyCartActivity, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        val view: View = LayoutInflater.from(this).inflate(R.layout.order_placed, null)
        dialog.setContentView(view)
        dialog.setCancelable(false)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)
        btnOk.setOnClickListener {
            dialog.dismiss()
            startActivity(
                Intent(
                    this@MyCartActivity,
                    HomeActivity::class.java
                )
            )

            ActivityCompat.finishAffinity(this@MyCartActivity)
        }
        dialog.show()
    }

    class getAllCartOrders(val context: Context):AsyncTask<Void,Void,List<CartEntity>>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val cartEntityList=db.cartDao().getAllOrders()
            db.close()
            return cartEntityList
        }

    }

    class clearAllCartOrders(val context: Context,val resId:String):AsyncTask<Void,Void,Boolean>(){
        val db= Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
           db.cartDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

}