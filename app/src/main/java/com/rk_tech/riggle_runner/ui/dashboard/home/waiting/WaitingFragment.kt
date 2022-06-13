package com.rk_tech.riggle_runner.ui.dashboard.home.waiting

import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.PendingOrdersBean
import com.rk_tech.riggle_runner.databinding.FragmentWaitingBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitingFragment : BaseFragment<FragmentWaitingBinding>() {

    val viewModel: WaitingFragmentVM by viewModels()
    var pendingAdapter: PendingRecyclerViewAdapter? = null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_waiting
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        pendingAdapter =
            PendingRecyclerViewAdapter(object : PendingRecyclerViewAdapter.ProductCallback {
                override fun onItemClick(v: View?, m: PendingOrdersBean?, pos: Int?) {
                    when (v?.id) {
                        R.id.tvAcceptOrder -> {
                            pos?.let { pendingAdapter?.notifyItemRemoved(it) }
                            //acceptOrder(m, 1)
                        }
                        R.id.ivCancel -> {
                            //acceptOrder(m, 2)
                        }
                    }
                }
            })
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvPending.layoutManager = layoutManager
        binding.rvPending.adapter = pendingAdapter
        pendingAdapter?.showShimmerLoading(binding.shimmer)
    }

}