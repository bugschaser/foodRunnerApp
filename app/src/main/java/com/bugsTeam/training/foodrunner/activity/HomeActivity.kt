package com.bugsTeam.training.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.*
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bugsTeam.training.foodrunner.KEY_MOBILE
import com.bugsTeam.training.foodrunner.KEY_NAME
import com.bugsTeam.training.foodrunner.R
import com.bugsTeam.training.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

@Suppress("DEPRECATION")
class HomeActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var frame:FrameLayout
    private lateinit var navigationView: NavigationView
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //SetUp an shared Preferences
        sharedPref=getSharedPreferences(getString(R.string.preferences_file_name),Context.MODE_PRIVATE)

        setUpUIWidgets()

        /*This method is also user created to setup the toolbar*/
        setUpToolbar()

        /*User created method to handle the action bar drawer toogle*/
        setUpActionOnToolBar()

        /*This method is created to setting up the header layout*/
        setUpNavigationHeader()

        /*This is method is created to display the home fragment inside the activity by default*/
        displayHome()

        navigationView.setNavigationItemSelectedListener { item:MenuItem ->

            //Unchecking the previous item when new item is clicked
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }

            /*Highlighting the new menu item, the one which is clicked*/
            item.isCheckable=true
            item.isChecked=true

            /*This sets the value of previous menu item as the current one*/
            previousMenuItem=item

            /*The closing of navigation drawer is delayed to make the transition smooth
            * We delay it by 0.1 second*/
            val mPendingRunnable = Runnable { drawerLayout.closeDrawer(GravityCompat.START) }
            Handler().postDelayed(mPendingRunnable, 100)

            /*The fragment transaction takes care of the different fragments which will be opened and closed*/
            val fragmentTransaction=supportFragmentManager.beginTransaction()

            when(item.itemId){
                R.id.home->{
                    val homeFragment = HomeFragment()
                    fragmentTransaction.replace(R.id.frame,homeFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title="All Restaurants"
                }
                R.id.myProfile->{
                    val profileFragment = MyProfileFragment()
                    fragmentTransaction.replace(R.id.frame,profileFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title="My Profile"
                }
                R.id.favourite->{
                    val favouriteFragment=FavouriteFragment()
                    fragmentTransaction.replace(R.id.frame,favouriteFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title="Favourite Restaurants"
                }
                R.id.orderHistory->{
                    val orderHistoryFragment=OrderHistoryFragment()
                    fragmentTransaction.replace(R.id.frame,orderHistoryFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title="My Previous Orders"
                }

                R.id.faqs->{
                    val faqFragment=FAQFragment()
                    fragmentTransaction.replace(R.id.frame,faqFragment)
                    fragmentTransaction.commit()
                    supportActionBar?.title="Frequently Asked Questions"
                }
                R.id.logout->{
                    val dialogBuilder=AlertDialog.Builder(this@HomeActivity)
                    dialogBuilder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes"){_,_->
                            if(sharedPref.edit().clear().commit())
                                ActivityCompat.finishAffinity(this)
                        }
                        .setNegativeButton("No"){_,_->
                            val homeFragment = HomeFragment()
                            fragmentTransaction.replace(R.id.frame,homeFragment)
                            fragmentTransaction.commit()
                            supportActionBar?.title="All Restaurants"
                        }
                        .create()
                        .show()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    /*This is method is created to display the home fragment*/
    fun displayHome(){
        val homeFragment = HomeFragment()
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,homeFragment)
        transaction.commit()
        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    @SuppressLint("SetTextI18n")
    private fun setUpNavigationHeader() {

        //Set Navigation Header
        val header=navigationView.inflateHeaderView(R.layout.drawer_header)
        val headerName=header.findViewById<TextView>(R.id.txtName)
        val headerMobile=header.findViewById<TextView>(R.id.txtMobile)
        headerName.text=sharedPref.getString(KEY_NAME,"Name")
        headerMobile.text=sharedPref.getString(KEY_MOBILE,"+91-1234567890")
    }

    private fun setUpActionOnToolBar() {
        actionBarDrawerToggle= object :
            ActionBarDrawerToggle(this,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)
            {
                override fun onDrawerStateChanged(newState: Int) {
                    super.onDrawerStateChanged(newState)
                    val pendingRunnable= Runnable {
                        val inputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    }
                    Handler().postDelayed(pendingRunnable,50)
                }
            }
        /*Adding the drawer toggle to the drawer layout*/
        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        /*This handles the animation of the hamburger icon when the drawer is opened/closed*/
        actionBarDrawerToggle.syncState()

    }

    private fun setUpUIWidgets() {
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.homeToolbar)
        frame=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Dashboard"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*This is done to open the navigation drawer when the hamburger icon is clicked*/
        if(item.itemId ==android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

}