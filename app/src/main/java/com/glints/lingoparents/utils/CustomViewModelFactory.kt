package com.glints.lingoparents.utils

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.glints.lingoparents.ui.accountsetting.AccountSettingViewModel
import com.glints.lingoparents.ui.insight.InsightListViewModel
import com.glints.lingoparents.ui.course.AllCoursesViewModel
import com.glints.lingoparents.ui.liveevent.LiveEventListViewModel
import com.glints.lingoparents.ui.liveevent.detail.LiveEventDetailViewModel
import com.glints.lingoparents.ui.login.LoginViewModel
import com.glints.lingoparents.ui.register.RegisterViewModel
import com.glints.lingoparents.ui.splash.SplashViewModel

class CustomViewModelFactory(
    private val tokenPref: TokenPreferences,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    private val eventId: Int? = null,
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
            modelClass.isAssignableFrom(AccountSettingViewModel::class.java) -> {
                AccountSettingViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(LiveEventListViewModel::class.java) -> {
                LiveEventListViewModel(tokenPref) as T
            }
            modelClass.isAssignableFrom(LiveEventDetailViewModel::class.java) -> {
                LiveEventDetailViewModel(tokenPref, eventId as Int) as T
            }
            modelClass.isAssignableFrom(InsightListViewModel::class.java) -> {
                InsightListViewModel(tokenPref) as  T
            }
            modelClass.isAssignableFrom(AllCoursesViewModel::class.java) -> {
                AllCoursesViewModel(tokenPref) as T
            }
            else -> throw Throwable("Unknown ViewModel class: " + modelClass.name)
        }
    }

}