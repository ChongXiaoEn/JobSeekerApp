package com.example.jobseeker.Activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.jobseeker.Fragments.*
import com.example.jobseeker.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class UserMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null

    private lateinit var auth: FirebaseAuth

    private var menuFlag = 1

    private var allowSave = false

    private val PROFILE_FRAG_TAG = "profile_frag"
    private val HOME_FRAG_TAG = "home_frag"
    private val SAVED_JOB_FRAG_TAG = "saved_job_frag"
    private val SETTINGS_FRAG = "settings_frag"
    private val APPLICATION_FRAG = "applications_frag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        val name = user!!.displayName
        val email = user.email
        val photoUri = user.photoUrl

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorGray)))

        drawer = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()

        supportFragmentManager.beginTransaction().replace(
            R.id.menuFragContainer,
            HomeFragment()
        ).commit()

        title = "Home"

        navigationView.setCheckedItem(R.id.nav_home)

        val headerView: View = navigationView.getHeaderView(0)

        val navUsername: TextView = headerView.findViewById(R.id.txtUserName)

        val navUserEmail: TextView = headerView.findViewById(R.id.txtUserEmail)

        val navUserPhoto: ImageView = headerView.findViewById(R.id.user_icon)

        navUsername.text = name

        navUserEmail.text = email

        Picasso.get().load(photoUri).into(navUserPhoto)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.nav_home -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.menuFragContainer,
                    HomeFragment(), HOME_FRAG_TAG
                ).commit()
                title = "Home"

                menuFlag = 1
                invalidateOptionsMenu()
            }

            R.id.nav_save -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.menuFragContainer,
                    SavedJobsFragment(), SAVED_JOB_FRAG_TAG
                ).commit()
                title = "Saved Jobs"

                menuFlag = 2
                invalidateOptionsMenu()
            }

            R.id.nav_profile -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.menuFragContainer,
                    ProfileFragment(), PROFILE_FRAG_TAG
                ).commit()
                title = "Profile"

                menuFlag = 3

                invalidateOptionsMenu()
            }

            R.id.nav_settings -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.menuFragContainer,
                    SettingsFragment(), SETTINGS_FRAG
                ).commit()
                title = "Settings"

                menuFlag = 4
                invalidateOptionsMenu()
            }

            R.id.nav_applications -> {
                supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.menuFragContainer,
                        AppliedJobsFragment(), APPLICATION_FRAG
                    )
                    .commit()

                title = "Applications"

                menuFlag = 5
                invalidateOptionsMenu()
            }


        }

        drawer!!.closeDrawer(GravityCompat.START)

        return true

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater

        if (menuFlag == 3) {


            inflater.inflate(R.menu.edit_menu, menu)

        }

        if (menuFlag == 3) {

            val saveBtn = menu!!.findItem(R.id.saveBtn)

            val editBtn = menu!!.findItem(R.id.editBtn)

            if (allowSave) {

                saveBtn.isVisible = true
                editBtn.isVisible = false

            } else {

                saveBtn.isVisible = false
                editBtn.isVisible = true

            }
        }


        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.editBtn -> {

                var currentFragment =
                    supportFragmentManager.findFragmentByTag("profile_frag") as ProfileFragment

                currentFragment.enableInfoEdit()

                invalidateOptionsMenu()

                allowSave = true

            }

            R.id.saveBtn -> {

                var currentFragment =
                    supportFragmentManager.findFragmentByTag("profile_frag") as ProfileFragment

                currentFragment.saveInfo()

                invalidateOptionsMenu()

                allowSave = false

            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {

            drawer!!.closeDrawer(GravityCompat.START)

        } else {

            super.onBackPressed()

        }

    }

}