package com.rk_tech.riggle_runner.ui.main.create_store

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.PlaceOrderRequest
import com.rk_tech.riggle_runner.data.model.response_v2.CreateRetailerResponse
import com.rk_tech.riggle_runner.databinding.FragmentCreateStoreBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.extension.successToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class CreateStoreFragment : BaseFragment<FragmentCreateStoreBinding>() {


    private var mainActivity: MainActivity? = null
    private val viewModel: CreateStoreVM by viewModels()

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateStoreFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_store
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        mainActivity = activity as MainActivity
        viewModel.onClick.observe(requireActivity()) {
            when (it.id) {
                R.id.card_cart -> {
                    mainActivity?.addSubFragment(NewOrderFragment.TAG, CartFragment())
                }
                R.id.iv_back -> {
                    mainActivity?.onBackPressed()

                }
                R.id.tvNewStore -> {
                    val dialog =
                        BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
                    val view = layoutInflater.inflate(R.layout.bs_order_created, null)
                    val bt = view.findViewById<TextView>(R.id.tvCollectPayment)
                    bt.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()
                }
                R.id.etPinCode -> {
                    binding.acsPinCode.performClick()
                }
                R.id.tvNewStore -> {
                    if (isEmptyField()) {
                        val data = HashMap<String, String>()
                        data["name"] = binding.etSearchStore.text.toString()
                        data["mobile"] = binding.etOwnerName.text.toString()
                        data["full_name"] = binding.etSearchStore.text.toString()
                        data["pincode"] = binding.etPinCode.text.toString()
                        data["type"] = "retailer"
                        viewModel.createStore(getAuthorization(), data)
                    }
                }
            }
        }

        viewModel.obrPinList.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    it.data?.let { data ->
                        setListOnSpinner(data)
                    }
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrRetailer.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    successToast(it.message)
                    placeOrder(it.data)
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrPlaceOrder.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    successToast(it.message)
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        SharedPrefManager.getSavedUser()?.let { user ->
            viewModel.getActivePinCodes(
                getAuthorization(),
                user.user.id
            )
        }
    }

    private var cal = Calendar.getInstance()
    private fun placeOrder(data: CreateRetailerResponse?) {
        cal.timeInMillis = cal.timeInMillis + (2 * 24 * 60 * 60 * 1000)
        val date: String = updateDate(cal)
        data?.let { user ->
            viewModel.placeOrder(
                getAuthorization(),
                PlaceOrderRequest(user.id, date)
            )
        }
    }

    private fun updateDate(cal: Calendar): String {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

    private fun isEmptyField(): Boolean {
        if (TextUtils.isEmpty(binding.etSearchStore.text.toString().trim())) {
            binding.etSearchStore.error = "Enter Shop Name"
            return false
        }
        if (TextUtils.isEmpty(binding.etOwnerName.text.toString().trim())) {
            binding.etOwnerName.error = "Enter Valid Number"
            return false
        }
        if (TextUtils.isEmpty(binding.etPinCode.text.toString().trim())) {
            binding.etOwnerName.error = "Select Pin Code"
            return false
        }
        if (binding.etOwnerName.text.toString().length < 10) {
            binding.etOwnerName.error = "Enter Valid Number"
            return false
        }
        return true
    }

    private var mData: List<String>? = null
    private fun setListOnSpinner(data: List<String>) {
        mData = data
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, data)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.acsPinCode.adapter = adapter
        binding.acsPinCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mData?.let {
                    binding.etPinCode.setText(it[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }
}