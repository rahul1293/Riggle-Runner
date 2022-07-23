package com.rk_tech.riggle_runner.ui.main.pending.orderdetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request.VariantUpdate
import com.rk_tech.riggle_runner.data.model.request_v2.EditProductRequest
import com.rk_tech.riggle_runner.data.model.request_v2.ProductEditData
import com.rk_tech.riggle_runner.data.model.response.RequestComboUpdate
import com.rk_tech.riggle_runner.data.model.response_v2.Product
import com.rk_tech.riggle_runner.databinding.ActivityOrderDetailsBinding
import com.rk_tech.riggle_runner.databinding.ListMyOrderDetailBinding
import com.rk_tech.riggle_runner.databinding.UpdateCreateMixBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.collect_payment.CollectPaymentActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet.PaymentBottomSheet
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.cancel_order.CancelOrderSheet
import com.rk_tech.riggle_runner.utils.Constants
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bs_create_mix.view.*

@AndroidEntryPoint
class OrderDetailsActivity : BaseFragment<ActivityOrderDetailsBinding>(), LocationResultListener,
    CallBackBlurry {
    val viewModel: OrderDetailsActivityVM by viewModels()
    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null
    private var orderId = 0

    private var final_combo_count: Int = 0
    private var moqStep: Int = 0
    private var moqSteper: Int = 0
    private var count = 1
    private var check = false

    companion object {
        fun newIntent(activity: Activity): Intent? {
            val intent = Intent(activity, OrderDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

        fun newInstance(id: Int, name: String, type: Int): Fragment {
            val fragment = OrderDetailsActivity()
            val bundle = Bundle()
            bundle?.putInt("order_id", id)
            bundle?.putString("store_name", name)
            bundle?.putInt("is_from", type)
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
            binding.tvStoreName.text = it
        }
        arguments?.getInt("is_from", 2)?.let {
            binding.type = it
            Constants.isDeliver = it == 1
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
                        binding.bean?.let { bean ->
                            bundle.putString("pending_amount", bean.pending_amount.toString())
                            bundle.putInt("retailer_id", bean.buyer/*.id*/)
                        }
                        arguments?.getString("store_name")?.let { name ->
                            bundle.putString("retailer_name", name)
                        }
                    }
                    paymentSheet.arguments = bundle
                    paymentSheet.show(childFragmentManager, paymentSheet.tag)
                    paymentSheet.isCancelable = false
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
                        //Constants.isDeliver = bean.status.equals("delivered", true)
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
                    if (dialog?.isShowing == true) {
                        if (index < binding.clMain.childCount) {
                            binding.clMain.removeViewAt(index)
                        }
                        dialog?.dismiss()
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

        /*obrComboSelect.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { data -> apiUpdateCalls(data) }
                }
            }
        })*/

        setUpOrderProductList()
    }

    private fun apiUpdateCalls(data: ArrayList<VariantUpdate>) {
        viewModel.editComboProductItem(getAuthorization(), orderId, RequestComboUpdate(data))
    }

    private fun setUpAdapter(products: List<Product>?) {
        productAdapter?.list = products
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

    var productAdapter: SimpleRecyclerViewAdapter<Product, ListMyOrderDetailBinding>? = null

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpOrderProductList() {
        productAdapter = SimpleRecyclerViewAdapter<Product, ListMyOrderDetailBinding>(
            R.layout.list_my_order_detail,
            BR.bean
        ) { v, m, pos ->
            when (v?.id) {
                R.id.ivMinus -> {
                    if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        if (m.quantity > 0) {
                            m.quantity = m.quantity - m.product?.retailer_moq!!
                        }
                        productAdapter?.notifyDataSetChanged()
                        val prodData = ArrayList<ProductEditData>()
                        m.product?.id?.let { id ->
                            prodData.add(ProductEditData(id, m.quantity, null))
                        }
                        viewModel.editProductQty(
                            getAuthorization(),
                            orderId,
                            EditProductRequest(prodData)
                        )
                    }
                }
                R.id.ivPlus, R.id.tvAdd -> {
                    if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        m.quantity = m.quantity + m.product?.retailer_moq!!
                        productAdapter?.notifyDataSetChanged()
                        val prodData = ArrayList<ProductEditData>()
                        m.product?.id?.let { id ->
                            prodData.add(ProductEditData(id, m.quantity, null))
                        }
                        viewModel.editProductQty(
                            getAuthorization(),
                            orderId,
                            EditProductRequest(prodData)
                        )
                    }
                }
            }
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvProducts.layoutManager = layoutManager
        binding.rvProducts.adapter = productAdapter
    }

    private var mixtureAdpater: SimpleRecyclerViewAdapter<Product, UpdateCreateMixBinding>? =
        null
    var tvCancel: TextView? = null
    var tvMix: TextView? = null
    var dialog: BottomSheetDialog? = null
    private fun showComboSheet(m: Product?) {
        m?.let { mixBox ->
            dialog =
                BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.bs_create_mix, null)
            val cardCancel = view.findViewById<CardView>(R.id.card)
            tvCancel = view.findViewById(R.id.tvCancel)
            tvMix = view.findViewById(R.id.tvMix)

            mixtureAdpater =
                SimpleRecyclerViewAdapter(R.layout.update_create_mix, BR.bean) { v, m, pos ->
                    when (v?.id) {
                        R.id.card_minus -> {
                            if (m.quantity > 0) {
                                m.quantity = m.quantity - 1
                                mixtureAdpater?.notifyItemChanged(pos)
                                variantAdded()
                            }
                        }
                        R.id.card_plus -> {
                            m.quantity = m.quantity + 1
                            mixtureAdpater?.notifyItemChanged(pos)
                            variantAdded()
                        }
                    }
                }
            view.rvMixture.adapter = mixtureAdpater!!
            mixtureAdpater?.list = mixBox.products
            if (mixBox.products?.isNotEmpty() == true) {
                mixBox.products?.get(0)?.product?.retailer_moq?.let {
                    moqStep = it
                    moqSteper = it
                }
                variantAdded()
            }
            cardCancel.setOnClickListener {
                if (index < binding.clMain.childCount) {
                    binding.clMain.removeViewAt(index)
                }
                dialog?.dismiss()
            }
            tvCancel?.setOnClickListener {
                if (mixBox.products?.isNotEmpty() == true) {
                    val product = ArrayList<ProductEditData>()
                    for (index in mixBox.products) {
                        if (index.quantity > 0) {
                            index.product?.id?.let { id ->
                                product.add(ProductEditData(id, index.quantity, mixBox.id))
                            }
                        }
                    }
                    viewModel.editProductQty(
                        getAuthorization(),
                        orderId,
                        EditProductRequest(product)
                    )
                }
            }
            dialog?.setCancelable(false)
            dialog?.setContentView(view)
            dialog?.show()
            index = binding.clMain.childCount
            Blurry.with(activity).sampling(1).onto(binding.clMain)
        }

    }

    private fun variantAdded() {
        final_combo_count = 0
        mixtureAdpater?.list?.let {
            for (index in it.indices) {
                final_combo_count += it[index].quantity
            }

            if (!check) {
                val c = final_combo_count / moqStep
                if (c > 0 && final_combo_count % moqStep == 0) {
                    moqStep = c * it[0].product?.retailer_moq!!
                    moqSteper = it[0].product?.retailer_moq!!
                    count = c
                }
            }

            val dividend: Float = final_combo_count.toFloat() / moqSteper.toFloat()
            val reminder: Float = final_combo_count.toFloat() % moqSteper.toFloat()
            var step = 0
            if (reminder == 0f) {
                tvCancel?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.orange
                    )
                )
                tvCancel?.isEnabled = true
                step = if (final_combo_count == 0) 1 else (dividend).toInt()
            } else {
                tvCancel?.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.medium_gray
                    )
                )
                tvCancel?.isEnabled = false
                step = (dividend + 1).toInt()
            }
            tvMix?.text =
                "MOQ : " + final_combo_count + "/" + (step * moqSteper)

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
            viewModel.getOrderDetails(getAuthorization(), it)
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

    /*Even after order is assigned, show only if pending.
    Payment not acceptable issue.
    In order listing, make default all if no date provided.
    3 tabs. Pending, New Order, My Profile.
    Order ID issue.
    Remove number from order
    Add date and full address in view details of order
    Change text: Cost to Rate in new products
    Show MOQ on top in create mix
    Create spelling error
    Color + - buttons orange
    Change text: Create mix to You can create mix.
    when choosing a store, it gives 404
    Call Store LoGo

    tell rahul to check cart logic*/

}