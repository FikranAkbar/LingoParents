package com.glints.lingoparents.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener{

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_dashboard_fragment)

        binding.apply {
            bottomNavigationView.setupWithNavController(navController)
            header.tToolbar.setOnMenuItemClickListener(this@DashboardActivity)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.user_profile -> {
                if (navController.currentDestination?.id != R.id.accountSettingFragment){
                    navController.navigate(R.id.action_global_accountSettingFragment)
                }
                false
            }
            else -> {
                false
            }
        }
    }

    fun showBottomNav(bool: Boolean) {
        when(bool) {
            true -> {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
            else -> {
                binding.bottomNavigationView.visibility = View.GONE
            }

        }
    }
}