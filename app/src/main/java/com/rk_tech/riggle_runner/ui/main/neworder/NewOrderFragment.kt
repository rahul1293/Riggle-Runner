package com.rk_tech.riggle_runner.ui.main.neworder

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.RetailerDetails
import com.rk_tech.riggle_runner.databinding.FragmentNewOrdersBinding
import com.rk_tech.riggle_runner.databinding.ListRetailerItemBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.brand_category.BrandCategoryFragment
import com.rk_tech.riggle_runner.ui.main.neworder.create_retailer.CreateRetailerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<FragmentNewOrdersBinding>() {
    val viewModel: NewOrderFragmentVM by viewModels()

    private var mainActivity: MainActivity? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())

    companion object {
        fun newInstance(): Fragment {
            return NewOrderFragment()
        }

        val TAG = "NewOrderFragment"
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_new_orders
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        val startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    openSubFragment(
                        Gson().fromJson(
                            intent?.getStringExtra("data"),
                            RetailerDetails::class.java
                        )
                    )
                }
            }
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvCreateNew -> {
                    val intent = CreateRetailerActivity.newIntent(requireActivity())
                    startForResult.launch(intent)
                }
            }
        }

        viewModel.obrRetailerList.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    //showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    searchAdapter?.list = it.data
                }
                Status.WARN -> {
                    showHideLoader(false)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                }
            }
        })

        searchView()
        setUpRecyclerView()
    }

    private fun openSubFragment(retailer: RetailerDetails?) {
        mainActivity?.addSubFragment(TAG, BrandCategoryFragment(retailer))
    }

    private fun searchView() {

        val searchIcon: ImageView =
            binding.etSearch.findViewById(androidx.appcompat.R.id.search_mag_icon)
        val searchCloseIcon: ImageView =
            binding.etSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
        // To change color of close button, use:
        searchIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black_54),
            PorterDuff.Mode.SRC_IN
        )
        searchCloseIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black_54),
            PorterDuff.Mode.SRC_IN
        )

        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                binding.etSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    binding.rvRetailerList.visibility = View.VISIBLE
                } else {
                    searchAdapter?.clearList()
                    binding.rvRetailerList.visibility = View.GONE
                }
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({ onSearch(s) }, 500)
                return false
            }
        })
    }

    private fun onSearch(queryString: String) {
        if (queryString.length < 3) {
            return
        }
        viewModel.getRetailerList(getAuthorization(), queryString)
    }

    var searchAdapter: SimpleRecyclerViewAdapter<RetailerDetails, ListRetailerItemBinding>? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SimpleRecyclerViewAdapter<RetailerDetails, ListRetailerItemBinding>(
            R.layout.list_retailer_item, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                    openSubFragment(m)
                }
            }
        }
        binding.rvRetailerList.layoutManager = layoutManager
        binding.rvRetailerList.adapter = searchAdapter
    }

}