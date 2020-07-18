package com.priyansh.foodapp.activity.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.priyansh.bookhub.util.ConnectionManager
import com.priyansh.foodapp.R
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    lateinit var txtmobileNumber : EditText
    lateinit var txtPassword : EditText
    lateinit var txtName : EditText
    lateinit var btnRegister : Button
    lateinit var txtConfirmPassword : EditText
    lateinit var txtemail : EditText
    lateinit var txtAddress : EditText
    lateinit var sharedPreferencess: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {


        title= "Register Yourself"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        txtAddress = findViewById(R.id.address)
        txtPassword = findViewById(R.id.password)
        txtConfirmPassword = findViewById(R.id.confirmPassword)
        txtName = findViewById(R.id.nameOfuser)
        txtmobileNumber =findViewById(R.id.mobile)
        txtemail = findViewById(R.id.emailid)
        btnRegister =findViewById(R.id.buttonRegister)





        btnRegister.setOnClickListener {


            if(txtPassword.text.isBlank() || txtPassword.text.length <8)
            {
                txtPassword.error="Invalid password 8 numbers min."
                return@setOnClickListener
            }

            if (txtConfirmPassword.text.isBlank()){
                txtConfirmPassword.error="Password Empty"
            return@setOnClickListener
            }
            else if ((txtConfirmPassword.text.toString().toInt())!= txtPassword.text.toString().toInt())
                {
                    txtConfirmPassword.error="Password don't match"
                    return@setOnClickListener
                }

            if (txtmobileNumber.text.length<10 || txtmobileNumber.text.length>10 ) {
                txtmobileNumber.error = "Invalid"
                return@setOnClickListener
            }

            if (txtName.text.isBlank() || txtName.text.length < 2) {
                txtName.error= "Field Missing!"
                return@setOnClickListener
            }

            if(txtAddress.text.isBlank()){
                txtAddress.error="Address invalid"
                return@setOnClickListener
            }

            if(txtemail.text.isBlank()){
                txtemail.error="Invalid mail"
                return@setOnClickListener
            }


            sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)
            sharedPreferencess.edit().putBoolean("user_logged_in", false).apply()


            if (ConnectionManager().checkConnectivity(this@RegisterActivity))
            {
                try {

                    val registerUser = JSONObject()
                    registerUser.put("name", txtName.text.toString())
                    registerUser.put("mobile_number", txtmobileNumber.text.toString())
                    registerUser.put("password", txtPassword.text.toString())
                    registerUser.put("address", txtAddress.text.toString())
                    registerUser.put("email", txtemail.text.toString())


                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    val url = "http://13.235.250.119/v2/register/fetch_result"
                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
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
                                    this@RegisterActivity,
                                    "Registered sucessfully",
                                    Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(
                                    this@RegisterActivity,
                                    LoginActivity::class.java
                                )
                                startActivity(intent)
                                finish()

                        }
                            else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_SHORT).show()



                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()

                            headers["Content-type"] = "application/json"
                            headers["token"] ="3990e5266ecbe7"

                            return headers
                        }
                    }

                    queue.add(jsonObjectRequest)




                }catch (e: JSONException) {
                    Toast.makeText(this,
                        "Some unexpected error occured!!!",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }else
            {
                val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this@RegisterActivity)

                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings"){text,listener->
                    val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                    startActivity(settingsIntent)

                }

                alterDialog.setNegativeButton("Exit"){ text,listener->
                    ActivityCompat.finishAffinity(this@RegisterActivity)//closes all the instances of the app and the app closes completely
                }
                alterDialog.create()
                alterDialog.show()

            }
        }


            }

    override fun onBackPressed(){

        val intent = Intent(this@RegisterActivity,
            LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}




