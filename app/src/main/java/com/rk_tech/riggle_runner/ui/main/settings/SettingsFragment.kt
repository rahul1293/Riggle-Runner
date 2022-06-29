package com.rk_tech.riggle_runner.ui.main.settings
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.Admin
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.data.model.response_v2.ResultDeliverify
import com.rk_tech.riggle_runner.databinding.BottomSheetCallDeliverifyBinding
import com.rk_tech.riggle_runner.databinding.FragmentSettingsBinding
import com.rk_tech.riggle_runner.databinding.ListCallDeliverifyBinding
import com.rk_tech.riggle_runner.ui.base.*
import com.rk_tech.riggle_runner.ui.login.LoginActivity
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ComboBottomSheet
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.PaymentDetailsActivity
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bottom_sheet_call_deliverify.view.*

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private var sheetAdapter: SimpleRecyclerViewAdapter<ResultDeliverify, ListCallDeliverifyBinding>? =
        null
    val viewModel: SettingsFragmentVM by viewModels()
    private var admins: List<ResultDeliverify>? = null
    var index = 0
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
        showBottomSheet()
        details?.let {
            binding.tvHeroName.text = it.user.full_name
            //binding.tvServiceHub.text = it.user.service_hub.name
            //binding.tvWhereHouse.text = it.user.service_hub.warehouse_address
        }

        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvSettlement -> {
                    val intent = PaymentDetailsActivity.newIntent(requireActivity())
                    startActivity(intent)
                }
                R.id.tvCallDeliverifySolution -> {
                    // ComboBottomSheet()
                    val dialog =
                        BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
                    val view = layoutInflater.inflate(R.layout.bottom_sheet_call_deliverify, null)
                    val bt = view.findViewById<ImageView>(R.id.iv)
                    view.rvContact.adapter = sheetAdapter
                    bt.setOnClickListener {
                        if (index < binding.clMain.childCount) {
                            binding.clMain.removeViewAt(index)
                        }
                        dialog.dismiss()
                    }

                    dialog.setCancelable(false)
                    dialog.setContentView(view)
                    dialog.show()
                    index = binding.clMain.childCount
                    Blurry.with(activity).sampling(1).onto(binding.clMain)
                    //inItBottomSheet()
                }
                R.id.tvLogout -> {
                    val dialog =
                        BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
                    val view = layoutInflater.inflate(R.layout.bs_logout, null)
                    val bt = view.findViewById<TextView>(R.id.tvLogout)
                    bt.setOnClickListener {
                        dialog.dismiss()
                        SharedPrefManager.clear()
                        val intent = LoginActivity.newIntent(requireActivity())
                        startActivity(intent)
                        requireActivity().finishAffinity()
                    }
                    val btcanel = view.findViewById<TextView>(R.id.tvCancel)
                    btcanel.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()

                }
                R.id.tvContactUs -> {
                    val intentDial =
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+919930286991"))
                    requireActivity().startActivity(intentDial)
                }
                R.id.tvCallSp -> {
                    val popup = PopupMenu(requireContext(), it)
                    for (index in lists()) {
                        popup.menu.add(index.name.toString() + " - " + index.no)
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
                    admins = it.data
                    sheetAdapter?.list = admins
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
        viewModel.getServiceHubDetails(getAuthorization())
        /*details?.user?.service_hub?.id?.let {
            viewModel.getServiceHubDetails(getAuthorization(), it.toString())
        }*/
    }

    private fun showBottomSheet() {
       /* val bottomSheet =
            BottomSheet<BottomSheetCallDeliverifyBinding>(R.layout.bottom_sheet_call_deliverify)
*/
        sheetAdapter =
            SimpleRecyclerViewAdapter<ResultDeliverify, ListCallDeliverifyBinding>(
                R.layout.list_call_deliverify, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlMain -> {

                    }

                }
            }
        bottomSheet?.binding?.rvContact?.adapter = sheetAdapter
        //   bottomSheet.show(parentFragmentManager, "")
    }


    private var bottomSheet: BaseCustomBottomSheet<BottomSheetCallDeliverifyBinding>? = null
    private fun inItBottomSheet() {
        bottomSheet =
            BaseCustomBottomSheet(
                requireActivity(), R.layout.bottom_sheet_call_deliverify
            ) {
                when (it.id) {
                    R.id.ivCancel -> {

                    }
                }
            }
        bottomSheet?.binding?.rvContact?.adapter = sheetAdapter
        bottomSheet?.show()
    }

    private fun lists(): ArrayList<DummyData> {
        val list = ArrayList<DummyData>()
        list.add(DummyData("", ""))
        list.add(DummyData("", ""))
        return list
    }
}