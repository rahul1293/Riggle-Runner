package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response_v2.BrandOfferResponse
import com.rk_tech.riggle_runner.data.model.response_v2.OfferResult
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchemeBottomSheetVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {
    var obrBrandOffer = SingleRequestEvent<List<OfferResult>>()
    fun getBrandOffer(authorization: String, brandId: Int) {
        val data = HashMap<String, String>()
        data["brand"] = brandId.toString()
        viewModelScope.launch {
            obrBrandOffer.postValue(Resource.loading(null))
            try {
                apiHelper.getBrandOffer(authorization, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrBrandOffer.postValue(Resource.success(results.results, "Success"))
                        }
                    } else {
                        obrBrandOffer.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrBrandOffer.postValue(Resource.error(null, it))
                }
            }
        }
    }
}