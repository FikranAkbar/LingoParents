package com.glints.lingoparents.ui.progress

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.accountsetting.changepassword.ChangePasswordFragment

class ProgressSectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = ProgressProfileFragment()
            1 -> fragment = ChangePasswordFragment()
        }

        return fragment as Fragment
    }
}