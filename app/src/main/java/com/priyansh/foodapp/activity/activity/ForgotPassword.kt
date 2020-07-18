package com.priyansh.foodapp.activity.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.priyansh.bookhub.util.ConnectionManager
import com.priyansh.foodapp.R
import org.json.JSONException
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var btnNext : Button
    lateinit var editTextMobileNumber:EditText
    lateinit var editTextEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        title = "Forgot Password"

        btnNext = findViewById(R.id.buttonNext)
        editTextMobileNumber=findViewById(R.id.username)
        editTextEmail=findViewById(R.id.emailid)
        btnNext.setOnClickListener{
            if (editTextMobileNumber.text.isBlank())
            {
                editTextMobileNumber.setError("Mobile Number Missing")
            }else{
                if(editTextEmail.text.isBlank()){
                    editTextEmail.setError("Email Missing")
                }else{
                    if (ConnectionManager().checkConnectivity(this)) {

                        try {

                            val loginUser = JSONObject()

                            loginUser.put("mobile_number", editTextMobileNumber.text)
                            loginUser.put("email", editTextEmail.text)
                            val queue = Volley.newRequestQueue(this)

                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                            val jsonObjectRequest = object : JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val responseJsonObjectData = it.getJSONObject("data")

                                    val success = responseJsonObjectData.getBoolean("success")

                                    if (success) {

                                        val first_try=responseJsonObjectData.getBoolean("first_try")

                                        if(first_try==true){
                                            Toast.makeText(
                                                this,
                                                "OTP sent",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            val intent = Intent(this,
                                               ResetPassword::class.java)
                                            intent.putExtra("mobile_number",editTextMobileNumber.text)
                                            startActivity(intent)
                                            finish()

                                        }else{
                                            Toast.makeText(
                                                this,
                                                "OTP sent already",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val intent = Intent(this,
                                                ResetPassword::class.java)
                                            intent.putExtra("mobile number",editTextMobileNumber.text.toString())
                                            startActivity(intent)
                                            finish()
                                        }

                                    } else {
                                        val responseMessageServer =
                                            responseJsonObjectData.getString("errorMessage")
                                        Toast.makeText(
                                            this,
                                            responseMessageServer.toString(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    } },
                                Response.ErrorListener {
                                    println(it)
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
                            jsonObjectRequest.setRetryPolicy( DefaultRetryPolicy(15000,
                                1,
                                1f
                            )
                            )

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
                }
            }
        }

    }

    override fun onBackPressed(){

        val intent = Intent(this@ForgotPassword,
            LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
