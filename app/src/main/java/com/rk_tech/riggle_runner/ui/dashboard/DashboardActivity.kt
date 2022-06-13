package com.rk_tech.riggle_runner.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.databinding.ActivityDashboardBinding
import com.rk_tech.riggle_runner.databinding.HolderSideMenuBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.dashboard.home.HomeFragment
import com.rk_tech.riggle_runner.utils.BackStackManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    val viewModel: DashboardActivityVM by viewModels()

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_dashboard
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        initDrawer()
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    openDrawer()
                }
                R.id.ivCloseOne -> {
                    closeDrawer()
                }
            }
        }
        changeFragment(HomeFragment.TAG)
        setUpRecyclerView()
    }

    private var menuBean: MenuBean? = null
    private var menuAdpter: SimpleRecyclerViewAdapter<MenuBean, HolderSideMenuBinding>? = null
    private var menuList = ArrayList<MenuBean>()

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpRecyclerView() {
        menuAdpter = SimpleRecyclerViewAdapter(
            R.layout.holder_side_menu,
            BR.bean
        ) { v, m, pos ->
            for (index in menuList.withIndex()) {
                menuList[index.index].check = m.id == menuList[index.index].id
            }
            menuAdpter?.notifyDataSetChanged()
            menuBean = m
            /*when (m?.id) {
                1 -> {
                    changeFragment(HomeFragment.TAG)
                }
                2 -> {
                    changeFragment(SettingsFragment.TAG)
                }
                3 -> {
                    changeFragment(HelpActivity.TAG)
                }
                4 -> {
                    changeFragment(AboutUsActivity.TAG)
                }
            }*/
            closeDrawer()
        }
        val layoutManager = LinearLayoutManager(this)
        binding.rvMenuList.layoutManager = layoutManager
        binding.rvMenuList.adapter = menuAdpter
        menuList = getMenuList() as ArrayList<MenuBean>
        menuAdpter?.list = menuList
    }

    private fun getMenuList(): MutableList<MenuBean> {
        val dataList = ArrayList<MenuBean>()
        dataList.add(MenuBean(1, "Home", R.drawable.ic_home_icon, true))
        dataList.add(MenuBean(2, "Settings", R.drawable.ic_settings_icon, false))
        dataList.add(MenuBean(3, "Get Help", R.drawable.ic_get_help_icon, false))
        dataList.add(MenuBean(4, "About Us", R.drawable.ic_icon_about_us, false))
        return dataList
    }

    private fun openDrawer() {
        binding.drawer.open()
    }

    private fun closeDrawer() {
        binding.drawer.close()
    }

    private fun initDrawer() {
        binding.drawer.drawerElevation = 0f
        binding.drawer.setScrimColor(Color.TRANSPARENT)
        binding.nvOne.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.nvOne.viewTreeObserver.removeOnGlobalLayoutListener(this)
                //  binding.vAlpha.translationX = binding.nvOne.width.toFloat()-200;
            }
        })
        binding.drawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                Log.e("Home", "offset$slideOffset")
                val min = 0.80f
                val max = 1.0f
                val scaleFactor = max - (max - min) * slideOffset
                binding.mainContainer.scaleY = scaleFactor
                binding.mainContainer.scaleX = scaleFactor

                binding.mainContainer.radius = (slideOffset * 80)
                binding.vAlpha.radius = (slideOffset * 80)
                //    binding.mainContainer. = (slideOffset * 100)
                val slideX = drawerView.width * slideOffset
                binding.mainContainer.translationX = slideX
                binding.container.alpha = (1 - slideOffset)
                //binding.mainContainer.alpha = (1 - slideOffset)

                binding.ivBack.alpha = (1 - slideOffset)

                if (slideOffset == 1.0f) {
                    binding.ivCloseOne.animate().alpha(1f)
                    binding.ivCloseOne.visibility = View.VISIBLE
                } else {
                    binding.ivCloseOne.alpha = 0f
                    binding.ivCloseOne.visibility = View.GONE
                }

                //for alpha view
                val min1 = 0.72f
                val max1 = 1.0f
                val scaleFactor1 = max1 - (max1 - min1) * slideOffset
                binding.vAlpha.alpha = slideOffset * 0.5f
                //binding.vAlpha.alpha =  (1 - slideOffset)
                binding.vAlpha.scaleY = scaleFactor1
                binding.vAlpha.scaleX = scaleFactor1
                binding.vAlpha.radius = 80f
                binding.vAlpha.translationX = slideX - 100
            }

            override fun onDrawerOpened(drawerView: View) {
                menuBean = null
            }

            override fun onDrawerClosed(drawerView: View) {
                when (menuBean?.id) {
                    1 -> {
                        changeFragment(HomeFragment.TAG)
                    }
                    2 -> {

                    }
                    3 -> {

                    }
                    4 -> {

                    }
                }
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })
    }

    private fun changeFragment(tab: String?) {
        when (tab ?: HomeFragment.TAG) {
            HomeFragment.TAG -> {
                setHeader(0)
                BackStackManager.getInstance(this).pushFragments(
                    R.id.container,
                    HomeFragment.TAG,
                    HomeFragment.newInstance(),
                    tab != null
                )
            }
        }
    }

    private fun setHeader(type: Int) {
        binding.type = type
    }

    fun addSubFragment(tab: String, fragment: Fragment?) {
        setHeader(2)
        BackStackManager.getInstance(this).pushSubFragments(R.id.container, tab, fragment, true)
    }



}