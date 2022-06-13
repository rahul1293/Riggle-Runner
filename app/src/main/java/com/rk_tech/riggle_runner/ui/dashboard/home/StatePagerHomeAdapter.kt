package com.rk_tech.riggle_runner.ui.dashboard.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rk_tech.riggle_runner.ui.dashboard.home.waiting.WaitingFragment


class StatePagerHomeAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = WaitingFragment()
            }
            1 -> {
                fragment = WaitingFragment()
            }
            2 -> {
                fragment = WaitingFragment()
            }
        }
        return fragment!!
    }

}