package com.glints.lingoparents.utils

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.glints.lingoparents.ui.accountsetting.AccountSettingViewModel
import com.glints.lingoparents.ui.dashboard.DashboardViewModel
import com.glints.lingoparents.ui.accountsetting.AccountSettingFragment
import com.glints.lingoparents.ui.accountsetting.changepassword.PasswordSettingViewModel
import com.glints.lingoparents.ui.accountsetting.profile.ProfileViewModel
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.ui.course.AllCoursesViewModel
import com.glints.lingoparents.ui.home.HomeViewModel
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.ui.liveevent.category.CompletedLiveEventViewModel
import com.glints.lingoparents.ui.liveevent.category.TodayLiveEventViewModel
import com.glints.lingoparents.ui.liveevent.category.UpcomingLiveEventViewModel
import com.glints.lingoparents.ui.liveevent.detail.LiveEventDetailViewModel
import com.glints.lingoparents.ui.login.LoginViewModel
import com.glints.lingoparents.ui.register.RegisterViewModel
import com.glints.lingoparents.ui.splash.SplashViewModel

class CustomViewModelFactory(
    private val tokenPref: TokenPreferences,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    private val eventId: Int? = null,
    private val accountId: Int? = null,
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when {
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(LiveEventListViewModel::class.java) -> {
                LiveEventListViewModel() as T
            }
            modelClass.isAssignableFrom(TodayLiveEventViewModel::class.java) -> {
                TodayLiveEventViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(UpcomingLiveEventViewModel::class.java) -> {
                UpcomingLiveEventViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(CompletedLiveEventViewModel::class.java) -> {
                CompletedLiveEventViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(LiveEventDetailViewModel::class.java) -> {
                LiveEventDetailViewModel(tokenPref, eventId as Int) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(PasswordSettingViewModel::class.java) -> {
                PasswordSettingViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(InsightListViewModel::class.java) -> {
                InsightListViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(AllCoursesViewModel::class.java) -> {
                AllCoursesViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(tokenPref) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

}