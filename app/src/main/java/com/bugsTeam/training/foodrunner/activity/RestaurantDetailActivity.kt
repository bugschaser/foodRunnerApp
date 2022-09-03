@file:Suppress("DEPRECATION")

package com.bugsTeam.training.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.adapter.RestaurantDetailAdapter
import com.bugsTeam.training.foodrunner.database.CartEntity
import com.bugsTeam.training.foodrunner.database.RestaurantDatabase
import com.bugsTeam.training.foodrunner.model.MenuItemModel
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException

class RestaurantDetailActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var isImagFav:ImageView
    lateinit var menuRecyclerView: RecyclerView
    lateinit var menuAdapter: RestaurantDetailAdapter
    var menuListInfo=arrayListOf<MenuItemModel>()
    var orderListInfo= arrayListOf<MenuItemModel>()
    lateinit var proceedBtn:Button
    var resId: Int? = 0
    var resName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        setUpUIWidget()

        if(intent!=null){
            val bundle=intent.getBundleExtra("data")
            resId=bundle?.getInt("id",0) as Int
            resName= bundle.getString("res_name") as String
            if(bundle.getString("isImagFav").toString().equals("true")){
                isImagFav.setImageResource(R.drawable.ic_action_fav_checked)
            }
        }
        if(resId==0){
            Toast.makeText(this@RestaurantDetailActivity,"some error", Toast.LENGTH_SHORT).show()
        }

        //Setting Up Toolbar
        setUpToolbar(resName)

        //fetching menu from restaurant Id
        fetchRestaurantMenu()

        proceedBtn.setOnClickListener {
            val menuitem= Gson().toJson(orderListInfo)
            val asyncData=ItemInCart(baseContext, resId.toString(),menuitem).execute().get()
            if (asyncData) {
                val data = Bundle()
                data.putInt("id", resId as Int)
                data.putString("res_name", resName)
                val intent = Intent(this@RestaurantDetailActivity,MyCartActivity::class.java)
                intent.putExtra("data", data)// wealso send some data to cart activity as it might be useful there
                startActivity(intent)
            } else {
                Toast.makeText((this@RestaurantDetailActivity), "Some unexpected error", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun fetchRestaurantMenu() {
        if(ConnectionManager().isNetworkAvailable(this@RestaurantDetailActivity)){
            val queue = Volley.newRequestQueue(this@RestaurantDetailActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET, url+resId,null,
                Response.Listener { response ->
                    try {
                        val data=response.getJSONObject("data")
                        val success =data.getBoolean("success")

                        if(success){
                            val readArray=data.getJSONArray("data")
                            for(i in 0 until readArray.length()){
                                val resObject=readArray.getJSONObject(i)
                                val restaurantsMenu=MenuItemModel(
                                    resObject.getString("id").toInt(),
                                    resObject.getString("name"),
                                    resObject.getString("cost_for_one").toInt()
                                )
                                menuListInfo.add(restaurantsMenu)
                                menuAdapter=RestaurantDetailAdapter(this@RestaurantDetailActivity, menuListInfo,
                                    object : RestaurantDetailAdapter.OnItemClickListener{
                                        override fun onAddItemClick(menuItem: MenuItemModel) {
                                           orderListInfo.add(menuItem)
                                            if(orderListInfo.size>0){
                                                proceedBtn.visibility=View.VISIBLE
                                                RestaurantDetailAdapter.cardEmpty=false
                                            }
                                        }

                                        override fun onRemoveItemClick(menuItem: MenuItemModel) {
                                            orderListInfo.remove(menuItem)
                                            if(orderListInfo.isEmpty()){
                                                proceedBtn.visibility=View.GONE
                                                RestaurantDetailAdapter.cardEmpty=true
                                            }
                                        }

                                    }
                                )
                                menuRecyclerView.layoutManager=LinearLayoutManager(baseContext)
                                menuRecyclerView.itemAnimator= DefaultItemAnimator()
                                menuRecyclerView.adapter=menuAdapter
                                menuRecyclerView.setHasFixedSize(true)
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
                    headers["token"]="b312a26fe9bf61"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val builder = AlertDialog.Builder(this@RestaurantDetailActivity)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            builder.create()
            builder.show()
        }
    }

    private fun setUpToolbar(resName: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title=resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setUpUIWidget() {
        toolbar=findViewById(R.id.toolbarDetail)
        isImagFav=findViewById(R.id.isImgFav)
        menuRecyclerView=findViewById(R.id.recyclerRestaurantsMenu)
        proceedBtn=findViewById(R.id.btnGoToCart)
        proceedBtn.visibility=View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
            val builder = AlertDialog.Builder(this@RestaurantDetailActivity)
            builder.setTitle("Confirmatiom")
            builder.setMessage("Going back will reset your carts item.Do you still want to Proceed?")
            builder.setPositiveButton("Yes") { _, _ ->
               orderListInfo.clear()
                RestaurantDetailAdapter.cardEmpty=true
                super.onBackPressed()
                finish()
            }
            builder.setNegativeButton("No"){ _ ,_ ->

            }
            builder.create()
            builder.show()
        }

    @Suppress("DEPRECATION")
    class ItemInCart (val context: Context,val resId:String,val orderItems:String):
        AsyncTask<Void, Void, Boolean>(){
        val db=Room.databaseBuilder(context,RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().deleteOrders(resId)
            db.cartDao().insertOrder(CartEntity(resId,orderItems))
            db.close()
            return true
        }

    }



}