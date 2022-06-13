package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.Schemes
import kotlinx.android.synthetic.main.bottom_sheet_scheme.view.*

class SchemeBottomSheet : BottomSheetDialogFragment(),
    ProductsSchemeAdapter.HomeProductsListener {
    private var listener: ProductChooseListener? = null

    override fun variantAdded(position: Int, quantity: Int) {
        schemeList?.get(position)?.let { listener?.itemUpdated(it, product_id) }
        dismiss()
    }

    private var product_id = 0
    private var product_name: String = ""
    var schemeList = ArrayList<Schemes>()
    fun setListener(listener: ProductChooseListener) {
        this.listener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //RiggleApplication.getInstance().getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_scheme, container, false)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
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
                    behavior.peekHeight = 0
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val bundle = arguments
        if (bundle != null) {
            product_id = bundle.getInt("product_id", 0)
            product_name = bundle.getString("product_name").toString()
            schemeList = Gson().fromJson(
                bundle.getString("scheme"),
                object : TypeToken<ArrayList<Schemes>>() {}.type
            )
        }
        view.ivCross?.setOnClickListener { dismiss() }
        populateRecyclerView()
    }

    private fun populateRecyclerView() {

        view?.rootView?.let { rootView ->
            activity?.let { activity ->
                val adapter = ProductsSchemeAdapter(requireActivity(), schemeList, product_name)
                rootView.rvVariants?.layoutManager = LinearLayoutManager(activity)
                rootView.rvVariants?.adapter = adapter
                adapter.setListener(this)
            }
        }

    }

}