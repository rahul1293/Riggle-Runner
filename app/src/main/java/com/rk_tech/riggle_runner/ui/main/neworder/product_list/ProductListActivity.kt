package com.rk_tech.riggle_runner.ui.main.neworder.product_list

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ComboBottomSheet
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.request.VariantUpdate
import com.rk_tech.riggle_runner.data.model.response.ProductList
import com.rk_tech.riggle_runner.data.model.response.Schemes
import com.rk_tech.riggle_runner.databinding.ActivityProductListBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ProductChooseListener
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.SchemeBottomSheet
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.OrderDetailsActivity
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListActivity : BaseActivity<ActivityProductListBinding>() {
    val viewModel: ProductListActivityVM by viewModels()
    private var retailerId = 0
    private var retailer_name = ""

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, ProductListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

        var obrComboSelect = SingleRequestEvent<ArrayList<VariantUpdate>>()

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_product_list
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        binding.header.type = 1
        binding.header.ivNotification.setImageResource(R.drawable.ic_tick)
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressed()
                }
                R.id.ivNotification -> {
                    AlertDialog.Builder(this)
                        .setTitle("Delivery Order")
                        .setMessage("Are you sure want to deliver this order?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            val products = ArrayList<VariantUpdate>()
                            adapter?.getList()?.let {
                                for (index in it.indices) {
                                    if (it[index].quantity > 0) {
                                        val update = VariantUpdate(
                                            it[index].id,
                                            it[index].quantity,
                                            null
                                        )
                                        products.add(update)
                                    }
                                }
                            }
                            apiCalls(products)
                            dialog.dismiss()
                        }
                        .setNegativeButton("NO") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                    /*val intent = OrderDetailsActivity.newIntent(this)
                    startActivity(intent)*/
                }
            }
        }
        intent.getStringExtra("brand_name")?.let {
            binding.header.tvTitle.text = it
        }

        intent.getIntExtra("retailer_id", 0).let {
            retailerId = it
        }

        intent.getStringExtra("retailer_name")?.let {
            retailer_name = it
        }

        setUpRecyclerView()
        intent.getIntExtra("brand_id", 0).let {
            viewModel.getProductList(getAuthorization(), it.toString())
        }
        viewModel.obrProductList.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    val dataList: ArrayList<ProductList> = it.data as ArrayList<ProductList>
                    adapter?.setList(dataList)
                }
                Status.WARN -> {
                    showHideLoader(false)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                }
            }
        })

        viewModel.obrPlaceOrder.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    it?.data?.let { m ->
                        val intent = OrderDetailsActivity.newIntent(this)
                        if (m.isNotEmpty()) {
                            intent?.putExtra("order_id", m[0].id)
                        }
                        intent?.putExtra("store_name", retailer_name)
                        startActivity(intent)
                    }
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

        obrComboSelect.observe(this, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { data -> apiCalls(data) }
                }
            }
        })

    }

    private fun apiCalls(data: ArrayList<VariantUpdate>) {
        viewModel.orderProduct(getAuthorization(), OrderRequest(retailerId, 0, data, "todo"))
    }

    private var adapter: ShopByBrandsProductsAdapter? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        adapter = ShopByBrandsProductsAdapter(this)
        binding.rvProductList.layoutManager = layoutManager
        binding.rvProductList.adapter = adapter
        adapter?.setListener(object : ShopByBrandsProductsAdapter.ShopByBrandsProductsListener {
            override fun itemClicked(product_id: Int) {

            }

            override fun singleVariantUpdate(view: View, id: Int, position: Int) {
                when (view.id) {
                    R.id.tvCartCount -> {
                        val sheet = SchemeBottomSheet()
                        val bundle = Bundle()
                        bundle.putInt("product_id", id)
                        bundle.putString(
                            "scheme",
                            Gson().toJson(adapter?.getList()?.get(position)?.schemes)
                        )
                        bundle.putString("product_name", adapter?.getList()?.get(position)?.name)
                        sheet.arguments = bundle
                        sheet.show(supportFragmentManager, sheet.tag)
                        sheet.setListener(object : ProductChooseListener {
                            override fun itemUpdated(scheme: Schemes, pos: Int) {

                            }
                        })
                    }
                    R.id.tvAdd -> {
                        val sheet = ComboBottomSheet()
                        sheet.show(supportFragmentManager, sheet.tag)
                        sheet.isCancelable = false
                        //sheet.setListener(this)
                    }
                }
            }

            override fun comboClick(pos: Int) {
                val sheet = ComboBottomSheet()
                val bundle = Bundle()
                bundle.putBoolean("is_update", false)
                adapter?.getList()?.get(pos)?.id?.let {
                    bundle.putInt("product_id", it)
                }
                adapter?.getList()?.get(pos)?.banner_image?.image?.let {
                    bundle.putString("banner_img", it)
                }
                bundle.putString(
                    "scheme",
                    Gson().toJson(adapter?.getList()?.get(pos)?.combo_products)
                )
                sheet.arguments = bundle
                sheet.show(supportFragmentManager, sheet.tag)
                sheet.isCancelable = false
                sheet.setListener(object : ProductChooseListener {
                    override fun itemUpdated(scheme: Schemes, pos: Int) {

                    }

                    override fun onRefresh() {
                        super.onRefresh()
                    }
                })
            }
        })
    }
}