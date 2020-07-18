package com.priyansh.foodapp.activity.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.priyansh.bookhub.util.ConnectionManager

import com.priyansh.foodapp.R
import com.priyansh.foodapp.activity.adapter.DashboardRecyclerAdapter
import com.priyansh.foodapp.activity.model.Restaurent
import kotlinx.android.synthetic.main.sort_radio_button.view.*
import org.json.JSONException
import java.util.*


class HomeFragment : Fragment() {

    lateinit var  recyclerDashboard : RecyclerView
    lateinit var  layoutManager : RecyclerView.LayoutManager
    lateinit var ProgressBar : ProgressBar
    lateinit var radioButtonView:View
    lateinit var ProgressLayout : RelativeLayout
    lateinit var  recyclerAdapter : DashboardRecyclerAdapter
    val RestaurentInfoList = arrayListOf<Restaurent>()


    var ratingComparator= Comparator<Restaurent> { rest1, rest2 ->

        if(rest1.Restaurentrating.compareTo(rest2.Restaurentrating,true)==0){
            rest1.Restaurentname.compareTo(rest2.Restaurentname,true)
        }
        else{
            rest1.Restaurentrating.compareTo(rest2.Restaurentrating,true)
        }

    }

    var costComparator= Comparator<Restaurent> { rest1, rest2 ->

        rest1.Restaurentcost_for_one.compareTo(rest2.Restaurentcost_for_one,true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerview)
        layoutManager = LinearLayoutManager(activity)
        ProgressBar = view.findViewById(R.id.progressBar)
        ProgressLayout = view.findViewById(R.id.ProgressLayout)
        ProgressLayout.visibility = View.GONE


        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        ProgressLayout.visibility = View.GONE
                        val responseJsonObjectData = it.getJSONObject("data")

                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val restJsonObject = data.getJSONObject(i)
                                val restObject = Restaurent(
                                    restJsonObject.getString("id"),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("rating"),
                                    restJsonObject.getString("cost_for_one"),
                                    restJsonObject.getString("image_url")
                                )

                                RestaurentInfoList.add(restObject)
                                recyclerAdapter = DashboardRecyclerAdapter(
                                    activity as Context,
                                    RestaurentInfoList
                                )

                                recyclerDashboard.adapter = recyclerAdapter

                                recyclerDashboard.layoutManager = layoutManager


                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error Occurred",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            " Some Unexpected Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            " Volley Error Occured ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {

                        val Headers = HashMap<String, String>()
                        Headers["Content-type"] = "application/json"
                        Headers["token"] = "3990e5266ecbe7"
                        return Headers
                    }
                }

            queue.add(jsonObjectRequest)
        }else
        {
            val Dialog = AlertDialog.Builder(activity as Context)
            Dialog.setTitle("Failure")
            Dialog.setMessage("Internet connection Not Found")
            Dialog.setPositiveButton("Open settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            Dialog.setNegativeButton("Exit App"){text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            Dialog.create()
            Dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(
            R.menu.menu_dashboard,
            menu
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item?.itemId

        when(id){

            R.id.action_sort->{
                radioButtonView= View.inflate(activity as Context,R.layout.sort_radio_button,null)//radiobutton view for sorting display
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By?")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(RestaurentInfoList, costComparator)
                            RestaurentInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(RestaurentInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(RestaurentInfoList, ratingComparator)
                            RestaurentInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()//updates the adapter
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
