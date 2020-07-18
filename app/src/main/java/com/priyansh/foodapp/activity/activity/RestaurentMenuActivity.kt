package com.priyansh.foodapp.activity.activity

import android.app.DownloadManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.priyansh.bookhub.util.ConnectionManager
import com.priyansh.foodapp.R
import com.priyansh.foodapp.activity.adapter.RestaurantMenuAdapter
import com.priyansh.foodapp.activity.model.RestaurentMenu
import org.json.JSONException

class RestaurentMenuActivity : AppCompatActivity() {
    lateinit var toolbar:androidx.appcompat.widget.Toolbar


    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuAdapter
    lateinit var restaurantId:String

    lateinit var restaurantName:String

    lateinit var proceedToCartLayout: RelativeLayout

    lateinit var buttonProceedToCart: Button

    lateinit var activity_restaurant_menu_Progressdialog: RelativeLayout

    var restaurantMenuList = arrayListOf<RestaurentMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurent_menu)

        proceedToCartLayout=findViewById(R.id.relativeLayoutProceedToCart)
        buttonProceedToCart=findViewById(R.id.buttonProceedToCart)
        activity_restaurant_menu_Progressdialog=findViewById(R.id.activity_restaurant_menu_Progressdialog)

        toolbar=findViewById(R.id.toolbar)

        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName=intent.getStringExtra("restaurantName")

        setToolBar()

        layoutManager = LinearLayoutManager(this)//set the layout manager

        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

        fetchData()

    }

    fun fetchData() {
        if (ConnectionManager().checkConnectivity(this)) {

            activity_restaurant_menu_Progressdialog.visibility= View.VISIBLE
            try {

                val queue = Volley.newRequestQueue(this)

                //val restaurantId:String=""

                val url = "http://13.235.250.119/v2/restaurants/fetch_result/"+restaurantId

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        println("Response of menu is " + it)

                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            restaurantMenuList.clear()

                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val menuObject = RestaurentMenu(
                                    bookJsonObject.getString("id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("cost_for_one")

                                )
                                restaurantMenuList.add(menuObject)

                                //progressBar.visibility = View.GONE

                                menuAdapter = RestaurantMenuAdapter(
                                    this,
                                    restaurantId,//pass the restaurant Id
                                    restaurantName,//pass restaurantName
                                    proceedToCartLayout,//pass the relativelayout which has the button to enable it later
                                    buttonProceedToCart,
                                    restaurantMenuList
                                )//set the adapter with the data

                                recyclerView.adapter =
                                    menuAdapter//bind the  recyclerView to the adapter

                                recyclerView.layoutManager =
                                    layoutManager //bind the  recyclerView to the layoutManager


                                //spacing between list items
                                /*recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,(layoutManager as LinearLayoutManager).orientation
                                    )
                                )*/
                            }


                        }
                        activity_restaurant_menu_Progressdialog.visibility=View.INVISIBLE
                    },
                    Response.ErrorListener {
                        println("Error12menu is " + it)

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        activity_restaurant_menu_Progressdialog.visibility=View.INVISIBLE
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
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        else {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }

    }

    fun setToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title=restaurantName
        supportActionBar?.setHomeButtonEnabled(true)//enables the button on the tool bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//displays the icon on the button
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_chevron_left_black_24dp)//change icon to custom
    }


    override fun onBackPressed() {


        if(menuAdapter.getSelectedItemCount()>0) {


            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){
            android.R.id.home->{
                if(menuAdapter.getSelectedItemCount()>0) {

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { text, listener ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("No") { text, listener ->

                    }
                    alterDialog.show()
                }else{
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
