package com.rk_tech.riggle_runner.ui.main.cart_fragment

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.EditProductRequest
import com.rk_tech.riggle_runner.data.model.request_v2.ProductEditData
import com.rk_tech.riggle_runner.data.model.response_v2.CartResponse
import com.rk_tech.riggle_runner.data.model.response_v2.Product
import com.rk_tech.riggle_runner.databinding.*
import com.rk_tech.riggle_runner.ui.base.*
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.search_store.SearchStoreFragment
import com.rk_tech.riggle_runner.utils.BackStackManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bs_create_mix.view.*

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {

            }
    }

    val viewModel: CartFragmentVM by viewModels()
    var cartResponse: CartResponse? = null
    var index = 0

    private var final_combo_count: Int = 0
    private var moqStep: Int = 0
    private var moqSteper: Int = 0
    private var count = 1
    private var check = false

    override fun getLayoutResource(): Int {
        return R.layout.fragment_cart
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private var mainActivity: MainActivity? = null
    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        viewModel.onClick.observe(requireActivity()) {
            when (it.id) {
                R.id.iv_back -> {
                    mainActivity?.onBackPressed()
                }
                R.id.tvNext -> {
                    emptyCart(true)
                }
            }
        }

        viewModel.obrCartList.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    cartResponse = it.data
                    binding.bean = cartResponse
                    it.data?.products?.let { list ->
                        cartAdapter?.list = list
                        if (list.isEmpty()) {
                            binding.rvRetailerList.visibility = View.GONE
                            binding.rlAmount.visibility = View.GONE
                            binding.ivEmpty.visibility = View.VISIBLE
                            binding.tvEmptyCart.visibility = View.VISIBLE
                            binding.tvNext.text = "Add Item"
                        } else {
                            binding.rvRetailerList.visibility = View.VISIBLE
                            binding.rlAmount.visibility = View.VISIBLE
                            binding.tvEmptyCart.visibility = View.GONE
                            binding.ivEmpty.visibility = View.GONE
                            binding.tvNext.text = "Next"
                        }
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


        viewModel.obrCartUpdate.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.LOADING -> {
                    if (dialog?.isShowing == true) {
                        showHideLoader(true)
                    }
                }
                Status.SUCCESS -> {
                    if (dialog?.isShowing == true) {
                        if (index < binding.clMain.childCount) {
                            binding.clMain.removeViewAt(index)
                        }
                        dialog?.dismiss()
                    }
                    showHideLoader(false)
                    viewModel.getCartData(getAuthorization())
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

        setUpRecyclerView()
        viewModel.getCartData(getAuthorization())
    }

    @SuppressLint("SetTextI18n")
    private fun emptyCart(emptyCart: Boolean) {
        if (binding.tvNext.text.toString().equals("Add Item", true)) {
            mainActivity?.onBackPressed()
        } else {
            cartResponse?.let { cart ->
                BackStackManager.getInstance(requireActivity()).currentTab?.let {
                    mainActivity?.addSubFragment(
                        it, SearchStoreFragment.newInstance(cart.id.toString(), "")
                    )
                }
            }
        }
    }

    private var cartAdapter: SimpleRecyclerViewAdapter<Product, CartItemLayoutBinding>? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = SimpleRecyclerViewAdapter<Product, CartItemLayoutBinding>(
            R.layout.cart_item_layout, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {

                }
                R.id.card_minus -> {
                    if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        if (m.quantity > 0) {
                            m.quantity = m.quantity - m.product?.retailer_moq!!
                        }
                        cartAdapter?.notifyDataSetChanged()
                        val prodData = ArrayList<ProductEditData>()
                        m.product?.id?.let {
                            prodData.add(ProductEditData(it,  m.quantity, null))
                        }
                        //prodData.add(ProductEditData(m.id, m.quantity, null))
                        viewModel.editProductQty(
                            getAuthorization(), cartResponse?.id,
                            EditProductRequest(prodData)
                        )
                    }
                }
                R.id.card_plus -> {
                    if (m.products != null) {
                        showComboSheet(m)
                    } else {
                        m.quantity = m.quantity + m.product?.retailer_moq!!
                        cartAdapter?.notifyDataSetChanged()
                        val prodData = ArrayList<ProductEditData>()
                        m.product?.id?.let {
                            prodData.add(ProductEditData(it, m.quantity, null))
                        }
                        //prodData.add(ProductEditData(m.id, m.quantity, null))
                        viewModel.editProductQty(
                            getAuthorization(),
                            cartResponse?.id,
                            EditProductRequest(prodData)
                        )
                    }
                }
                R.id.tv_view -> {
                    if (m.products != null) {
                        // for combo
                        if (m.products?.isNotEmpty() == true) {
                            val product = ArrayList<ProductEditData>()
                            for (index in m.products) {
                                if (index.quantity > 0) {
                                    index.product?.id?.let { id->
                                        product.add(ProductEditData(id, 0, m.id))
                                    }
                                }
                            }
                            viewModel.editProductQty(
                                getAuthorization(),
                                cartResponse?.id,
                                EditProductRequest(product)
                            )
                        }
                    } else {
                        // for single
                        val prodData = ArrayList<ProductEditData>()
                        m.product?.id?.let { ProductEditData(it, 0, null) }
                            ?.let { prodData.add(it) }
                        viewModel.editProductQty(
                            getAuthorization(),
                            cartResponse?.id,
                            EditProductRequest(prodData)
                        )
                    }
                }
            }
        }
        binding.rvRetailerList.layoutManager = layoutManager
        binding.rvRetailerList.adapter = cartAdapter
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
                            if (m.quantity > 1) {
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
                    /*for (index in mixBox.products) {
                        if (index.quantity > 0) {
                            product.add(ProductEditData(index.id, index.quantity, mixBox.id))
                        }
                    }*/
                    for (index in mixBox.products) {
                        if (index.quantity > 0) {
                            index.product?.id?.let { id->
                                product.add(ProductEditData(id, index.quantity, mixBox.id))
                            }
                        }
                    }
                    viewModel.editProductQty(
                        getAuthorization(),
                        cartResponse?.id,
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


}