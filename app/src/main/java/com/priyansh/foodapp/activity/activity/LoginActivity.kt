package com.priyansh.foodapp.activity.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.priyansh.bookhub.util.ConnectionManager
import com.priyansh.foodapp.R
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var txtmobileNumber : EditText
    lateinit var txtPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtForgotPassword : TextView
    lateinit var txtRegisterYourself : TextView
    lateinit var sharedPreferencess: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferencess.getBoolean("user_logged_in", false)

        setContentView(R.layout.activity_login)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        txtmobileNumber=findViewById(R.id.username)
        txtRegisterYourself = findViewById(R.id.register)
        txtPassword = findViewById(R.id.password)
        txtForgotPassword = findViewById(R.id.forgotPassword)
        btnLogin = findViewById(R.id.buttonLogin)


        txtRegisterYourself.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                RegisterActivity::class.java
            )
            startActivity(intent)
            finish()
        }


        txtForgotPassword.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                ForgotPassword::class.java
            )
            startActivity(intent)
            finish()
        }



        btnLogin.setOnClickListener {


            if (txtmobileNumber.text.length<10 || txtmobileNumber.text.length>10 ) {
                txtmobileNumber.error = "Invalid Number"
                return@setOnClickListener
            }
            if(txtPassword.text.isBlank() || txtPassword.text.length <8)
            {
                txtPassword.error="Invalid password"
                return@setOnClickListener
            }





            if (ConnectionManager().checkConnectivity(this)) {

                //login_fragment_Progressdialog.visibility=View.VISIBLE
                try {

                    val loginUser = JSONObject()

                    loginUser.put("mobile_number", txtmobileNumber.text)
                    loginUser.put("password", txtPassword.text)


                    val queue = Volley.newRequestQueue(this)

                    val url = "http://13.235.250.119/v2/login/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        loginUser,
                        Response.Listener {

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")


                            if (success) {

                                val data = responseJsonObjectData.getJSONObject("data")
                                sharedPreferencess.edit().putBoolean("user_logged_in", true).apply()
                                sharedPreferencess.edit().putString("user_id", data.getString("user_id")).apply()
                                sharedPreferencess.edit().putString("name", data.getString("name")).apply()
                                sharedPreferencess.edit().putString("email", data.getString("email")).apply()
                                sharedPreferencess.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                sharedPreferencess.edit().putString("address", data.getString("address")).apply()

                                Toast.makeText(
                                    this,
                                    "Welcome "+data.getString("name"),
                                    Toast.LENGTH_SHORT
                                ).show()


                                val intent = Intent(
                                    this@LoginActivity,
                                    MainActivity::class.java
                                )
                                startActivity(intent)
                                finish()


                            } else {


                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()


                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            headers["Content-type"] = "application/json"
                            headers["token"] = "3990e5266ecbe7"

                            return headers
                        }
                    }

                    queue.add(jsonObjectRequest)

                } catch (e: JSONException) {


                    Toast.makeText(
                        this,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }else
            {
                val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this@LoginActivity)

                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings"){text,listener->
                    val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                    startActivity(settingsIntent)

                }

                alterDialog.setNegativeButton("Exit"){ text,listener->
                    ActivityCompat.finishAffinity(this@LoginActivity)  //closes all the instances of the app and the app closes completely
                }
                alterDialog.create()
                alterDialog.show()

            }




        }


        }

    }



