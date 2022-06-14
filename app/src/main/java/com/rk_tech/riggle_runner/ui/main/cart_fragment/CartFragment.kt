package com.rk_tech.riggle_runner.ui.main.cart_fragment

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.databinding.*
import com.rk_tech.riggle_runner.ui.base.*
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_call_deliverify.view.*

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>() {
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartFragment().apply {

            }
    }

    private var sheetAdapter: SimpleRecyclerViewAdapter<DummyData, ListCallDeliverifyBinding>? =
        null
    val viewModel: CartFragmentVM by viewModels()

    override fun getLayoutResource(): Int {
        return R.layout.fragment_cart
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    private var mainActivity: MainActivity? = null
    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        showBottomSheet()
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
        setUpRecyclerView()
    }

    private fun showBottomSheet() {
       /* val bottomSheet =
            BottomSheet<BottomSheetCallDeliverifyBinding>(R.layout.bottom_sheet_call_deliverify)
*/
        sheetAdapter =
            SimpleRecyclerViewAdapter<DummyData, ListCallDeliverifyBinding>(
                R.layout.list_call_deliverify, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlMain -> {
                    }

                }
            }
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        sheetAdapter?.list = dummyList
        bottomSheet?.binding?.rvContact?.adapter = sheetAdapter
        //   bottomSheet.show(parentFragmentManager, "")
    }

    private var bottomSheet: BaseCustomBottomSheet<BottomSheetCallDeliverifyBinding>? = null
    private fun inItBottomSheet() {
        bottomSheet =
            BaseCustomBottomSheet(
                requireActivity(), R.layout.bottom_sheet_call_deliverify
            ) {
                when (it.id) {
                    R.id.ivCancel -> {

                    }
                }
            }
        bottomSheet?.show()
    }

    @SuppressLint("SetTextI18n")
    private fun emptyCart(emptyCart: Boolean) {
        if (emptyCart) {
            binding.rvRetailerList.visibility = View.GONE
            binding.rlAmount.visibility = View.GONE
            binding.ivEmpty.visibility = View.VISIBLE
            binding.tvEmptyCart.visibility = View.VISIBLE
            binding.tvNext.text = "Add Item"
        } else {
            binding.rvRetailerList.visibility = View.VISIBLE
            binding.rlAmount.visibility = View.VISIBLE
            binding.tvEmptyCart.visibility = View.GONE
            binding.ivEmpty.visibility=View.GONE
            binding.tvNext.text = "Next"
        }
    }


    private var cartAdapter: SimpleRecyclerViewAdapter<DummyData, CartItemLayoutBinding>? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        cartAdapter = SimpleRecyclerViewAdapter<DummyData, CartItemLayoutBinding>(
            R.layout.cart_item_layout, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                }

            }
        }
        binding.rvRetailerList.layoutManager = layoutManager
        binding.rvRetailerList.adapter = cartAdapter
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        cartAdapter?.list = dummyList
    }
}