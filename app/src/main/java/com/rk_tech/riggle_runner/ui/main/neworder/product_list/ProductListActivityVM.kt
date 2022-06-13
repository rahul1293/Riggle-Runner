package com.rk_tech.riggle_runner.ui.main.neworder.product_list

import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.response.BrandResults
import com.rk_tech.riggle_runner.data.model.response.DeliveryOrderResponse
import com.rk_tech.riggle_runner.data.model.response.ProductList
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrProductList = SingleRequestEvent<List<ProductList>>()
    var obrPlaceOrder = SingleRequestEvent<List<DeliveryOrderResponse>>()
    fun getProductList(authorization: String, brand_in: String) {
        val query = HashMap<String, String>()
        query["brand__in"] = brand_in
        query["expand"] = "banner_image,schemes.free_product,combo_products.products,service_hub"
        query["is_active"] = "true"
        viewModelScope.launch {
            obrProductList.postValue(Resource.loading(null))
            try {
                apiHelper.getProductList(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrProductList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrProductList.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrProductList.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun orderProduct(authorization: String, orderRequest: OrderRequest) {
        obrPlaceOrder
        viewModelScope.launch {
            obrPlaceOrder.postValue(Resource.loading(null))
            try {
                apiHelper.placeOrderByRunner(authorization, orderRequest).let {
                    if (it.isSuccessful) {
                        it.body().let { results ->
                            obrPlaceOrder.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrPlaceOrder.postValue(Resource.warn(null, it.errorBody()?.string().toString()))
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrPlaceOrder.postValue(Resource.error(null, it))
                }
            }
        }
    }

}