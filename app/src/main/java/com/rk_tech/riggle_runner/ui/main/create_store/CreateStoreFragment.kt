package com.rk_tech.riggle_runner.ui.main.create_store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.databinding.FragmentCreateStoreBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.NewOrderFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateStoreFragment : BaseFragment<FragmentCreateStoreBinding>() {


    private var mainActivity: MainActivity? = null
    private val viewModel: CreateStoreVM by viewModels()

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateStoreFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_create_store
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        mainActivity = activity as MainActivity
        viewModel.onClick.observe(requireActivity()) {
            when (it.id) {
                R.id.card_cart -> {
                    mainActivity?.addSubFragment(NewOrderFragment.TAG, CartFragment())
                }
                R.id.iv_back -> {
                    mainActivity?.onBackPressed()

                }
                R.id.tvNewStore -> {
                    val dialog =
                        BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
                    val view = layoutInflater.inflate(R.layout.bs_order_created, null)
                    val bt = view.findViewById<TextView>(R.id.tvCollectPayment)
                    bt.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.setCancelable(true)
                    dialog.setContentView(view)
                    dialog.show()
                }
            }

        }
    }
}