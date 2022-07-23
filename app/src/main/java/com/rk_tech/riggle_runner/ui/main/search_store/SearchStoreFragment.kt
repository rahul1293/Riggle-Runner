package com.rk_tech.riggle_runner.ui.main.search_store

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.PlaceOrderRequest
import com.rk_tech.riggle_runner.data.model.response_v2.GetRetailsListItem
import com.rk_tech.riggle_runner.databinding.FragmentSearchStoreBinding
import com.rk_tech.riggle_runner.databinding.ListOfSearchItemsBinding
import com.rk_tech.riggle_runner.databinding.ListOfSuggestedBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.create_store.CreateStoreFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.CallBackBlurry
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet.PaymentBottomSheet
import com.rk_tech.riggle_runner.utils.BackStackManager
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.extension.successToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SearchStoreFragment : BaseFragment<FragmentSearchStoreBinding>(), LocationResultListener,
    CallBackBlurry {

    private var store_name = ""
    private var store_id = 0
    private var mainActivity: MainActivity? = null
    private val viewModel: SearchStoreVM by viewModels()

    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchStoreFragment().apply {

            }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_search_store
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        checkLocation()
        setUpRecyclerView()
        mainActivity = activity as MainActivity
        binding.etSearchStore.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                if (binding.etSearchStore.text.toString().length < 3) {
                    binding.rvSearchAdapter.visibility = View.GONE
                } else {
                    binding.rvSearchAdapter.visibility = View.VISIBLE
                    //searchAdapter?.list = filterList(binding.etSearchStore.text.toString())
                    SharedPrefManager.getSavedUser()?.let { user ->
                        viewModel.getNearByRetailer(
                            getAuthorization(),
                            user.user.id,
                            binding.etSearchStore.text.toString()
                        )
                    }
                }
            }
        })
        viewModel.onClick.observe(requireActivity()) {
            when (it.id) {
                R.id.card_cart -> {
                    BackStackManager.getInstance(requireActivity()).currentTab?.let { it1 ->
                        mainActivity?.addSubFragment(
                            it1, CartFragment()
                        )
                    }
                }
                R.id.iv_back -> {
                    mainActivity?.onBackPressed()

                }
                R.id.tvNewStore -> {
                    BackStackManager.getInstance(requireActivity()).currentTab?.let { tag ->
                        mainActivity?.addSubFragment(tag, CreateStoreFragment())
                    }
                    /*val dialog =
                        BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
                    val view = layoutInflater.inflate(R.layout.bs_order_created, null)
                    val bt = view.findViewById<TextView>(R.id.tvCollectPayment)
                    bt.setOnClickListener {
                        dialog.dismiss()
                    }
                   *//* val btcanel = view.findViewById<TextView>(R.id.tvCancel)
                    btcanel.setOnClickListener {
                        dialog.dismiss()
                    }*//*
                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()*/
                }
            }
        }

        viewModel.obrRetailerList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    if (binding.etSearchStore.text.toString().length < 3)
                        showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    if (binding.etSearchStore.text.toString().length >= 3) {
                        searchAdapter?.list = it.data
                    } else {
                        suggestedAdapter?.list = it.data
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

        viewModel.obrPlaceOrder.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    successToast(it.message)
                    it.data?.final_amount?.let { it1 -> showConfirmation(it1, it.data.id) }
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
            viewModel.getNearByRetailer(getAuthorization(), user.user.id, "")
        }
    }

    private fun checkLocation() {
        Permissions.check(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            0,
            object : PermissionHandler() {
                override fun onGranted() {
                    createLocationHandler()
                }

                override fun onDenied(
                    context: Context?,
                    deniedPermissions: ArrayList<String>?
                ) {
                    super.onDenied(context, deniedPermissions)
                    showErrorToast("Need to enable the location permission")
                }
            })
    }

    var index = 0
    private fun showConfirmation(finalAmount: Double, id: Int) {
        val dialog =
            BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bs_order_created, null)
        val bt = view.findViewById<TextView>(R.id.tvCollectPayment)
        val tvText = view.findViewById<TextView>(R.id.tvText)
        tvText.text = "Order created for $store_name amounting to $finalAmount"
        bt.setOnClickListener {
            if (index < binding.clMain.childCount) {
                binding.clMain.removeViewAt(index)
            }
            openPaymentSheet(finalAmount, id)
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
        index = binding.clMain.childCount
        try {
            Blurry.with(activity).sampling(1).onto(binding.clMain)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openPaymentSheet(finalAmount: Double, id: Int) {
        val paymentSheet = PaymentBottomSheet()
        paymentSheet.setListener(this)
        val bundle = Bundle()
        bundle.putInt("order_id", id)
        finalAmount.let { finalAmount ->
            bundle.putString("pending_amount", finalAmount.toString())
            bundle.putInt("retailer_id", store_id)
        }

        paymentSheet.arguments = bundle
        paymentSheet.show(childFragmentManager, paymentSheet.tag)
        paymentSheet.isCancelable = false
        index = binding.clMain.childCount
        Handler(Looper.getMainLooper()).postDelayed({
            Blurry.with(activity).sampling(1).onto(binding.clMain)
        }, 500)
    }

    private var searchAdapter: SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSearchItemsBinding>? =
        null
    var suggestedAdapter: SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSuggestedBinding>? =
        null

    private fun setUpRecyclerView() {
        searchAdapter = SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSearchItemsBinding>(
            R.layout.list_of_search_items, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                    binding.etSearchStore.setText(m?.name!!)
                    binding.rvSearchAdapter.visibility = View.GONE
                    store_name = m.name
                    store_id = m.id
                    placeOrder(m)
                }

            }
        }
        binding.rvSearchAdapter.adapter = searchAdapter

        suggestedAdapter =
            SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSuggestedBinding>(
                R.layout.list_of_suggested, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlProduct -> {
                        store_name = m?.name!!
                        store_id = m.id
                        placeOrder(m)
                    }
                }
            }
        binding.rvSuggested.adapter = suggestedAdapter
    }

    private var cal = Calendar.getInstance()
    private fun placeOrder(data: GetRetailsListItem?) {
        //cal.timeInMillis = cal.timeInMillis + (2 * 24 * 60 * 60 * 1000)
        //val date: String = updateDate(cal)
        var locationCo = ""
        mCurrentLocation?.let { location ->
            locationCo =
                location.latitude.toString() + ":" + location.longitude.toString()
        }
        data?.let { user ->
            viewModel.placeOrder(
                getAuthorization(),
                PlaceOrderRequest(user.id, locationCo)
            )
        }
    }

    private fun updateDate(cal: Calendar): String {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        return sdf.format(cal.time)
    }

    private fun createLocationHandler() {
        locationHandler = LocationHandler(requireActivity(), this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    override fun getLocation(location: Location) {
        this.mCurrentLocation = location
    }

    override fun isExpand(isOpen: Boolean) {
        if (index < binding.clMain.childCount) {
            binding.clMain.removeViewAt(index)
        }
    }

}


