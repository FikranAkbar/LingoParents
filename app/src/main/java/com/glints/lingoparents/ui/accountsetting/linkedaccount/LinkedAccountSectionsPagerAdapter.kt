package com.glints.lingoparents.ui.accountsetting.linkedaccount

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.accountsetting.linkedaccount.category.LinkedAccountListFragment

class LinkedAccountSectionsPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = LinkedAccountListFragment()
            1 -> fragment = LinkedAccountListFragment()
        }

        return fragment as Fragment
    }

}