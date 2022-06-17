package com.rk_tech.riggle_runner.ui.main.pending.orderdetails

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request.VariantUpdate
import com.rk_tech.riggle_runner.data.model.response.ComboProducts
import com.rk_tech.riggle_runner.data.model.response.Product
import com.rk_tech.riggle_runner.data.model.response.RequestComboUpdate
import com.rk_tech.riggle_runner.databinding.ActivityOrderDetailsBinding
import com.rk_tech.riggle_runner.databinding.ListMyOrderDetailBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet.ComboBottomSheet
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.collect_payment.CollectPaymentActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet.PaymentBottomSheet
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.cancel_order.CancelOrderSheet
import com.rk_tech.riggle_runner.utils.Constants
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry

@AndroidEntryPoint
class OrderDetailsActivity : BaseFragment<ActivityOrderDetailsBinding>(), LocationResultListener,
    CallBackBlurry {
    val viewModel: OrderDetailsActivityVM by viewModels()
    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null
    private var orderId = 0

    companion object {
        fun newIntent(activity: Activity): Intent? {
            val intent = Intent(activity, OrderDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

        fun newInstance(id: Int, name: String): Fragment {
            val fragment = OrderDetailsActivity()
            val bundle = Bundle()
            bundle?.putInt("order_id", id)
            bundle?.putString("store_name", name)
            fragment.arguments = bundle
            return fragment
        }

        var obrComboSelect = SingleRequestEvent<ArrayList<VariantUpdate>>()

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_order_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {

        checkLocation()
        binding.header.tvTitle.text = getString(R.string.retailer_orders)
        arguments?.getString("store_name")?.let {
            //binding.tvStoreName.text = it
        }
        binding.header.type = 0
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBack -> {
                    requireActivity().onBackPressed()
                }
                /*R.id.btnCancelOrders -> {
                    val sheet = CancelOrderSheet()
                    sheet.show(supportFragmentManager, sheet.tag)
                    sheet.isCancelable = false
                }*/
                R.id.btnContinue -> {
                    /*if (binding.bean?.status.equals("delivered", true)) {
                        val intent = CollectPaymentActivity.newIntent(requireActivity())
                        intent.putExtra("order_details", Gson().toJson(binding.bean))
                        intent.putExtra(
                            "retailer_name",
                            binding.header.tvTitle.text.toString().trim()
                        )
                        mCurrentLocation?.let { loc ->
                            intent.putExtra(
                                "location_data",
                                loc.latitude.toString() + "," + loc.longitude.toString()
                            )
                        }
                        startActivity(intent)
                    } else {
                        mCurrentLocation?.let { location ->
                            viewModel.deliveryOrder(getAuthorization(), orderId, location)
                        }
                    }*/
                    val paymentSheet = PaymentBottomSheet()
                    paymentSheet.setListener(this)
                    val bundle = Bundle()
                    arguments?.getInt("order_id", 0)?.let { id ->
                        bundle.putInt("order_id", id)
                    }
                    paymentSheet.arguments = bundle
                    paymentSheet.show(childFragmentManager, paymentSheet.tag)
                    paymentSheet.isCancelable = true
                    index = binding.clMain.childCount
                    Handler(Looper.getMainLooper()).postDelayed({
                        Blurry.with(activity).sampling(1).onto(binding.clMain)
                    }, 500)
                }
                R.id.ivNotification, R.id.tvCancel -> {
                    val sheet = CancelOrderSheet()
                    sheet.setListener(this)
                    val bundle = Bundle()
                    arguments?.getInt("order_id", 0)?.let { id ->
                        bundle.putInt("order_id", id)
                    }
                    sheet.arguments = bundle
                    sheet.show(childFragmentManager, sheet.tag)
                    sheet.isCancelable = false
                    index = binding.clMain.childCount
                    Handler(Looper.getMainLooper()).postDelayed({
                        Blurry.with(activity).sampling(1).onto(binding.clMain)
                    }, 500)
                }
            }
        }

        viewModel.obrOrderDelivery.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    binding.bean?.status = "delivered"
                    val intent = CollectPaymentActivity.newIntent(requireActivity())
                    intent.putExtra("order_details", Gson().toJson(binding.bean))
                    intent.putExtra("retailer_name", binding.header.tvTitle.text.toString().trim())
                    mCurrentLocation?.let { loc ->
                        intent.putExtra(
                            "location_data",
                            loc.latitude.toString() + "," + loc.longitude.toString()
                        )
                    }
                    startActivity(intent)
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
        viewModel.obrOrderDetails.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    if (binding.bean == null)
                        showHideLoader(true)
                }
                Status.SUCCESS -> {
                    binding.bean = it.data
                    it.data?.let { bean ->
                        Constants.isDeliver = bean.status.equals("delivered", true)
                    }
                    setUpAdapter(it.data?.products)
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

        viewModel.obrEditProduct.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    viewModel.getOrderDetails(getAuthorization(), orderId)
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

        viewModel.obrEditMixBox.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    viewModel.getOrderDetails(getAuthorization(), orderId)
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
                    it.data?.let { data -> apiUpdateCalls(data) }
                }
            }
        })

        setUpOrderProductList()
    }

    /*override fun onCreateView() {
        checkLocation()
        intent?.getStringExtra("store_name")?.let {
            binding.header.tvTitle.text = it//getString(R.string.order_details)
        }
        binding.header.type = 1
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressed()
                }
                *//*R.id.btnCancelOrders -> {
                    val sheet = CancelOrderSheet()
                    sheet.show(supportFragmentManager, sheet.tag)
                    sheet.isCancelable = false
                }*//*
                R.id.btnContinue -> {
                    if (binding.bean?.status.equals("delivered", true)) {
                        val intent = CollectPaymentActivity.newIntent(this)
                        intent.putExtra("order_details", Gson().toJson(binding.bean))
                        intent.putExtra(
                            "retailer_name",
                            binding.header.tvTitle.text.toString().trim()
                        )
                        mCurrentLocation?.let { loc ->
                            intent.putExtra(
                                "location_data",
                                loc.latitude.toString() + "," + loc.longitude.toString()
                            )
                        }
                        startActivity(intent)
                    } else {
                        mCurrentLocation?.let { location ->
                            viewModel.deliveryOrder(getAuthorization(), orderId, location)
                        }
                    }
                }
                R.id.ivNotification -> {
                    openMenu(it)
                }
            }
        }

        viewModel.obrOrderDelivery.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    binding.bean?.status = "delivered"
                    val intent = CollectPaymentActivity.newIntent(this)
                    intent.putExtra("order_details", Gson().toJson(binding.bean))
                    intent.putExtra("retailer_name", binding.header.tvTitle.text.toString().trim())
                    mCurrentLocation?.let { loc ->
                        intent.putExtra(
                            "location_data",
                            loc.latitude.toString() + "," + loc.longitude.toString()
                        )
                    }
                    startActivity(intent)
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
        viewModel.obrOrderDetails.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    if (binding.bean == null)
                        showHideLoader(true)
                }
                Status.SUCCESS -> {
                    binding.bean = it.data
                    it.data?.let { bean ->
                        Constants.isDeliver = bean.status.equals("delivered", true)
                    }
                    setUpAdapter(it.data?.products)
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

        viewModel.obrEditProduct.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    viewModel.getOrderDetails(getAuthorization(), orderId)
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

        viewModel.obrEditMixBox.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    viewModel.getOrderDetails(getAuthorization(), orderId)
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
                    it.data?.let { data -> apiUpdateCalls(data) }
                }
            }
        })

        setUpOrderProductList()
    }*/

    private fun apiUpdateCalls(data: ArrayList<VariantUpdate>) {
        viewModel.editComboProductItem(getAuthorization(), orderId, RequestComboUpdate(data))
    }

    private fun setUpAdapter(products: List<Product>?) {
        //productAdapter?.list = products
    }

    private fun openMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        //menu.menu.add("Call Sp")
        if (!binding.bean?.status.equals("delivered", true)) {
            menu.menu.add("Cancel order")
        }
        menu.setOnMenuItemClickListener {
            when (it.title.toString()) {
                /*"Call Sp" -> {
                }*/
                "Cancel order" -> {
                    val sheet = CancelOrderSheet()
                    val bundle = Bundle()
                    arguments?.getInt("order_id", 0)?.let {
                        bundle.putInt("order_id", it)
                    }
                    sheet.arguments = bundle
                    sheet.show(childFragmentManager, sheet.tag)
                    sheet.isCancelable = false
                }
            }
            true
        }
        menu.show()
    }

    var productAdapter: SimpleRecyclerViewAdapter<MenuBean, ListMyOrderDetailBinding>? = null
    private fun setUpOrderProductList() {
        productAdapter = SimpleRecyclerViewAdapter<MenuBean, ListMyOrderDetailBinding>(
            R.layout.list_my_order_detail,
            BR.bean
        ) { v, m, pos ->
            when (v?.id) {
                R.id.ivMinus -> {
                    /*if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        if (m.quantity > 0) {
                            m.quantity = m.quantity - m.product?.retailer_step!!
                        }
                        productAdapter?.notifyDataSetChanged()
                        viewModel.editProductQty(getAuthorization(), orderId, m.id, m.quantity)
                    }*/
                }
                R.id.ivPlus, R.id.tvAdd -> {
                    /*if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        m.quantity = m.quantity + m.product?.retailer_step!!
                        productAdapter?.notifyDataSetChanged()
                        viewModel.editProductQty(getAuthorization(), orderId, m.id, m.quantity)
                    }*/
                }
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvProducts.layoutManager = layoutManager
        binding.rvProducts.adapter = productAdapter
        productAdapter?.list = getListOne()
    }

    private fun getListOne(): ArrayList<MenuBean> {
        val dataList = ArrayList<MenuBean>()
        dataList.add(MenuBean(1, "Product Name", 0, false))
        dataList.add(MenuBean(1, "Product Name", 0, false))
        dataList.add(MenuBean(1, "Product Name", 0, false))
        dataList.add(MenuBean(1, "Product Name", 0, false))
        return dataList
    }

    private fun showComboSheet(m: Product?) {
        /*productAdapter?.list?.let { prod ->
            for (index in prod) {
                m?.product_combo?.products?.let { pro_com ->
                    for (select in pro_com) {
                        if (index.product?.name.equals(select.name, true)) {
                            select.quantity = index.quantity
                        }
                    }
                }
            }
        }*/
        m?.let { m ->
            val sheet = ComboBottomSheet()
            sheet.show(childFragmentManager, sheet.tag)
            val bundle = Bundle()
            bundle.putBoolean("is_update", true)
            m.banner_image?.image?.let {
                bundle.putString("banner_img", it)
            }
            m.products?.let { products ->
                bundle.putInt("product_id", products[0].product_combo!!)
                bundle.putString(
                    "scheme",
                    Gson().toJson(ArrayList<ComboProducts>().apply {
                        add(
                            ComboProducts(
                                m.code.toString(),
                                m.created_at,
                                products[0].product_combo!!,
                                m.is_active,
                                m.name.toString(),
                                products,
                                products[0].product?.retailer_step!!,
                                m.update_url,
                                m.updated_at
                            )
                        )
                    })
                )
            }
            sheet.arguments = bundle
            sheet.isCancelable = false
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

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    super.onDenied(context, deniedPermissions)
                }
            })
    }

    private fun createLocationHandler() {
        locationHandler = LocationHandler(requireActivity(), this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    override fun onStart() {
        super.onStart()
        arguments?.getInt("order_id", 0)?.let {
            orderId = it
            //viewModel.getOrderDetails(getAuthorization(), it)
        }
    }

    override fun getLocation(location: Location) {
        this.mCurrentLocation = location
    }

    var index = 0
    override fun isExpand(isOpen: Boolean) {
        if (index < binding.clMain.childCount) {
            binding.clMain.removeViewAt(index)
        }
    }

}