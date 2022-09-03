package com.bugsTeam.training.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.*
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity(){

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin:Button
    private lateinit var txtForgetPassword:TextView
    private lateinit var txtRegister:TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE)

        val isLoggedIn=sharedPreferences.getBoolean(IS_LOGIN,false)

        if(isLoggedIn){
            val dashboardIntent=Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(dashboardIntent)
            finish()
        }

        setUpUIViews()
    }

    private fun setUpUIViews() {
        etMobileNumber=findViewById(R.id.etLoginMobile)
        etPassword =findViewById(R.id.etLoginPassword)
        btnLogin=findViewById(R.id.btnLogin)
        txtForgetPassword=findViewById(R.id.tvForgetPassword)
        txtRegister=findViewById(R.id.tvRegister)
    }

    fun View.registerBtnClick() {
        val registerIntent=Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    fun View.forgetBtnClick() {
        val forgetPwdIntent=Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
        startActivity(forgetPwdIntent)
    }

    fun View.loginBtnClick() {
        checkCredential()
    }

    private fun checkCredential() {
        val etMobileNumber=etMobileNumber.text.toString()
        val etPassword=etPassword.text.toString()
        if(ConnectionManager().isNetworkAvailable(this@LoginActivity)){
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number",etMobileNumber)
            jsonParams.put("password",etPassword)

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,LOGIN,jsonParams,
                Response.Listener { response ->
                    try {
                        println("Response is $response")
                        val data=response.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success){
                           val resObj=data.getJSONObject("data")
                            savePreferences(resObj.getString("user_id"),
                                resObj.getString("name"),
                                resObj.getString("mobile_number"),
                                resObj.getString("address"),
                                resObj.getString("email")
                            )
                            Toast.makeText(this@LoginActivity,
                                "Login is successfully done",
                                Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@LoginActivity,HomeActivity::class.java))
                            finish()
                        }
                        else{
                            val errMsg=data.getString("errorMessage")
                            Toast.makeText(this@LoginActivity,
                                errMsg,
                                Toast.LENGTH_LONG).show()
                        }

                    }
                    catch (e: JSONException){
                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    println("Error is $it")
                }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "b312a26fe9bf61"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }
        else{
            val builder = AlertDialog.Builder(this@LoginActivity)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(this@LoginActivity)
            }
            builder.create()
            builder.show()
        }
    }

    private fun savePreferences(
        id: String?,
        name: String?,
        mobileNumber: String?,
        address: String,
        email: String?
    ) {
        sharedPreferences.edit().putBoolean(IS_LOGIN,true).apply()
        sharedPreferences.edit().putString(KEY_ID,id).apply()
        sharedPreferences.edit().putString(KEY_NAME,name).apply()
        sharedPreferences.edit().putString(KEY_EMAIL,email).apply()
        sharedPreferences.edit().putString(KEY_MOBILE,mobileNumber).apply()
        sharedPreferences.edit().putString(KEY_ADDRESS,address).apply()
    }


}