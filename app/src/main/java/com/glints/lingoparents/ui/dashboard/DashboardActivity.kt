package com.glints.lingoparents.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.glints.lingoparents.R
import com.glints.lingoparents.data.api.interceptors.TokenAuthenticationInterceptor
import com.glints.lingoparents.databinding.ActivityDashboardBinding
import com.glints.lingoparents.ui.authentication.AuthenticationActivity
import com.glints.lingoparents.utils.CustomViewModelFactory
import com.glints.lingoparents.utils.NoInternetAccessOrErrorListener
import com.glints.lingoparents.utils.TokenPreferences
import com.glints.lingoparents.utils.dataStore
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class DashboardActivity : AppCompatActivity(), Toolbar.OnMenuItemClickListener,
    NoInternetAccessOrErrorListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var navController: NavController

    private lateinit var tokenPreferences: TokenPreferences
    private lateinit var viewModel: DashboardViewModel

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenPreferences = TokenPreferences.getInstance(this.dataStore)
        viewModel = ViewModelProvider(this, CustomViewModelFactory(tokenPreferences, this))[
                DashboardViewModel::class.java
        ]

        navController = findNavController(R.id.nav_host_dashboard_fragment)

        binding.apply {
            bottomNavigationView.setupWithNavController(navController)
            header.tToolbar.setOnMenuItemClickListener(this@DashboardActivity)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.dashboardEvent.collect { event ->
                when (event) {
                    is DashboardViewModel.DashboardEvent.HandleRefreshTokenExpired -> {
                        viewModel.logoutUser()
                    }
                    is DashboardViewModel.DashboardEvent.Loading -> {
                        showLoading(true)
                    }
                    is DashboardViewModel.DashboardEvent.Success -> {
                        showLoading(false)
                        viewModel.resetToken()

                        val intent =
                            Intent(this@DashboardActivity, AuthenticationActivity::class.java)
                        intent.putExtra("flag", TOKEN_EXPIRED_FLAG)
                        startActivity(intent)
                        finish()
                    }
                    is DashboardViewModel.DashboardEvent.Failed -> {
                        showLoading(false)
                    }
                    is DashboardViewModel.DashboardEvent.NoInternetAccessOrSomethingError -> {
                        showSnackbar(event.message)
                    }
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(resources.getColor(R.color.error_color, null))
                .setTextColor(Color.parseColor("#FFFFFF"))
                .show()
        } else {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .show()
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.user_profile -> {
                if (navController.currentDestination?.id != R.id.accountSettingFragment) {
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
        when (bool) {
            true -> {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
            else -> {
                binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    @Subscribe
    fun onRefreshTokenExpiredEvent(event: TokenAuthenticationInterceptor.TokenAuthenticationEvent.RefreshTokenExpiredEvent) {
        viewModel.onRefreshTokenExpired()
    }

    private fun showLoading(bool: Boolean) {
        binding.apply {
            vLoadingBackground.isVisible = bool
            vLoadingProgress.isVisible = bool
        }
    }

    override fun onNoInternetAccessOrError(errorMessage: String) {
        viewModel.onNoInternetAccessOrError(errorMessage)
    }
}

const val TOKEN_EXPIRED_FLAG = Activity.RESULT_FIRST_USER + 10