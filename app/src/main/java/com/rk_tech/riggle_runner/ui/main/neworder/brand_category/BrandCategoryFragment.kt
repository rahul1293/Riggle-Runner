package com.rk_tech.riggle_runner.ui.main.neworder.brand_category

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.BrandResults
import com.rk_tech.riggle_runner.data.model.response.RetailerDetails
import com.rk_tech.riggle_runner.databinding.FragmentBrandCategoryBinding
import com.rk_tech.riggle_runner.databinding.ListBrandItemsBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.ProductListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BrandCategoryFragment constructor(val retailer: RetailerDetails?) :
    BaseFragment<FragmentBrandCategoryBinding>() {
    val viewModel: BrandCategoryFragmentVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.fragment_brand_category
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivClose -> {
                    requireActivity().onBackPressed()
                }
            }
        }
        retailer?.let {
            binding.tvStoreName.text = it.name
        }
        setUpRecyclerView()

        viewModel.getBrandList(getAuthorization(), "true")
        viewModel.obrBrandList.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    //brandAdapter?.list = it.data
                }
                Status.WARN -> {
                    showHideLoader(false)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                }
            }
        })

    }

    var brandAdapter:
            SimpleRecyclerViewAdapter<BrandResults, ListBrandItemsBinding>? = null

    private fun setUpRecyclerView() {
        brandAdapter =
            SimpleRecyclerViewAdapter<BrandResults, ListBrandItemsBinding>(
                R.layout.list_brand_items,
                BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlMain -> {
                        /*val intent = ProductListActivity.newIntent(requireActivity())
                        intent.putExtra("brand_id",m.id)
                        intent.putExtra("brand_name",m.name)
                        retailer?.let {
                            intent.putExtra("retailer_id",it.id)
                            intent.putExtra("retailer_name",it.name)
                        }
                        startActivity(intent)*/
                    }
                }
            }
        binding.rvBrands.layoutManager = GridLayoutManager(activity, 2)
        binding.rvBrands.adapter = brandAdapter
    }

}