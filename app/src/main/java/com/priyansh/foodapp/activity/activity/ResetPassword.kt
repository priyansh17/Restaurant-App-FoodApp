package com.priyansh.foodapp.activity.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
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

class ResetPassword : AppCompatActivity() {

    lateinit var editTextOTP:EditText
    lateinit var editTextNewPassword:EditText
    lateinit var editTextConfirmPasswordForgot: EditText
    lateinit var buttonSubmit : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        val mobile_number=intent.getStringExtra("mobile number")
        editTextOTP=findViewById(R.id.otp)
        editTextNewPassword=findViewById(R.id.password)
        editTextConfirmPasswordForgot=findViewById(R.id.confirmPassword)
        buttonSubmit=findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener{


            if(editTextOTP.text.isBlank()){
                editTextOTP.error="OTP missing"
            }else{
                if(editTextNewPassword.text.isBlank())
                {
                    editTextNewPassword.setError("Password Missing")
                }else{
                    if(editTextConfirmPasswordForgot.text.isBlank()){
                        editTextConfirmPasswordForgot.setError("Confirm Password Missing")
                    }else{
                        if((editTextNewPassword.text.toString().toInt()==editTextConfirmPasswordForgot.text.toString().toInt()))
                        { if (ConnectionManager().checkConnectivity(this)) {


                            try {

                                val loginUser = JSONObject()

                                loginUser.put("mobile_number", mobile_number)
                                loginUser.put("password", editTextNewPassword.text.toString())
                                loginUser.put("otp", editTextOTP.text.toString())

                                val queue = Volley.newRequestQueue(this)

                                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                val jsonObjectRequest = object : JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    loginUser,
                                    Response.Listener {

                                        val responseJsonObjectData = it.getJSONObject("data")

                                        val success = responseJsonObjectData.getBoolean("success")

                                        if (success) {

                                            val serverMessage=responseJsonObjectData.getString("successMessage")

                                            Toast.makeText(
                                                this,
                                                serverMessage,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val intent = Intent(this,
                                                LoginActivity::class.java)
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


                                        Toast.makeText(
                                            this,
                                            "mSome Error occurred!!!",
                                            Toast.LENGTH_SHORT
                                        ).show()

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
                            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)

                            alterDialog.setTitle("No Internet")
                            alterDialog.setMessage("Internet Connection can't be establish!")
                            alterDialog.setPositiveButton("Open Settings"){text,listener->
                                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                                startActivity(settingsIntent)

                            }

                            alterDialog.setNegativeButton("Exit"){ text,listener->
                                ActivityCompat.finishAffinity(this)//closes all the instances of the app and the app closes completely
                            }
                            alterDialog.create()
                            alterDialog.show()
                        }

                        }else{

                            editTextConfirmPasswordForgot.error = "Passwords don't match"

                        }
                    }
                }
            }

        }
    }





    override fun onBackPressed(){

        val intent = Intent(this@ResetPassword,
            ForgotPassword::class.java)
        startActivity(intent)
        finish()
    }
}
