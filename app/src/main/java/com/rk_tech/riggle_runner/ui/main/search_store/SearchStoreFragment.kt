package com.rk_tech.riggle_runner.ui.main.search_store


import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.databinding.FragmentSearchStoreBinding
import com.rk_tech.riggle_runner.databinding.ListOfSearchItemsBinding
import com.rk_tech.riggle_runner.databinding.ListOfSuggestedBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.login.LoginActivity
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.create_store.CreateStoreFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.list_of_search_items.view.*

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
                if (binding.etSearchStore.text.toString().length == 0) {
                    binding.rvSearchAdapter.visibility = View.GONE
                } else {
                    binding.rvSearchAdapter.visibility = View.VISIBLE

                    searchAdapter?.list = filterList(binding.etSearchStore.text.toString())
                    binding.rvSearchAdapter.adapter = searchAdapter
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
                    mainActivity?.addSubFragment("SearchStoreFragment", CreateStoreFragment())
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
    }

    private var searchAdapter: SimpleRecyclerViewAdapter<DummyData, ListOfSearchItemsBinding>? =
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
    private fun setUpRecyclerView() {
        searchAdapter = SimpleRecyclerViewAdapter<DummyData, ListOfSearchItemsBinding>(
            R.layout.list_of_search_items, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                    binding.etSearchStore.setText(m?.name!!)
                    binding.rvSearchAdapter.visibility = View.GONE
                }

            }
        }
        nameList = ArrayList<DummyData>()
        nameList?.add(DummyData("Sandeep", ""))
        nameList?.add(DummyData("Rahul", ""))
        searchAdapter?.list = nameList


        val suggestedAdapter = SimpleRecyclerViewAdapter<DummyData, ListOfSuggestedBinding>(
            R.layout.list_of_suggested, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                }

            }
            /*  val layout = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
              binding.rvSuggested.layoutManager = layout*/

        }
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        suggestedAdapter.list = dummyList
        binding.rvSuggested.adapter = suggestedAdapter


    }
}


