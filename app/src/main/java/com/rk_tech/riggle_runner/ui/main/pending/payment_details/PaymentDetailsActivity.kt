package com.rk_tech.riggle_runner.ui.main.pending.payment_details

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.Settlement
import com.rk_tech.riggle_runner.databinding.ActivityPaymentDetailsBinding
import com.rk_tech.riggle_runner.databinding.ItemSettlementBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentDetailsActivity : BaseActivity<ActivityPaymentDetailsBinding>() {

    val viewModel: PaymentDetailsActivityVM by viewModels()

    companion object {
        fun newIntent(activity: Activity): Intent? {
            val intent = Intent(activity, PaymentDetailsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_payment_details
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        binding.header.tvTitle.text = getString(R.string.settlement_with_sp)

        details?.let {
            binding.tvDeliveryExecutive.text = it.user.full_name
            //binding.tvStoreName.text = it.user.service_hub.name
        }

        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressed()
                }
            }
        }

        viewModel.obrPaymentHistory.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    binding.tvAmountValue.text = "₹ " + it.data?.order_value
                    it.data?.settlements?.let { settlements ->
                        setUpAdapter(settlements)
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

        /*details?.user?.service_hub?.id?.let {
            viewModel.settlementHistory(getAuthorization(), it)
        }*/
    }

    private fun setUpAdapter(settlements: List<Settlement>) {
        val adapter =
            SimpleRecyclerViewAdapter<Settlement, ItemSettlementBinding>(
                R.layout.item_settlement,
                BR.bean
            ) { v, m, pos -> }
        val linearLayoutManager = LinearLayoutManager(this)
        binding.paymentDetails.layoutManager = linearLayoutManager
        binding.paymentDetails.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(
            this,
            linearLayoutManager.orientation
        )
        dividerItemDecoration.setDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.divider
                )
            )
        )
        binding.paymentDetails.addItemDecoration(dividerItemDecoration)

        adapter.list = settlements
    }

}