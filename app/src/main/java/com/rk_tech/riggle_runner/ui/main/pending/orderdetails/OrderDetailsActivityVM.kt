package com.rk_tech.riggle_runner.ui.main.pending.orderdetails

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.rk_tech.riggle_runner.data.api.ApiHelper
import com.rk_tech.riggle_runner.data.model.helper.Resource
import com.rk_tech.riggle_runner.data.model.response.*
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.utils.event.SingleRequestEvent
import com.rk_tech.riggle_runner.utils.extension.parseException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsActivityVM @Inject constructor(private val apiHelper: ApiHelper) :
    BaseViewModel() {

    var obrOrderDetails = SingleRequestEvent<OrderDetailsResponse>()
    var obrOrderDelivery = SingleRequestEvent<DeliveryOrderResponse>()
    var obrEditProduct = SingleRequestEvent<ProductResponse>()
    var obrEditMixBox = SingleRequestEvent<List<ComboUpdateResponse>>()
    fun getOrderDetails(authorization: String, orderId: Int) {
        val query = HashMap<String, String>()
        //query["expand"] = "service_hub,products.product,products.free_product"
        query["expand"] =
            "service_hub,products.product.banner_image,products.free_product,products.product_combo.products"
        query["edit_view"] = "true"
        //data.put("expand", "schemes.free_product,banner_image,combo_products.products,service_hub")
        //https://stag.api.riggleapp.in/api/v1/core/orders/429/?expand=service_hub%2Cproducts.product.banner_image%2Cproducts.free_product%2Cproducts.product_combo
        viewModelScope.launch {
            obrOrderDetails.postValue(Resource.loading(null))
            try {
                apiHelper.getOrderDetails(authorization, orderId, query).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrOrderDetails.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrOrderDetails.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
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

    fun editProductQty(authorization: String, orderId: Int, productId: Int, quantity: Int) {
        val data = HashMap<String, String>()
        data["quantity"] = quantity.toString()
        viewModelScope.launch {
            obrEditProduct.postValue(Resource.loading(null))
            try {
                apiHelper.editProductItem(authorization, orderId, productId, data).let {
                    if (it.isSuccessful) {
                        it.body()?.let { results ->
                            obrEditProduct.postValue(Resource.success(results, "Success"))
                        }
                    } else {
                        obrEditProduct.postValue(
                            Resource.warn(
                                null,
                                it.errorBody()?.string().toString()
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