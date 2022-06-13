package com.rk_tech.riggle_runner.ui.main.pending

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.Admin
import com.rk_tech.riggle_runner.data.model.response.Results
import com.rk_tech.riggle_runner.data.model.response.Retailer
import com.rk_tech.riggle_runner.databinding.FragmentPendingOrdersBinding
import com.rk_tech.riggle_runner.databinding.ListOrdersItemBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.OrderDetailsActivity
import com.rk_tech.riggle_runner.ui.main.pending.payment_details.PaymentDetailsActivity
import com.rk_tech.riggle_runner.utils.VerticalPagination
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class PendingOrdersFragment : BaseFragment<FragmentPendingOrdersBinding>(),
    VerticalPagination.VerticalScrollListener {

    val viewModel: PendingOrdersFragmentVM by viewModels()
    private lateinit var mVerticalPagination: VerticalPagination
    private var mainActivity: MainActivity? = null

    companion object {
        fun newInstance(): Fragment {
            return PendingOrdersFragment()
        }

        const val TAG = "PendingOrdersFragment"
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_pending_orders
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.tvAmountValue -> {
                    val intent = PaymentDetailsActivity.newIntent(requireActivity())
                    startActivity(intent)
                }
            }
        }
        details?.let {
            binding.tvHeroName.text = it.user.full_name
            binding.tvServiceHub.text = it.user.service_hub.name
        }

        binding.srl.setOnRefreshListener {
            page = 1
            viewModel.getTripList(getAuthorization(), page.toString())
            binding.srl.isRefreshing = false
        }

        viewModel.obrTripList.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    if (ordersAdapter?.itemCount!! > 0) {

                    } else
                        showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    if (page == 1)
                        ordersAdapter?.clearList()

                    if (it.data?.size!! >= 15) {
                        page++
                        mVerticalPagination.isLoading = false
                    } else {
                        page = 1
                    }
                    ordersAdapter?.addToList(it.data)
                }
                Status.WARN -> {
                    showHideLoader(false)
                    //showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    //showErrorToast(it.message)
                }
                else -> {
                    showHideLoader(false)
                }
            }
        })

        viewModel.obrPaymentHistory.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    binding.tvAmountValue.text = "₹ " + it.data?.order_value
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        page = 1
        viewModel.getTripList(getAuthorization(), page.toString())
        details?.user?.service_hub?.id?.let {
            viewModel.settlementHistory(getAuthorization(), it)
        }
    }

    private var ordersAdapter: SimpleRecyclerViewAdapter<Results, ListOrdersItemBinding>? = null
    private var result: Results? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        ordersAdapter = SimpleRecyclerViewAdapter<Results, ListOrdersItemBinding>(
            R.layout.list_orders_item,
            BR.bean
        ) { v, m, pos ->
            when (v?.id) {
                R.id.ivNavigate -> {
                    result = m
                    result?.let {
                        it.retailer.store_location.let { d ->
                            /*val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + (mCurrentLocation?.latitude.toString() + "," + mCurrentLocation?.longitude.toString()) + "&daddr=" + d)
                            )*/
                            /*val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("google.navigation:q=an+address+city")
                            )*/
                            if (d.isNotEmpty()) {
                                /*val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("http://maps.google.com/maps?daddr=" + d)
                                )*/
                                val navigationIntentUri =
                                    Uri.parse("google.navigation:q=$d")
                                val intent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
                                intent.setPackage("com.google.android.apps.maps")
                                startActivity(intent)
                            } else {
                                //showInfoToast("location not updated..")
                            }
                        }
                    }
                    //checkLocation()
                }
                R.id.tvPhone -> {
                    if (m.retailer.admin.mobile.startsWith("+91")) {
                        val intentDial =
                            Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + m.retailer.admin.mobile))
                        requireActivity().startActivity(intentDial)
                    } else {
                        val intentDial =
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:" + "+91" + m.retailer.admin.mobile)
                            )
                        requireActivity().startActivity(intentDial)
                    }
                }
                else -> {
                    mainActivity?.addSubFragment(
                        TAG,
                        OrderDetailsActivity.newInstance(m.id, m.retailer.name)
                    )
                    /*if (m.status.equals("delivered", true)) {
                        val intent = CollectPaymentActivity.newIntent(requireActivity())
                        intent?.putExtra("order_id", m.id)
                        intent?.putExtra("retailer_name", m.retailer.name)
                        startActivity(intent)
                    } else {
                        val intent = OrderDetailsActivity.newIntent(requireActivity())
                        intent?.putExtra("order_id", m.id)
                        intent?.putExtra("store_name", m.retailer.name)
                        startActivity(intent)
                    }*/
                    /*val intent = OrderDetailsActivity.newIntent(requireActivity())
                    intent?.putExtra("order_id", m.id)
                    intent?.putExtra("store_name", m.retailer.name)
                    startActivity(intent)*/
                }
            }
        }

        mVerticalPagination = VerticalPagination(layoutManager, 3)
        mVerticalPagination.setListener(this)
        binding.rvOrders.addOnScrollListener(mVerticalPagination)

        binding.rvOrders.layoutManager = layoutManager
        binding.rvOrders.adapter = ordersAdapter
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            layoutManager.orientation
        )
        dividerItemDecoration.setDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.divider
                )
            )
        )
        binding.rvOrders.addItemDecoration(dividerItemDecoration)
        ordersAdapter?.list = getListData()
    }

    private fun getListData(): ArrayList<Results> {
        return ArrayList<Results>().apply {
            add(
                Results(
                    0.0,
                    0.0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    0,
                    Retailer(
                        "",
                        "",
                        Admin(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0,
                            "",
                            false,
                            false,
                            false,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        ""
                    ),
                    0,
                    0,
                    "",
                    "",
                    ""
                )
            )
            add(
                Results(
                    0.0,
                    0.0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    0,
                    Retailer(
                        "",
                        "",
                        Admin(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0,
                            "",
                            false,
                            false,
                            false,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        ""
                    ),
                    0,
                    0,
                    "",
                    "",
                    ""
                )
            )
            add(
                Results(
                    0.0,
                    0.0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    0,
                    Retailer(
                        "",
                        "",
                        Admin(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0,
                            "",
                            false,
                            false,
                            false,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        ""
                    ),
                    0,
                    0,
                    "",
                    "",
                    ""
                )
            )
            add(
                Results(
                    0.0,
                    0.0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    0,
                    Retailer(
                        "",
                        "",
                        Admin(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0,
                            "",
                            false,
                            false,
                            false,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        ""
                    ),
                    0,
                    0,
                    "",
                    "",
                    ""
                )
            )
            add(
                Results(
                    0.0,
                    0.0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    0,
                    0.0,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    0.0,
                    "",
                    0,
                    Retailer(
                        "",
                        "",
                        Admin(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            0,
                            "",
                            false,
                            false,
                            false,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                        ),
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        0,
                        false,
                        false,
                        false,
                        false,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        ""
                    ),
                    0,
                    0,
                    "",
                    "",
                    ""
                )
            )
        }
    }

    private var page = 1
    override fun onLoadMore() {
        viewModel.getTripList(getAuthorization(), page.toString())
    }

}