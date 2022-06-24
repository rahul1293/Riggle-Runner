package com.rk_tech.riggle_runner.ui.main.neworder.brand_category

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.BrandResults
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrandCategoryFragmentVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {
    var obrBrandList = SingleRequestEvent<List<Any>>()
    fun getBrandList(authorization: String, is_active: String) {
        val query = HashMap<String, String>()
        query["is_active"] = is_active
        viewModelScope.launch {
            obrBrandList.postValue(Resource.loading(null))
            try {
                apiHelper.getBrandList(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrBrandList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrBrandList.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrBrandList.postValue(Resource.error(null, it))
                }
            }
        }
    }

}