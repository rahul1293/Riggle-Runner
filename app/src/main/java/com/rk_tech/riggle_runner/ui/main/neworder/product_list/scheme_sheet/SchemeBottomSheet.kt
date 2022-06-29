package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.Schemes
import com.rk_tech.riggle_runner.data.model.response_v2.BrandResult
import com.rk_tech.riggle_runner.data.model.response_v2.OfferResult
import com.rk_tech.riggle_runner.databinding.BottomSheetSchemeBinding
import com.rk_tech.riggle_runner.databinding.ListCouponItemsBinding
import com.rk_tech.riggle_runner.databinding.ListOfRetailersBinding
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import com.rk_tech.riggle_runner.ui.main.neworder.product_list.ProductListActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.CallBackBlurry
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showSuccessToast
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.bottom_sheet_scheme.view.*

@AndroidEntryPoint
class SchemeBottomSheet : BottomSheetDialogFragment() {
    val viewModel: SchemeBottomSheetVM by viewModels()
    private var mlistener: CallBackBlurry? = null
    private var brand_id = 0
    private var product_name: String = ""
    var schemeList = ArrayList<Schemes>()
    lateinit var binding: BottomSheetSchemeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSchemeBinding.inflate(inflater)
        return binding.root//inflater.inflate(R.layout.bottom_sheet_scheme, container, false)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mlistener?.isExpand(false)
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

        viewModel.obrBrandOffer.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it.data?.let { list ->
                        offerAdapter?.list = list
                    }
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        view.ivCross?.setOnClickListener {
            mlistener?.isExpand(false)
            dismiss()
        }
        populateRecyclerView()
        val bundle = arguments
        if (bundle != null) {
            brand_id = bundle.getInt("brand_id", 0)
            SharedPrefManager.getSavedUser()?.let { user ->
                viewModel.getBrandOffer(user.session_id, brand_id)
            }
        }
    }

    var offerAdapter: SimpleRecyclerViewAdapter<OfferResult, ListCouponItemsBinding>? = null
    private fun populateRecyclerView() {

        view?.rootView?.let { rootView ->
            activity?.let { activity ->
                //val adapter = ProductsSchemeAdapter(requireActivity(), schemeList, product_name)
                offerAdapter = SimpleRecyclerViewAdapter<OfferResult, ListCouponItemsBinding>(
                    R.layout.list_coupon_items, BR.bean
                ) { v, m, pos ->
                    when (v.id) {
                    }
                }
                rootView.rvVariants?.layoutManager = LinearLayoutManager(activity)
                rootView.rvVariants?.adapter = offerAdapter
                //adapter.setListener(this)
            }
        }

    }

    fun setListenerOne(callBackBlurry: CallBackBlurry) {
        this.mlistener = callBackBlurry
    }

}