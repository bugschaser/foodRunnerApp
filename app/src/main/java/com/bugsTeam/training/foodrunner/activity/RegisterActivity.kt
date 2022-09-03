package com.bugsTeam.training.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.*
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import com.bugsTeam.training.foodrunner.util.ValidationUtil
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobile: EditText
    lateinit var etAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister:Button
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setUpUIWidgets()
        setUpToolbar()
        sharedPreferences=getSharedPreferences(getString(R.string.preferences_file_name),
            Context.MODE_PRIVATE)

        btnRegister.setOnClickListener {

           if(!ValidationUtil.isValidName(etName) or !ValidationUtil.isValidEmail(etEmail) or
                !ValidationUtil.isValidMobile(etMobile) or !ValidationUtil.isValidAddress(etAddress) or
                    !ValidationUtil.isValidPassword(etPassword,etConfirmPassword)){
                return@setOnClickListener
            }
            sendData()


        }

    }

    private fun setUpUIWidgets() {
        etName=findViewById(R.id.etName)
        etEmail=findViewById(R.id.etEmailAddress)
        etMobile=findViewById(R.id.etMobile)
        etAddress=findViewById(R.id.etAddress)
        etPassword=findViewById(R.id.etResetPassword)
        etConfirmPassword=findViewById(R.id.etResetConfirmPassword)
        btnRegister=findViewById(R.id.btnRegister)
        toolbar=findViewById(R.id.toolbar)
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun savePreferences(
        id: String?,
        name: String?,
        mobileNumber: String?,
        address: String,
        email: String?
    ) {
        sharedPreferences.edit().putString(KEY_ID,id).apply()
        sharedPreferences.edit().putString(KEY_NAME,name).apply()
        sharedPreferences.edit().putString(KEY_EMAIL,email).apply()
        sharedPreferences.edit().putString(KEY_MOBILE,mobileNumber).apply()
        sharedPreferences.edit().putString(KEY_ADDRESS,address).apply()
    }

    private fun sendData(){
        val name=etName.text.toString()
        val mobile=etMobile.text.toString()
        val email=etEmail.text.toString()
        val address=etAddress.text.toString()
        val password=etPassword.text.toString()
        if(ConnectionManager().isNetworkAvailable(this@RegisterActivity)){
            val queue = Volley.newRequestQueue(this@RegisterActivity)
            val jsonParams = JSONObject()
            jsonParams.put("name", name);
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("password", password)
            jsonParams.put("address", address)
            jsonParams.put("email", email)

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, REGISTER,jsonParams,
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
                            Toast.makeText(this@RegisterActivity, "\nRegistration is successfully done",
                                Toast.LENGTH_LONG).show()
                            startActivity(Intent(this@RegisterActivity,HomeActivity::class.java))
                        }
                        else{
                            val errMsg=data.getString("errorMessage")
                            Toast.makeText(this@RegisterActivity,
                                errMsg,
                                Toast.LENGTH_LONG).show()
                        }

                    }
                    catch (e:JSONException){
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
            val builder = AlertDialog.Builder(this@RegisterActivity)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(this@RegisterActivity)
            }
            builder.create()
            builder.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }





}