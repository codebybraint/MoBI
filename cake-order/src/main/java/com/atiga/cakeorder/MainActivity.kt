package com.atiga.cakeorder

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.atiga.cakeorder.core.domain.enum.UserRole
import com.atiga.cakeorder.databinding.ActivityMainBinding
import com.atiga.cakeorder.databinding.NavHeaderMainBinding
import com.atiga.cakeorder.ui.login.LoginActivity
import com.atiga.cakeorder.util.SessionManager
import com.atiga.cakeorder.util.SessionManager.Companion.ROLE_ID
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding: NavHeaderMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navHeaderBinding = NavHeaderMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        sessionManager = SessionManager(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_order, R.id.nav_report, R.id.nav_admin, R.id.nav_capacity, R.id.nav_user), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.itemIconTintList = null

        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            sessionManager.logout()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
            true
        }

        // set username on nav header
        val username = sessionManager.getFromPreference(SessionManager.KEY_USERNAME)
        val headerView = navView.getHeaderView(0)
        val tvUsername = headerView.findViewById<TextView>(R.id.tv_header_username)
        tvUsername.text = username

        // hide/show master data nav drawer menu
        navView.menu.findItem(R.id.nav_admin).isVisible = UserRole.SUPER_ADMIN.id == sessionManager.getFromPreference(ROLE_ID)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}