package com.rk_tech.riggle_runner.ui.main.pending.orderdetails

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.request_v2.EditProductRequest
import com.rk_tech.riggle_runner.data.model.request_v2.ProductEditData
import com.rk_tech.riggle_runner.data.model.response.*
import com.rk_tech.riggle_runner.data.model.response_v2.OrderDetailResponse
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrOrderDetails = SingleRequestEvent<OrderDetailResponse>()
    var obrOrderDelivery = SingleRequestEvent<DeliveryOrderResponse>()
    var obrEditProduct = SingleRequestEvent<ProductResponse>()
    var obrEditMixBox = SingleRequestEvent<List<ComboUpdateResponse>>()
    fun getOrderDetails(authorization: String, orderId: Int) {
        val query = HashMap<String, String>()
        query["build_edit_view"] = "true"
        query["expand"] = "buyer,products.product,products.free_product"
        //https://stag.api.riggleapp.in/api/v1/core/orders/429/?expand=service_hub%2Cproducts.product.banner_image%2Cproducts.free_product%2Cproducts.product_combo
        viewModelScope.launch {
            obrOrderDetails.postValue(Resource.loading(null))
            try {
                apiHelper.getOrderDetailsApi(authorization, orderId, query).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDetails.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDetails.postValue(
                            Resource.warn(
                                null, getErrorMessage(it.errorBody())
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrOrderDetails.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun deliveryOrder(authorization: String, orderId: Int, location: Location?) {
        val data = HashMap<String, String>()
        data["delivery_location"] =
            location?.latitude.toString() + "," + location?.longitude.toString()
        viewModelScope.launch {
            obrOrderDelivery.postValue(Resource.loading(null))
            try {
                apiHelper.deliveryOrder(authorization, orderId, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDelivery.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDelivery.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrOrderDelivery.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun editProductQty(authorization: String, orderId: Int, editProductRequest: EditProductRequest,) {
        /*val data = HashMap<String, String>()
        data["quantity"] = quantity.toString()*/
        //val prodData = ArrayList<ProductEditData>()
        //prodData.add(ProductEditData(productId, quantity, null))
        viewModelScope.launch {
            obrEditProduct.postValue(Resource.loading(null))
            try {
                apiHelper.editProductItem(authorization, orderId, editProductRequest)
                    .let {
                        if (it.isSuccessful) {
                            it.body()?.let { results ->
                                obrEditProduct.postValue(Resource.success(results, "Success"))
                            }
                        } else {
                            obrEditProduct.postValue(
                                Resource.warn(
                                    null,
                                    getErrorMessage(it.errorBody())
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrEditProduct.postValue(Resource.error(null, it))
                }
            }
        }
    }

    fun editComboProductItem(
        authorization: String,
        orderId: Int,
        requestComboUpdate: RequestComboUpdate
    ) {
        viewModelScope.launch {
            obrEditMixBox.postValue(Resource.loading(null))
            try {
                apiHelper.editComboProductItem(authorization, orderId, requestComboUpdate).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrEditMixBox.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrEditMixBox.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                parseException(e.cause)?.let {
                    obrEditMixBox.postValue(Resource.error(null, it))
                }
            }
        }
    }

}