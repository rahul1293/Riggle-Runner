package com.rk_tech.riggle_runner.ui.main.neworder

import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.RetailerDetails
import com.rk_tech.riggle_runner.data.model.response_v2.BrandResult
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.Coroutines
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrRetailerList = SingleRequestEvent<List<RetailerDetails>>()
    var obrBrandData = SingleRequestEvent<List<BrandResult>>()

    fun getRetailerList(authorization: String, queryString: String) {
        val query = HashMap<String, String>()
        query["search"] = queryString
        Coroutines.io {
            obrRetailerList.postValue(Resource.loading(null))
            try {
                apiHelper.searchRetailers(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrRetailerList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrRetailerList.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrRetailerList.postValue(Resource.error(null, it))
                }
            }
        }
    }


    fun getBrandList(authorization: String) {
        val query = HashMap<String, String>()
        query["expand"] = "~all"
        Coroutines.io {
            obrBrandData.postValue(Resource.loading(null))
            try {
                apiHelper.getBrandList(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrBrandData.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrBrandData.postValue(Resource.warn(null, it.message()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrBrandData.postValue(Resource.error(null, it))
                }
            }
        }
    }
}