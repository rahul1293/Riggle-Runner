package com.rk_tech.riggle_runner.ui.main.neworder

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.data.model.response.RetailerDetails
import com.rk_tech.riggle_runner.databinding.*
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.cart_fragment.CartFragment
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.brand_category.BrandCategoryFragment
import com.rk_tech.riggle_runner.ui.main.neworder.create_retailer.CreateRetailerActivity
import com.rk_tech.riggle_runner.ui.main.search_store.SearchStoreFragment
import com.rk_tech.riggle_runner.utils.event.SingleLiveEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_call_deliverify.view.*
import kotlinx.android.synthetic.main.bs_create_mix.view.*

@AndroidEntryPoint
class NewOrderFragment : BaseFragment<FragmentNewOrdersBinding>() {
    val viewModel: NewOrderFragmentVM by viewModels()


    private var mainActivity: MainActivity? = null
    private var searchHandler: Handler = Handler(Looper.getMainLooper())

    companion object {
        var secondAdapterSet: Boolean = false
        val createMixClick = SingleLiveEvent<String>()
        fun newInstance(): Fragment {
            return NewOrderFragment()
        }

        val TAG = "NewOrderFragment"
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_new_orders
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        mainActivity = requireActivity() as MainActivity
        initMixAdapter()
        createMixClick.observe(requireActivity()) {
            val dialog =
                BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.bs_create_mix, null)
            val bt = view.findViewById<CardView>(R.id.card)
            view.rvMixture.adapter = mixtureAdpater!!
            bt.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }
        viewModel.onClick.observe(requireActivity()) {
            when (it.id) {
                R.id.card_cart -> {
                    mainActivity?.addSubFragment(TAG, CartFragment())
                }
                R.id.iv_back -> {
                    if (secondAdapterSet) {
                        changeAdpter()
                    } else {
                        mainActivity?.onBackPressed()
                    }

                }
                R.id.tvCreateNew -> {
                    mainActivity?.addSubFragment(TAG, SearchStoreFragment())
                }

            }
        }
        /*  val startForResult =
              registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                  if (result.resultCode == Activity.RESULT_OK) {
                      val intent = result.data
                      openSubFragment(
                          Gson().fromJson(
                              intent?.getStringExtra("data"),
                              RetailerDetails::class.java
                          )
                      )
                  }
              }
          viewModel.onClick.observe(viewLifecycleOwner) {
              when (it?.id) {
                  R.id.tvCreateNew -> {
                      val intent = CreateRetailerActivity.newIntent(requireActivity())
                      startForResult.launch(intent)
                  }
              }
          }

          viewModel.obrRetailerList.observe(viewLifecycleOwner, Observer {
              when (it?.status) {
                  Status.LOADING -> {
                      //showHideLoader(true)
                  }
                  Status.SUCCESS -> {
                      showHideLoader(false)
                    //  searchAdapter?.list = it.data
                  }
                  Status.WARN -> {
                      showHideLoader(false)
                  }
                  Status.ERROR -> {
                      showHideLoader(false)
                  }
              }
          })
  */
        //searchView()
        setUpProductAdatper()
        setUpRecyclerView()
    }

    private var mixtureAdpater: SimpleRecyclerViewAdapter<DummyData, ListOfMixtureBinding>? = null
    private fun initMixAdapter() {
        mixtureAdpater = SimpleRecyclerViewAdapter(R.layout.bs_create_mix, BR.bean) { i, v, pos ->
        }
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        mixtureAdpater?.list = dummyList


    }

    private fun openSubFragment(retailer: RetailerDetails?) {
        mainActivity?.addSubFragment(TAG, BrandCategoryFragment(retailer))

    }

    private fun searchView() {

        val searchIcon: ImageView =
            binding.etSearch.findViewById(androidx.appcompat.R.id.search_mag_icon)
        val searchCloseIcon: ImageView =
            binding.etSearch.findViewById(androidx.appcompat.R.id.search_close_btn)
        // To change color of close button, use:
        searchIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black_54),
            PorterDuff.Mode.SRC_IN
        )
        searchCloseIcon.setColorFilter(
            ContextCompat.getColor(requireContext(), R.color.black_54),
            PorterDuff.Mode.SRC_IN
        )

        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                binding.etSearch.clearFocus()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                if (s.isNotEmpty()) {
                    binding.rvRetailerList.visibility = View.VISIBLE
                } else {
                    searchAdapter?.clearList()
                    binding.rvRetailerList.visibility = View.GONE
                }
                searchHandler.removeCallbacksAndMessages(null)
                searchHandler.postDelayed({ onSearch(s) }, 500)
                return false
            }
        })
    }

    private fun onSearch(queryString: String) {
        if (queryString.length < 3) {
            return
        }
        //viewModel.getRetailerList(getAuthorization(), queryString)
    }

    // new product details adapter


    var dummyAdapter: SimpleRecyclerViewAdapter<DummyData, LayoutOrderDetailsBinding>? = null
    private fun setUpProductAdatper() {
        dummyAdapter = SimpleRecyclerViewAdapter<DummyData, LayoutOrderDetailsBinding>(
            R.layout.layout_order_details, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {

                }
            }
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvRetailerList.layoutManager = layoutManager
        binding.rvRetailerList.adapter = dummyAdapter
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyAdapter?.list = dummyList
    }


    var searchAdapter: SimpleRecyclerViewAdapter<DummyData, ListOfRetailersBinding>? = null
    private fun setUpRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        searchAdapter = SimpleRecyclerViewAdapter<DummyData, ListOfRetailersBinding>(
            R.layout.list_of_retailers, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.rlMain -> {
                    binding.tvCreateNew.visibility = View.VISIBLE
                    binding.rvRetailerList.adapter = dummyAdapter
                    secondAdapterSet = true
                }
            }
        }
        binding.rvRetailerList.layoutManager = layoutManager
        binding.rvRetailerList.adapter = searchAdapter
        val dummyList = ArrayList<DummyData>()
        dummyList.add(DummyData("", ""))
        dummyList.add(DummyData("", ""))
        searchAdapter?.list = dummyList
    }

    fun changeAdpter() {
        binding.rvRetailerList.adapter = searchAdapter
        secondAdapterSet = false
        binding.tvCreateNew.visibility = View.GONE
    }

}