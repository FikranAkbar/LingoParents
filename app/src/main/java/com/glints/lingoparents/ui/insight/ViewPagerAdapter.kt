package com.glints.lingoparents.ui.insight

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.insight.category.AllInsightFragment
import com.glints.lingoparents.ui.insight.category.LifestyleInsightFragment
import com.glints.lingoparents.ui.insight.category.ParentingInsightFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragmentArray: Array<Fragment> = arrayOf(
        AllInsightFragment(),
        ParentingInsightFragment(),
        LifestyleInsightFragment()
    )

    override fun getItemCount(): Int {
        return fragmentArray.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentArray[position]
    }
}