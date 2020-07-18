package com.priyansh.foodapp.activity.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.priyansh.foodapp.R
import com.priyansh.foodapp.activity.activity.CartActivity
import com.priyansh.foodapp.activity.model.RestaurentMenu

class RestaurantMenuAdapter(val context: Context, val restaurantId:String, val restaurantName:String, val proceedToCartPassed: RelativeLayout, val buttonProceedToCart: Button, val restaurantMenu:ArrayList<RestaurentMenu>):
    RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {

    var itemSelectedCount:Int=0
    lateinit var proceedToCart:RelativeLayout


    var itemsSelectedId= arrayListOf<String>()

    class ViewHolderRestaurantMenu(view: View):RecyclerView.ViewHolder(view){
        val textViewSerialNumber:TextView=view.findViewById(R.id.textViewSerialNumber)
        val textViewItemName: TextView =view.findViewById(R.id.textViewItemName)
        val textViewItemPrice:TextView=view.findViewById(R.id.textViewItemPrice)
        val buttonAddToCart:Button=view.findViewById(R.id.buttonAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.restaurant_menu_recycler_view_single_row,parent,false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        restaurantMenu.size
        return restaurantMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {
        val restaurantMenuItem=restaurantMenu[position]

        proceedToCart=proceedToCartPassed//button view passed from the RestaurantMenuActivity

        //click listener to the button view Passed from activity which has the button proceed to cart
        buttonProceedToCart.setOnClickListener(View.OnClickListener {

            val intent= Intent(context, CartActivity::class.java)

            intent.putExtra("restaurantId",restaurantId.toString())// pass the restaurant id to the next acticity

            intent.putExtra("restaurantName",restaurantName)

            intent.putExtra("selectedItemsId",itemsSelectedId)//pass all the items selected by the user

            context.startActivity(intent)

        })


        holder.buttonAddToCart.setOnClickListener(View.OnClickListener {

            if(holder.buttonAddToCart.text.toString().equals("Remove"))
            {
                itemSelectedCount--//unselected

                itemsSelectedId.remove(holder.buttonAddToCart.getTag().toString())

                holder.buttonAddToCart.text="Add"

                holder.buttonAddToCart.setBackgroundColor(Color.rgb(98,0,238))

            }
            else
            {
                itemSelectedCount++//selected

                itemsSelectedId.add(holder.buttonAddToCart.getTag().toString())


                holder.buttonAddToCart.text="Remove"

                holder.buttonAddToCart.setBackgroundColor(Color.rgb(255,196,0))//yellow colour to rgb

            }

            if(itemSelectedCount>0){
                proceedToCart.visibility=View.VISIBLE
            }
            else{
                proceedToCart.visibility=View.INVISIBLE
            }

        })

        holder.buttonAddToCart.setTag(restaurantMenuItem.Restaurentid+"")//save the item id in textViewName Tag ,will be used to add to cart
        holder.textViewSerialNumber.text=(position+1).toString()//position starts from 0
        holder.textViewItemName.text=restaurantMenuItem.Restaurentname
        holder.textViewItemPrice.text="Rs."+restaurantMenuItem.cost_for_one

    }

    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }

}