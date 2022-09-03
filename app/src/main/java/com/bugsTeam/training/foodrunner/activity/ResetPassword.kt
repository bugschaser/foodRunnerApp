package com.bugsTeam.training.foodrunner.activity

import android.content.Intent
import android.os.Bundle
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
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.RESET_PASSWORD
import com.bugsTeam.training.foodrunner.util.ConnectionManager
import com.bugsTeam.training.foodrunner.util.ValidationUtil
import org.json.JSONObject

class ResetPassword : AppCompatActivity() {
    private lateinit var edtOTP:EditText
    private lateinit var edtPassword:EditText
    private lateinit var edtConfirmPassword:EditText
    private lateinit var btnSubmit:Button
    private var mobileNumber:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        //Setting UpUI widgets
        edtOTP=findViewById(R.id.etOTP)
        edtPassword=findViewById(R.id.etResetPassword)
        edtConfirmPassword=findViewById(R.id.etResetConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("user_mobile")!!
        }

        Toast.makeText(this@ResetPassword,mobileNumber,Toast.LENGTH_LONG).show()

        btnSubmit.setOnClickListener {
            if(!ValidationUtil.isValidPassword(edtPassword,edtConfirmPassword)) {
                return@setOnClickListener
            }
            val otp=edtOTP.text.toString()
            val newPassword=edtPassword.text.toString()

            if(ConnectionManager().isNetworkAvailable(this@ResetPassword)){
                val queue = Volley.newRequestQueue(this@ResetPassword)
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number",mobileNumber)
                jsonParams.put("password", newPassword)
                jsonParams.put("otp", otp)

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, RESET_PASSWORD,jsonParams,
                    Response.Listener {
                        try {
                            val resetObject = it.getJSONObject("data")
                            val success = resetObject.getBoolean("success")
                            if (success) {
                                val builder = AlertDialog.Builder(this@ResetPassword)
                                builder.setTitle("Confirmation")
                                builder.setMessage("Your password has been changed successfully")
                                builder.setIcon(R.drawable.ic_action_success)
                                builder.setCancelable(false)
                                builder.setPositiveButton("Ok") { _, _ ->
                                    startActivity(
                                        Intent(
                                            this@ResetPassword,
                                            LoginActivity::class.java
                                        )
                                    )
                                    ActivityCompat.finishAffinity(this@ResetPassword)
                                }
                                builder.create().show()
                            } else {
                                val error = resetObject.getString("errorMessage")
                                Toast.makeText(
                                    this@ResetPassword,
                                    error,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@ResetPassword,
                                "Incorrect Response!!",
                                Toast.LENGTH_SHORT
                            ).show()
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
                val builder = AlertDialog.Builder(this@ResetPassword)
                builder.setTitle("Error")
                builder.setMessage("No Internet Connection found. Please connect to the internet and re-open the app.")
                builder.setCancelable(false)
                builder.setPositiveButton("Ok") { _, _ ->
                    ActivityCompat.finishAffinity(this@ResetPassword)
                }
                builder.create()
                builder.show()
            }


        }

    }
}