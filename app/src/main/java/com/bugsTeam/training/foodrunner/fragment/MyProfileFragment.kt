package com.bugsTeam.training.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bugsTeam.training.foodrunner.*


class MyProfileFragment : Fragment() {

    lateinit var txtName: TextView
    lateinit var txtEmail: TextView
    lateinit var txtMobile: TextView
    lateinit var txtAddress:TextView
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

         txtName =view.findViewById(R.id.txtUserName)
         txtEmail =view.findViewById(R.id.txtEmail)
         txtMobile =view.findViewById(R.id.txtPhone)     //SetUp an shared Preferences
         txtAddress =view.findViewById(R.id.txtAddress)
         sharedPref=(activity as FragmentActivity).getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)
         txtName.text=sharedPref.getString(KEY_NAME,"") as String
         txtEmail.text=sharedPref.getString(KEY_EMAIL,"") as String
         txtMobile.text=sharedPref.getString(KEY_MOBILE,"") as String
         txtAddress.text=sharedPref.getString(KEY_ADDRESS,"") as String
        return view
    }
}