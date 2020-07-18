package com.priyansh.foodapp.activity.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.priyansh.foodapp.R
import com.priyansh.foodapp.activity.activity.RestaurentMenuActivity
import com.priyansh.foodapp.activity.database.RestaurantDatabase
import com.priyansh.foodapp.activity.database.RestaurantEntity
import com.priyansh.foodapp.activity.model.Restaurent
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context : Context, val itemList: ArrayList<Restaurent>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val RestaurentName: TextView = view.findViewById(R.id.Restname)
        val RestaurentPrice: TextView = view.findViewById(R.id.onePrice)
        val RestaurentRating: TextView = view.findViewById(R.id.Rating)
        val RestaurentImage: ImageView = view.findViewById(R.id.imgRestImage)
        val RestaurentLayout: LinearLayout = view.findViewById(R.id.ListContent)
        val FavButton : ImageButton = view.findViewById(R.id.favbtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_main_fragment_singlerow, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val rest = itemList[position]
        val restaurantEntity = RestaurantEntity(
            rest.Restaurentid,
            rest.Restaurentname
        )
        holder.RestaurentName.text = rest.Restaurentname
        holder.RestaurentPrice.text = "Rs." + rest.Restaurentcost_for_one + "/Person"
        holder.RestaurentRating.text = rest.Restaurentrating
        Picasso.get().load(rest.RestaurentImage).error(R.drawable.icon)
            .into(holder.RestaurentImage)
        if (!DBAsynTask(context, restaurantEntity, 1).execute().get())
        {
            holder.FavButton.setImageResource(R.drawable.normal)
        }
        else
        {
            holder.FavButton.setImageResource(R.drawable.favourite)
        }



        holder.RestaurentLayout.setOnClickListener {
            Toast.makeText(context, "Opening ${holder.RestaurentName.text} Menu", Toast.LENGTH_LONG)
                .show()

            val intent = Intent(context, RestaurentMenuActivity::class.java)

            intent.putExtra("restaurantId", rest.Restaurentid)

            intent.putExtra("restaurantName", holder.RestaurentName.text.toString())


            context.startActivity(intent)
        }

        holder.FavButton.setOnClickListener(View.OnClickListener {
            if (!DBAsynTask(context, restaurantEntity, 1).execute().get()) {

                val result = DBAsynTask(context, restaurantEntity, 2).execute().get()

                if (result) {

                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()

                    holder.FavButton.setImageResource(R.drawable.favourite)

                } else {

                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()

                }

            } else {

                val result = DBAsynTask(context, restaurantEntity, 3).execute().get()

                if (result) {

                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()

                    holder.FavButton.setImageResource(R.drawable.normal)
                } else {

                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()

                }

            }


        })


    }

    class DBAsynTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {

            /*
            * Mode 1->check if restaurant is in favourites
            * Mode 2->Save the restaurant into DB as favourites
            * Mode 3-> Remove the favourite restaurant*/


            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false

            }

        }

    }

}