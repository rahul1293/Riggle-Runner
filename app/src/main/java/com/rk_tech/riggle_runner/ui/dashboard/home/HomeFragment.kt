package com.rk_tech.riggle_runner.ui.dashboard.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.databinding.AvailPopupDialogBinding
import com.rk_tech.riggle_runner.databinding.FragmentHomeBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    val viewModel: HomeFragmentVM by viewModels()

    companion object {
        fun newInstance(): Fragment {
            return HomeFragment()
        }

        const val TAG = "HomeFragment"
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_home
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        binding.header.type = 1
        viewModel.onClick.observe(viewLifecycleOwner, Observer {
            when (it?.id) {
                R.id.tabOne -> {
                    binding.viewPagerHome.currentItem = 0
                }
                R.id.tabTwo -> {
                    binding.viewPagerHome.currentItem = 1
                }
                R.id.tabThree -> {
                    binding.viewPagerHome.currentItem = 2
                }
                R.id.llDrop -> {
                    showAvailDropDown(it)
                }
                R.id.ivNotification -> {

                }
            }
        })

        setUpViewPager()
    }

    private fun setUpViewPager() {
        val pagerAdapter = StatePagerHomeAdapter(this)
        binding.viewPagerHome.adapter = pagerAdapter
        binding.viewPagerHome.isUserInputEnabled = false
        updateView(0)
        binding.viewPagerHome.offscreenPageLimit = 1
        binding.viewPagerHome.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateView(position)
            }
        })
    }

    private fun updateView(position: Int) {
        //binding.tvPendingCount.text = PendingFragment.count.toString()
        binding.type = position
        //binding.count = PendingFragment.count
    }

    private fun showAvailDropDown(it: View?) {
        val layoutInflater =
            requireActivity().getSystemService(AppCompatActivity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val bindingView: AvailPopupDialogBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.avail_popup_dialog, binding.clMain, false
        )
        val layout: View = bindingView.root
        // Creating the PopupWindow
        val changeSortPopUp = PopupWindow(requireActivity())
        changeSortPopUp.contentView = layout
        changeSortPopUp.width = LinearLayout.LayoutParams.WRAP_CONTENT
        changeSortPopUp.height = LinearLayout.LayoutParams.WRAP_CONTENT
        changeSortPopUp.isFocusable = true
        val offSetX = -15
        val offSetY = 0
        changeSortPopUp.setBackgroundDrawable(
            BitmapDrawable(
                requireActivity().resources,
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            )
        )
        changeSortPopUp.showAsDropDown(it, offSetX, offSetY)

        bindingView.tvAvailable.setOnClickListener {
            binding.header.tvStatusType.text = getString(R.string.open)
            binding.header.tvStatusType.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_5CAC7D
                )
            )
            changeSortPopUp.dismiss()
            /*if (HomeActivity.obrStatus.value == false)
                viewModel.updateOnlineStatus(
                    getAuthorization(),
                    OnlineStatus(true)
                )*/
        }

        bindingView.tvOffline.setOnClickListener {
            binding.header.tvStatusType.text = getString(R.string.close)
            binding.header.tvStatusType.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_C6345C
                )
            )
            changeSortPopUp.dismiss()
            /*if (HomeActivity.inProgress)
                binding.header.tvStatusType.text = getString(R.string.available)
            if (HomeActivity.obrStatus.value == true || !HomeActivity.inProgress)
                viewModel.updateOnlineStatus(
                    getAuthorization(),
                    OnlineStatus(false)
                )*/
        }

        bindingView.tvBusy.setOnClickListener {
            binding.header.tvStatusType.text = getString(R.string.busy)
            binding.header.tvStatusType.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.color_D9AE56
                )
            )
            changeSortPopUp.dismiss()
        }
    }

}