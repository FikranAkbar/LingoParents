package com.glints.lingoparents.ui.accountsetting

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.accountsetting.changepassword.ChangePasswordFragment
import com.glints.lingoparents.ui.accountsetting.linkedaccount.LinkedAccountFragment
import com.glints.lingoparents.ui.accountsetting.profile.ProfileFragment

class AccountSettingSectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = ProfileFragment()
            1 -> fragment = ChangePasswordFragment()
            2 -> fragment = LinkedAccountFragment()
        }

        return fragment as Fragment
    }
}