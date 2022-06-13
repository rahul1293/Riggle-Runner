package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request.VariantUpdate
import com.rk_tech.riggle_runner.data.model.response.ComboProducts
import com.rk_tech.riggle_runner.data.model.response.ProductList
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.ProductListActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.OrderDetailsActivity
import kotlinx.android.synthetic.main.bottom_sheet_combo.*
import kotlinx.android.synthetic.main.bottom_sheet_combo.view.*
import kotlinx.android.synthetic.main.bottom_sheet_scheme.view.ivCross
import kotlinx.android.synthetic.main.bottom_sheet_scheme.view.rvVariants
import kotlin.math.roundToInt

class ComboBottomSheet : BottomSheetDialogFragment(),
    ProductsComboAdapter.HomeProductsListener {
    var adapter: ProductsComboAdapter? = null
    private var final_combo_count: Int = 0
    private var moqStep: Int = 0
    private var moqSteper: Int = 0
    private var count = 1
    private var check = false
    var schemeList: ArrayList<ComboProducts>? = null
    private var listener: ProductChooseListener? = null
    fun setListener(listener: ProductChooseListener?) {
        this.listener = listener
    }

    override fun variantAdded(combo_id: Int, quantity: Int) {
        final_combo_count = 0
        adapter?.getList()?.let {
            for (index in it.indices) {
                final_combo_count += it[index].quantity
            }

            if (!check) {
                val c = final_combo_count / moqStep
                if (c > 0 && final_combo_count % moqStep == 0) {
                    arguments?.getBoolean("is_update")?.let { isUpdate ->
                        if (isUpdate) {
                            moqStep = c * it[0].product?.retailer_step!!
                            moqSteper = it[0].product?.retailer_step!!
                        } else {
                            moqStep = c * it[0].retailer_step
                            moqSteper = it[0].retailer_step
                        }
                    }
                    count = c
                }
            }

            val dividend: Float = final_combo_count.toFloat() / moqSteper.toFloat()
            val reminder: Float = final_combo_count.toFloat() % moqSteper.toFloat()
            var step = 0
            if (reminder == 0f) {
                tvMoqCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.profit_color
                    )
                )
                step = if (final_combo_count == 0) 1 else (dividend).toInt()
            } else {
                tvMoqCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorError
                    )
                )
                step = (dividend + 1).toInt()
            }
            tvMoqCount.text =
                "MOQ : " + final_combo_count + "/" + (step * moqSteper)

            /*if (final_combo_count == moqStep) {
                tvMoqCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.profit_color
                    )
                )
                count++
            } else {
                tvMoqCount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorError
                    )
                )

                schemeList?.get(0)?.step?.let { s ->
                    if (final_combo_count % s == 0 && final_combo_count < moqStep && final_combo_count > 0) {
                        if (count > 0)
                            count--
                        tvMoqCount.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.profit_color
                            )
                        )
                    } else {
                        tvMoqCount.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.colorError
                            )
                        )
                    }
                    moqStep = count * s
                }

            }
            tvMoqCount.text =
                "MOQ : " + final_combo_count + "/" + moqStep.toString()*/
        }
    }

    fun itemUpdatedItem(product: MenuBean, quantity: Int) {

    }

    private fun showHideLoader(state: Boolean) {

    }

    private var product_id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_combo, container, false)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            view.viewTreeObserver.addOnGlobalLayoutListener {
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

                bottomSheet?.let {
                    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
                    behavior.isDraggable = false
                    behavior.peekHeight = 0
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val bundle = arguments
        if (bundle != null) {
            product_id = bundle.getInt("product_id", 0)
            schemeList = Gson().fromJson(
                bundle.getString("scheme"),
                object : TypeToken<ArrayList<ComboProducts>>() {}.type
            )
            schemeList?.let {
                if (it.isNotEmpty()) {
                    moqStep = it[0].step
                    moqSteper = it[0].step
                }
            }

            bundle.getString("banner_img")?.let {
                Glide.with(requireContext()).load(it).placeholder(R.mipmap.ic_app_launcher)
                    .into(ivImg)
            }

            bundle.getBoolean("is_update")?.let {
                if (it) {
                    tvTitle.text = "Update Mix"
                }
            }
        }

        schemeList?.let {
            if (it.size > 0) {
                product_id = it[0].id
                tvName.text = it[0].name
                tvMOQ.text = it[0].step.toString()
                tvMoqCount.text = "MOQ : 0/" + it[0].step.toString()
            }
            it[0].products?.let { list ->
                populateRecyclerView(list as ArrayList<ProductList>)
            }
        }

        view.ivCross?.setOnClickListener { dismiss() }
        view.ivDone?.setOnClickListener {
            schemeList?.let { it ->
                if ((final_combo_count == it[0].step || final_combo_count % it[0].step == 0) && final_combo_count != 0) {
                    var message = ""
                    arguments?.getBoolean("is_update")?.let { isUpdate ->
                        message = if (isUpdate) {
                            "Are you sure want to update this order?"
                        } else {
                            "Are you sure want to deliver this order?"
                        }
                    }
                    AlertDialog.Builder(requireContext())
                        .setTitle("Delivery Order")
                        .setMessage(message)
                        .setPositiveButton("Yes") { dialog, _ ->
                            apiCallCombo()
                            dialog.dismiss()
                            dismiss()
                        }
                        .setNegativeButton("NO") { dialog, _ ->
                            dialog.dismiss()
                        }.show()
                    /*apiCallCombo()
                    dismiss()*/
                } else {
                    Toast.makeText(
                        requireContext(),
                        "mix items are equal to multiple of MOQ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun apiCallCombo() {
        val products = ArrayList<VariantUpdate>()
        adapter?.getList()?.let {
            for (index in it.indices) {
                if (it[index].quantity > 0) {
                    val update = VariantUpdate(
                        it[index].id,
                        it[index].quantity,
                        product_id
                    )
                    products.add(update)
                }
            }
        }
        if (products.size > 0) {
            arguments?.getBoolean("is_update")?.let {
                if (it) {
                    OrderDetailsActivity.obrComboSelect.value = Resource.success(products, "")
                } else {
                    ProductListActivity.obrComboSelect.value = Resource.success(products, "")
                }
            }
        }
    }

    private fun populateRecyclerView(schemeList: ArrayList<ProductList>) {
        view?.rootView?.let { rootView ->
            activity?.let { activity ->
                adapter = ProductsComboAdapter(requireActivity(), schemeList)
                arguments?.getBoolean("is_update")?.let {
                    adapter?.setUpdate(it)
                }
                rootView.rvVariants?.layoutManager = LinearLayoutManager(activity)
                rootView.rvVariants?.adapter = adapter
                adapter?.setListener(this)
                variantAdded(0, 0)
                check = true
            }
        }
        /*schemeList?.let { data ->
                if (data.size > 0) {

                }
            }*/
    }

}