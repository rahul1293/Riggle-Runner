package com.rk_tech.riggle_runner.ui.main.cart_fragment

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response_v2.CartResponse
import com.rk_tech.riggle_runner.data.model.response_v2.GetRetailsListItem
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartFragmentVM @Inject constructor(private val apiHelper: ApiHelper) : BaseViewModel() {

    var obrCartList = SingleRequestEvent<CartResponse>()
    fun getCartData(authorization: String) {
        val queryMap = HashMap<String, String>()
        queryMap["expand"] = "products.product"
        viewModelScope.launch {
            obrCartList.postValue(Resource.loading(null))
            try {
                //apiHelper.getServiceHubList(authorization, userId)
                apiHelper.getCartResponse(authorization, queryMap).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrCartList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrCartList.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrCartList.postValue(Resource.error(null, it))
                }
            }
        }
    }
}