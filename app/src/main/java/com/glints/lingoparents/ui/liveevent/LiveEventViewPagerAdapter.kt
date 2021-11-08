package com.glints.lingoparents.ui.liveevent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.glints.lingoparents.ui.liveevent.category.CompletedEventFragment
import com.glints.lingoparents.ui.liveevent.category.TodayEventFragment
import com.glints.lingoparents.ui.liveevent.category.UpcomingEventFragment

class LiveEventViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    private val fragmentArray: Array<Fragment> = arrayOf(
        TodayEventFragment(),
        UpcomingEventFragment(),
        CompletedEventFragment()
    )

    override fun getItemCount(): Int = fragmentArray.size

    override fun createFragment(position: Int): Fragment = fragmentArray[position]
}