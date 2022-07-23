package com.rk_tech.riggle_runner.ui.main.settings

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.data.model.response_v2.ResultDeliverify
import com.rk_tech.riggle_runner.databinding.BottomSheetCallDeliverifyBinding
import com.rk_tech.riggle_runner.databinding.FragmentSettingsBinding
import com.rk_tech.riggle_runner.databinding.ListCallDeliverifyBinding
import com.rk_tech.riggle_runner.ui.base.*
import com.rk_tech.riggle_runner.ui.login.LoginActivity
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.PaymentDetailsActivity
import com.rk_tech.riggle_runner.utils.Constants
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bottom_sheet_call_deliverify.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    private var sheetAdapter: SimpleRecyclerViewAdapter<ResultDeliverify, ListCallDeliverifyBinding>? =
        null
    val viewModel: SettingsFragmentVM by viewModels()
    private var admins: List<ResultDeliverify>? = null
    var index = 0
    private var count = 0

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
                        if (index < binding.clMain.childCount) {
                            binding.clMain.removeViewAt(index)
                        }
                        dialog.dismiss()
                        SharedPrefManager.clear()
                        val intent = LoginActivity.newIntent(requireActivity())
                        startActivity(intent)
                        requireActivity().finishAffinity()
                    }
                    val btCancel = view.findViewById<TextView>(R.id.tvCancel)
                    btCancel.setOnClickListener {
                        if (index < binding.clMain.childCount) {
                            binding.clMain.removeViewAt(index)
                        }
                        dialog.dismiss()
                    }
                    val btClose = view.findViewById<CardView>(R.id.card)
                    btClose.setOnClickListener {
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
                R.id.tvToday -> {
                    showDatePicker()
                }
                R.id.ivPrevious -> {
                    count--
                    cal.timeInMillis = today.timeInMillis + (count * 24 * 60 * 60 * 1000)
                    updateDate(cal)
                    getDashBoardData(cal.time)
                }
                R.id.ivNext -> {
                    if (cal.time.equals(today.time))
                        return@observe
                    count++
                    cal.timeInMillis = today.timeInMillis + (count * 24 * 60 * 60 * 1000)
                    updateDate(cal)
                    getDashBoardData(cal.time)
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

        viewModel.obrDashboard.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        binding.bean = data
                    }
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.getServiceHubDetails(getAuthorization())
    }

    private fun showBottomSheet() {
        sheetAdapter =
            SimpleRecyclerViewAdapter<ResultDeliverify, ListCallDeliverifyBinding>(
                R.layout.list_call_deliverify, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.tvPhone -> {
                        val intentDial =
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:+91" + m.mobile))
                        requireActivity().startActivity(intentDial)
                    }
                }
            }
    }

    private fun lists(): ArrayList<DummyData> {
        val list = ArrayList<DummyData>()
        list.add(DummyData("", ""))
        list.add(DummyData("", ""))
        return list
    }


    private fun showDatePicker() {
        var datePicker = DatePickerDialog(
            requireActivity(), dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        /*datePicker.datePicker.setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 1000)
        datePicker.datePicker.setMaxDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 7 - 1000)*/
        //datePicker.datePicker.maxDate = calendarMax.timeInMillis
        datePicker.show()

    }

    private var cal = Calendar.getInstance()
    private var today = Calendar.getInstance()
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd-MMM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.tvToday.text = sdf.format(cal.time)
            getDashBoardData(cal.time)
        }


    private fun getDashBoardData(time: Date) {
        details?.let {
            viewModel.getDashBoard(
                getAuthorization(),
                it.user.company.id,
                SimpleDateFormat("yyyy-MM-dd").format(time)
            )
        }
    }

    private fun updateDate(cal: Calendar) {
        if (today.time.equals(cal.time)) {
            binding.tvToday.text = "today"
        } else {
            val myFormat = "dd-MMM-yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.tvToday.text = sdf.format(cal.time)
        }
    }

    override fun onResume() {
        super.onResume()
        //getDashBoardData(today.time)
        details?.let {
            viewModel.getDashBoard(
                getAuthorization(),
                it.user.company.id,
                ""
            )
        }
    }

}