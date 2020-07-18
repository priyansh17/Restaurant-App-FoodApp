package com.priyansh.foodapp.activity.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.priyansh.foodapp.activity.fragment.*
import com.priyansh.foodapp.R

class MainActivity : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuitem : MenuItem? = null
    lateinit var sharedPreferencess: SharedPreferences
    lateinit var textViewcurrentUser : TextView
    lateinit var textViewMobileNumber : TextView


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerlayout)
        coordinatorLayout = findViewById(R.id.coordinatorlayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.framelayout)
        navigationView = findViewById(R.id.navigationview)
        val headerView=navigationView.getHeaderView(0)
        textViewcurrentUser=headerView.findViewById(R.id.TextForName)
        textViewMobileNumber=headerView.findViewById(R.id.textForNumber)


        setUpToolbar()


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open,
            R.string.close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportFragmentManager.beginTransaction().replace(
            R.id.framelayout,
            HomeFragment()
        ).commit()
        supportActionBar?.title= "Foodies Nation"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.mainhome)



        textViewcurrentUser.text=sharedPreferencess.getString("name","")
        textViewMobileNumber.text="+91-"+sharedPreferencess.getString("mobile_number","")


        navigationView.setNavigationItemSelectedListener {

            if (previousMenuitem != null) {
                previousMenuitem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuitem = it

            when (it.itemId) {
                R.id.mainhome -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        HomeFragment()
                    ).commit()
                    supportActionBar?.title= "Foodies Nation"
                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.mainhome)
                }


                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        UserFragment(this)
                    ).commit()
                    supportActionBar?.title= "User Profile"
                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.profile)
                }

                R.id.Favourites -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        FavouritesFragment()
                    ).commit()
                    supportActionBar?.title= "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.Favourites)
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.framelayout,
                        faqsFragment()
                    ).commit()
                    supportActionBar?.title= "FAQs"
                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.faq)
                }

                R.id.OrderHistory -> {
                    val intent= Intent(this, OrderHistoryActivity::class.java)
                    drawerLayout.closeDrawers()
                    startActivity(intent)
                }


                R.id.logout -> {
                    val Dialog = AlertDialog.Builder(this@MainActivity)
                    Dialog.setTitle("Confirmation")
                    Dialog.setMessage("Are you sure you want to exit?")
                    Dialog.setPositiveButton("Yes"){text, listener ->
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                        sharedPreferencess.edit().clear().apply()
                        finish()

                        ///Piece of code of shared preferences yet to be added
                    }
                    Dialog.setNegativeButton("No"){text, listener ->
                        supportFragmentManager.beginTransaction().replace(
                            R.id.framelayout,
                            HomeFragment()
                        ).commit()
                        supportActionBar?.title= "Foodies Nation"
                        drawerLayout.closeDrawers()
                        navigationView.setCheckedItem(R.id.mainhome)

                    }
                    Dialog.create()
                    Dialog.show()

                    drawerLayout.closeDrawers()
                    navigationView.setCheckedItem(R.id.logout)
                }

            }

            return@setNavigationItemSelectedListener true
        }



    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title= "Foodies Nation"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id== android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }


    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.framelayout)

        when(frag)
        {
            !is HomeFragment -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.framelayout,
                    HomeFragment()
                ).commit()
                supportActionBar?.title="Foodies Nation"
                drawerLayout.closeDrawers()
                navigationView.setCheckedItem(R.id.mainhome)
            }

            else -> super.onBackPressed()
        }
    }

}
