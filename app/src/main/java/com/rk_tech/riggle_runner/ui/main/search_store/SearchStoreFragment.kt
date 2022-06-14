package com.rk_tech.riggle_runner.ui.main.search_store


import android.view.View
import androidx.fragment.app.viewModels
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.DummyData
import com.rk_tech.riggle_runner.databinding.FragmentSearchStoreBinding
import com.rk_tech.riggle_runner.databinding.ListOfSuggestedBinding
import com.rk_tech.riggle_runner.ui.base.BaseFragment
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchStoreFragment : BaseFragment<FragmentSearchStoreBinding>() {

    private val viewModel: SearchStoreVM by viewModels()

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchStoreFragment().apply {

            }
    }

    override fun getLayoutResource(): Int {
        return R.layout.fragment_search_store
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        setUpRecyclerView()
    }

    private var suggestedAdapter: SimpleRecyclerViewAdapter<DummyData, ListOfSuggestedBinding>? =
        null

    private fun setUpRecyclerView() {
        try {
            suggestedAdapter = SimpleRecyclerViewAdapter<DummyData, ListOfSuggestedBinding>(
                R.layout.list_of_suggested, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.rlMain -> {
                    }

                }
                /*  val layout = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                  binding.rvSuggested.layoutManager = layout*/
                val dummyList = ArrayList<DummyData>()
                dummyList.add(DummyData("", ""))
                dummyList.add(DummyData("", ""))
                suggestedAdapter?.list = dummyList
                binding.rvSuggested.adapter = suggestedAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}


