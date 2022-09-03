package com.bugsTeam.training.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bugsTeam.training.foodrunner.FORGOT_PASSWORD
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class ForgetPasswordActivity : AppCompatActivity() {


    private  lateinit var btnNext: Button
    private  lateinit var edtForgotMobile:EditText
    private  lateinit var edtForgotEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        edtForgotEmail=findViewById(R.id.etForgetEmail)
        edtForgotMobile=findViewById(R.id.etForgetMobile)
        btnNext=findViewById(R.id.btnNext)

    }

    fun View.NextBtnClick() {
        val mobile= edtForgotMobile.text.toString()
        val email= edtForgotEmail.text.toString()

        if(ConnectionManager().isNetworkAvailable(this@ForgetPasswordActivity)){
            val queue = Volley.newRequestQueue(this@ForgetPasswordActivity)
            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", mobile)
            jsonParams.put("email", email)

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST, FORGOT_PASSWORD,jsonParams,
                Response.Listener { response ->
                    try {
                        println("Response is $response")
                        val data=response.getJSONObject("data")
                        val success=data.getBoolean("success")
                        if(success){
                            val firstTry=data.getBoolean("first_try")

                            if(firstTry){
                                val builder=AlertDialog.Builder(this@ForgetPasswordActivity)
                                builder.setTitle("Information").setCancelable(false)
                                builder.setMessage("Please check your registered Email for the OTP")
                                    .setPositiveButton("OK"){_,_->
                                        val intent = Intent(
                                            this@ForgetPasswordActivity,
                                            ResetPassword::class.java
                                        )
                                        intent.putExtra("user_mobile", mobile)
                                        startActivity(intent)
                                }
                                builder.create().show()

                            }
                            else{
                                val builder=AlertDialog.Builder(this@ForgetPasswordActivity)
                                builder.setTitle("Information").setCancelable(false)
                                builder.setMessage("Please refer to the previous email for the OTP.P")
                                    .setPositiveButton("OK"){_,_->
                                        val intent = Intent(
                                            this@ForgetPasswordActivity,
                                            ResetPassword::class.java
                                        )
                                        intent.putExtra("user_mobile", mobile)
                                        startActivity(intent)
                                    }
                                builder.create().show()
                            }
                        }
                        else{
                            val errMsg=data.getString("errorMessage")
                            Toast.makeText(
                                this@ForgetPasswordActivity,
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
            val builder = AlertDialog.Builder(this@ForgetPasswordActivity)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
            builder.setCancelable(false)
            builder.setPositiveButton("Ok") { _, _ ->
                ActivityCompat.finishAffinity(this@ForgetPasswordActivity)
            }
            builder.create()
            builder.show()
        }

    }
}