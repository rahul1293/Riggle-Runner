package com.rk_tech.riggle_runner.ui.main.pending.payment_details.cancel_order

import android.os.Bundle
import android.text.TextUtils
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
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response_v2.RunnerOrderCancellationReason
import com.rk_tech.riggle_runner.databinding.ItemCancelListBinding
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.CallBackBlurry
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showSuccessToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_cancel.*

@AndroidEntryPoint
class CancelOrderSheet : BottomSheetDialogFragment() {

    val viewModel: CancelOrderSheetVM by viewModels()
    var cancelReasonOne = ArrayList<RunnerOrderCancellationReason>()
    private var reason = ""
    private var mlistener: CallBackBlurry? = null

    var reasonAdapter: SimpleRecyclerViewAdapter<RunnerOrderCancellationReason, ItemCancelListBinding>? =
        null

    private fun setRecyclerView() {
        reasonAdapter =
            SimpleRecyclerViewAdapter<RunnerOrderCancellationReason, ItemCancelListBinding>(
                R.layout.item_cancel_list,
                BR.bean
            ) { v, m, pos ->
                for (index in cancelReasonOne) {
                    if (index.name == m.name) {
                        index.check = true
                        if (index.name.equals("Others", true)) {
                            etReason.visibility = View.VISIBLE
                            /*if (index.check) {
                                etReason.visibility = View.VISIBLE
                            } else {
                                etReason.visibility = View.GONE
                            }*/
                        } else {
                            etReason.visibility = View.GONE
                        }
                        reason = m.name
                    } else {
                        index.check = false
                    }
                }
                reasonAdapter?.notifyDataSetChanged()
            }
        val layoutManager = LinearLayoutManager(requireContext())
        rvVariants.layoutManager = layoutManager
        rvVariants.adapter = reasonAdapter
        //reasonAdapter?.list = cancelReason
    }

    fun setListener(listener: CallBackBlurry) {
        this.mlistener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_cancel, container, false)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mlistener?.isExpand(false)
                    dismiss()
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
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
                    behavior.isDraggable = true
                    behavior.peekHeight = 0
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewModel.obrCancelOrder.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    showSuccessToast("Cancelled")
                    dismiss()
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrReason.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it?.data?.let { list ->
                        cancelReasonOne = list as ArrayList<RunnerOrderCancellationReason>
                    }
                    reasonAdapter?.list = it.data
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        setRecyclerView()
        ivCross.setOnClickListener {
            mlistener?.isExpand(false)
            dismiss()
        }
        btnContinue.setOnClickListener {
            arguments?.getInt("order_id", 0)?.let { id ->
                SharedPrefManager.getSavedUser()?.let { user ->
                    if (reason.isNotEmpty()) {
                        if (reason == "Others") {
                            if (!TextUtils.isEmpty(etReason.text.toString().trim())) {
                                viewModel.getCancelOrder(
                                    user.session_id,
                                    id,
                                    etReason.text.toString().trim()
                                )
                            } else {
                                etReason.error = "Add cancellation reason"
                            }
                        } else {
                            viewModel.getCancelOrder(user.session_id, id, reason)
                        }
                    } else {
                        showErrorToast("Select cancellation reason")
                    }
                }
            }
        }
        SharedPrefManager.getSavedUser()?.let { user ->
            viewModel.getCancellationReason(user.session_id)
        }
    }

}