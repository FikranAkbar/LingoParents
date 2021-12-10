package com.glints.lingoparents.ui.progress

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.progress.learning.ProgressLearningFragment
import com.glints.lingoparents.ui.progress.profile.ProgressProfileFragment

class ProgressSectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position) {
            0 -> fragment = ProgressProfileFragment()
            1 -> fragment = ProgressLearningFragment()
        }

        return fragment as Fragment
    }
}