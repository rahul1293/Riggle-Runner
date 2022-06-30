package com.rk_tech.riggle_runner.ui.main.search_store


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.PlaceOrderRequest
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.data.model.response_v2.GetRetailsListItem
import com.rk_tech.riggle_runner.databinding.FragmentSearchStoreBinding
import com.rk_tech.riggle_runner.databinding.ListOfSearchItemsBinding
import com.rk_tech.riggle_runner.databinding.ListOfSuggestedBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.create_store.CreateStoreFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.utils.BackStackManager
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.extension.successToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class SearchStoreFragment : BaseFragment<FragmentSearchStoreBinding>() {

    private var mainActivity: MainActivity? = null
    private val viewModel: SearchStoreVM by viewModels()

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
                    mainActivity?.addSubFragment(NewOrderFragment.TAG, CartFragment())
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

    private var searchAdapter: SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSearchItemsBinding>? =
        null

    private fun filterList(text: String): ArrayList<DummyData> {
        val list = ArrayList<DummyData>()
        nameList?.let {
            for (i in nameList?.indices!!) {
                if (nameList?.get(i)?.name?.contains(text, true) == true) {
                    list.add(nameList?.get(i)!!)
                }
            }
            return list
        }
        return list
    }

    private var nameList: ArrayList<DummyData>? = null
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
                    placeOrder(m)
                }

            }
        }
        binding.rvSearchAdapter.adapter = searchAdapter
        /*nameList = ArrayList<DummyData>()
        nameList?.add(DummyData("Sandeep", ""))
        nameList?.add(DummyData("Rahul", ""))
        searchAdapter?.list = nameList*/

        suggestedAdapter =
            SimpleRecyclerViewAdapter<GetRetailsListItem, ListOfSuggestedBinding>(
                R.layout.list_of_suggested, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlMain -> {
                        placeOrder(m)
                    }
                }
                /*  val layout = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                  binding.rvSuggested.layoutManager = layout*/

            }
        binding.rvSuggested.adapter = suggestedAdapter
        //suggestedAdapter?.list = dummyList
    }

    private var cal = Calendar.getInstance()
    private fun placeOrder(data: GetRetailsListItem?) {
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

}


