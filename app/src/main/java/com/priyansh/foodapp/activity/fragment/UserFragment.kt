package com.priyansh.foodapp.activity.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.priyansh.foodapp.R


class UserFragment(val contextParam:Context) : Fragment() {


    lateinit var username : TextView
    lateinit var phone : TextView
    lateinit var emailid : TextView
    lateinit var addressOfuser : TextView
    lateinit var sharedPreferencess: SharedPreferences

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_user, container, false)

        username = view.findViewById(R.id.username)
        phone = view.findViewById(R.id.mobileNumber)
        emailid = view.findViewById(R.id.emailid)
        addressOfuser = view.findViewById(R.id.address)


        sharedPreferencess = contextParam.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)

        username.text=sharedPreferencess.getString("name","")
        emailid.text=sharedPreferencess.getString("email","")
        phone.text="+91-"+sharedPreferencess.getString("mobile_number","")
        addressOfuser.text=sharedPreferencess.getString("address","")


        return view

    }

}
