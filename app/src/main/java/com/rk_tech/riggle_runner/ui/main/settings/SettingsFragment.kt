package com.rk_tech.riggle_runner.ui.main.settings

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.Admin
import com.rk_tech.riggle_runner.databinding.FragmentSettingsBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.login.LoginActivity
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.PaymentDetailsActivity
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    val viewModel: SettingsFragmentVM by viewModels()
    private var admins: List<Admin>? = null

    companion object {
        fun newInstance(): Fragment {
            return SettingsFragment()
        }

        val TAG = "SettingsFragment"
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_settings
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

        details?.let {
            binding.tvHeroName.text = it.user.full_name
            binding.tvServiceHub.text = it.user.service_hub.name
            binding.tvWhereHouse.text = it.user.service_hub.warehouse_address
        }

        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvSettlement -> {
                    val intent = PaymentDetailsActivity.newIntent(requireActivity())
                    startActivity(intent)
                }
                R.id.tvLogout -> {
                    SharedPrefManager.clear()
                    val intent = LoginActivity.newIntent(requireActivity())
                    startActivity(intent)
                    requireActivity().finishAffinity()
                }
                R.id.tvContactUs -> {
                    val intentDial =
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919930286991"))
                    requireActivity().startActivity(intentDial)
                }
                R.id.tvCallSp -> {
                    admins?.let { hubs ->
                        val popup = PopupMenu(requireContext(), it)
                        for (index in hubs) {
                            popup.menu.add(index.first_name.toString() + " - " + index.mobile)
                        }
                        popup.setOnMenuItemClickListener { menuItem ->
                            val strs = menuItem.title.toString().split(" - ").toTypedArray()
                            if (strs.isNotEmpty() && strs.size > 1) {
                                val intentDial =
                                    Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + strs[1]))
                                requireActivity().startActivity(intentDial)
                            }
                            true
                        }
                        popup.show()
                    }
                }
            }
        }

        viewModel.obrDetails.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    admins = it.data?.admins
                    showHideLoader(false)
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        details?.user?.service_hub?.id?.let {
            viewModel.getServiceHubDetails(getAuthorization(), it.toString())
        }
    }
}