package com.rk_tech.riggle_runner.ui.main.neworder.product_list

import android.os.Bundle
import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ComboBottomSheet
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request.VariantUpdate
import com.rk_tech.riggle_runner.data.model.request_v2.EditProductRequest
import com.rk_tech.riggle_runner.data.model.request_v2.ProductEditData
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.data.model.response.Schemes
import com.rk_tech.riggle_runner.data.model.response_v2.CartResponse
import com.rk_tech.riggle_runner.data.model.response_v2.ComboProduct
import com.rk_tech.riggle_runner.data.model.response_v2.ProductResult
import com.rk_tech.riggle_runner.databinding.ActivityProductListBinding
import com.rk_tech.riggle_runner.databinding.ListOfMixtureBinding
import com.rk_tech.riggle_runner.databinding.ListOfProductBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ProductChooseListener
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.SchemeBottomSheet
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bs_create_mix.view.*

@AndroidEntryPoint
class ProductListActivity : BaseFragment<ActivityProductListBinding>() {
    val viewModel: ProductListActivityVM by viewModels()
    private var mainActivity: MainActivity? = null
    var cartResponse: CartResponse? = null
    var index = 0

    companion object {
        fun newIntent(id: Int, name: String): Fragment {
            val bundle = Bundle()
            bundle.putInt("brand_id", id)
            bundle.putString("brand_name", name)
            val fragment = ProductListActivity()
            fragment.arguments = bundle
            return fragment
        }

        var obrComboSelect = SingleRequestEvent<ArrayList<VariantUpdate>>()

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_product_list
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private fun apiCalls(data: ArrayList<VariantUpdate>) {
        //viewModel.orderProduct(getAuthorization(), OrderRequest(retailerId, 0, data, "todo"))
    }

    private var adapter: ShopByBrandsProductsAdapter? = null
    private fun setUpRecyclerView() {
        adapter = ShopByBrandsProductsAdapter(requireContext())
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
                        sheet.show(childFragmentManager, sheet.tag)
                        /*sheet.setListener(object : ProductChooseListener {
                            override fun itemUpdated(scheme: Schemes, pos: Int) {

                            }
                        })*/
                    }
                    R.id.tvAdd -> {
                        val sheet = ComboBottomSheet()
                        sheet.show(childFragmentManager, sheet.tag)
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
                sheet.show(childFragmentManager, sheet.tag)
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
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvProductList.layoutManager = layoutManager
        setAdapter()

    }

    private var productAdapter: SimpleRecyclerViewAdapter<ProductResult, ListOfProductBinding>? =
        null

    private fun setAdapter() {
        productAdapter = SimpleRecyclerViewAdapter<ProductResult, ListOfProductBinding>(
            R.layout.list_of_product, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {

                }
                R.id.tv_createMix -> {
                    m.combo_products?.let { comboList->
                        createMixBox(comboList)
                    }
                }
                R.id.tvAdd -> {
                    if (m.combo_products != null && m.combo_products.isNotEmpty()) {
                        m.combo_products?.let { comboList->
                            createMixBox(comboList)
                        }
                    } else {
                        val qty = m.quantity + m.retailer_moq
                        updateListQty(qty, m, pos)
                    }
                }
                R.id.ivMinus -> {
                    if (m.combo_products != null && m.combo_products.isNotEmpty()) {
                        m.combo_products?.let { comboList->
                            createMixBox(comboList)
                        }
                    } else {
                        val qty = m.quantity - m.retailer_moq
                        updateListQty(qty, m, pos)
                    }
                }
                R.id.ivPlus -> {
                    if (m.combo_products != null && m.combo_products.isNotEmpty()) {
                        m.combo_products?.let { comboList->
                            createMixBox(comboList)
                        }
                    } else {
                        val qty = m.quantity + m.retailer_moq
                        updateListQty(qty, m, pos)
                    }
                }
            }
        }
        binding.rvProductList.adapter = productAdapter
        //productAdapter?.list = dummyList
    }

    private fun createMixBox(comboList: List<ComboProduct>) {
        val dialog =
            BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bs_create_mix, null)
        val cardCancel = view.findViewById<CardView>(R.id.card)
        view.rvMixture.adapter = mixtureAdpater!!
        cardCancel.setOnClickListener {
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
        mixtureAdpater?.list = comboList[0].products
    }

    private fun updateListQty(qty: Int, productResult: ProductResult?, pos: Int) {
        if (qty > 0) {
            productResult?.quantity = qty
        } else {
            productResult?.quantity = 0
        }
        productAdapter?.notifyItemChanged(pos)

        val product = ArrayList<ProductEditData>()
        product.add(ProductEditData(qty, productResult?.id!!))

        cartResponse?.let {
            viewModel.updateCartProduct(getAuthorization(), it.id, EditProductRequest(product))
        }
    }

    private var mixtureAdpater: SimpleRecyclerViewAdapter<ProductResult, ListOfMixtureBinding>? = null
    private fun initMixAdapter() {
        mixtureAdpater = SimpleRecyclerViewAdapter(R.layout.list_of_mixture, BR.bean) { i, v, pos ->
        }
        /*val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        mixtureAdpater?.list = dummyList*/
    }

    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        initMixAdapter()
        binding.header.type = 1
        binding.header.ivNotification.setImageResource(R.drawable.ic_shopping_cart)
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    mainActivity?.onBackPressed()
                }
                R.id.ivNotification -> {
                    /*AlertDialog.Builder(requireActivity())
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
                        }.show()*/
                    /*val intent = OrderDetailsActivity.newIntent(this)
                    startActivity(intent)*/
                    mainActivity?.addSubFragment(NewOrderFragment.TAG, CartFragment())
                }
            }
        }
        arguments?.getString("brand_name")?.let {
            binding.header.tvTitle.text = it
        }

        setUpRecyclerView()
        arguments?.getInt("brand_id", 0).let {
            viewModel.getProductList(getAuthorization(), it.toString())
        }
        viewModel.obrProductList.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    val dataList: ArrayList<ProductResult> = it.data as ArrayList<ProductResult>
                    //adapter?.setList(dataList)
                    productAdapter?.list = dataList
                }
                Status.WARN -> {
                    showHideLoader(false)
                }
                Status.ERROR -> {
                    showHideLoader(false)
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
                    it?.data?.let { m ->
                        /*val intent = OrderDetailsActivity.newIntent(requireActivity())
                        if (m.isNotEmpty()) {
                            intent?.putExtra("order_id", m[0].id)
                        }
                        intent?.putExtra("store_name", retailer_name)
                        startActivity(intent)*/
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

        obrComboSelect.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { data -> apiCalls(data) }
                }
            }
        })

        viewModel.obrCartList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    cartResponse = it.data
                }
                Status.WARN -> {
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrCartUpdate.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {

                }
                Status.WARN -> {
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })
        viewModel.getCartData(getAuthorization())
    }

}