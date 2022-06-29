package com.rk_tech.riggle_runner.ui.main.neworder.product_list

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request.OrderRequest
import com.rk_tech.riggle_runner.data.model.request_v2.EditProductRequest
import com.rk_tech.riggle_runner.data.model.response.BrandResults
import com.rk_tech.riggle_runner.data.model.response.DeliveryOrderResponse
import com.rk_tech.riggle_runner.data.model.response.ProductList
import com.rk_tech.riggle_runner.data.model.response_v2.AddCartResponse
import com.rk_tech.riggle_runner.data.model.response_v2.CartResponse
import com.rk_tech.riggle_runner.data.model.response_v2.ProductResult
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrProductList = SingleRequestEvent<List<ProductResult>>()
    var obrPlaceOrder = SingleRequestEvent<List<DeliveryOrderResponse>>()
    var obrCartList = SingleRequestEvent<CartResponse>()
    var obrCartUpdate = SingleRequestEvent<AddCartResponse>()

    fun getProductList(authorization: String, brand_in: String) {
        val query = HashMap<String, String>()
        query["brand"] = brand_in
        query["expand"] = "combo_products.products"
        //query["is_active"] = "true"
        viewModelScope.launch {
            obrProductList.postValue(Resource.loading(null))
            try {
                apiHelper.getBrandProductList(authorization, query).let {
                    if (it.isSuccessful) {
                        it.body()?.results?.let { results ->
                            obrProductList.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrProductList.postValue(Resource.warn(null, getErrorMessage(it.errorBody())))
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

    fun updateCartProduct(authorization: String, id: Int, editRequest: EditProductRequest) {
        viewModelScope.launch {
            obrCartUpdate.postValue(Resource.loading(null))
            try {
                //apiHelper.getServiceHubList(authorization, userId)
                apiHelper.addCartProduct(authorization, id,editRequest).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrCartUpdate.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrCartUpdate.postValue(
                            Resource.warn(
                                null,
                                getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrCartUpdate.postValue(Resource.error(null, it))
                }
            }
        }
    }

}