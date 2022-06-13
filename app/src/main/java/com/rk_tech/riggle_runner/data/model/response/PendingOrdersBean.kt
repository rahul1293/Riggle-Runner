package com.rk_tech.riggle_runner.data.model.response

data class PendingOrdersBean(
    val _id: String,
    val cartItems: List<CartItem>,
    val cookieMonsterId: String,
    val deliveryType: String,
    val orderId: String,
    val orderStatus: String,
    val payWith: String,
    val payableAmount: Double,
    val restaurantId: RestaurantId,
    val totalAmount: Double,
    val distance: String,
    val time: String,
    val totaltime: String,
    val cookieMonsterDetail: CookieMonsterDetail
)

data class CartItem(
    val _id: String,
    val additionalComments: String,
    val amount: Double,
    val choices: List<String>,
    val extras: List<Extra>,
    val image: String,
    val menuCategoryId: String,
    val menuItemId: String,
    val name: String,
    val quantity: Int
)

data class RestaurantId(
    val __v: Int,
    val _id: String,
    val address: Address,
    val avgRatings: Int,
    val bannerImage: List<String>,
    val closingTime: String,
    val deliveryCharge: Int,
    val deliveryTime: String,
    val deliveryType: List<String>,
    val foodType: List<String>,
    val isActive: Int,
    val lastOrderTime: String,
    val logo: String,
    val maxOrderValue: Int,
    val mealType: List<String>,
    val minOrderValue: Int,
    val name: String,
    val openingTime: String,
    val phoneNumber: Long,
    val totalReviews: Int
)

data class Extra(
    val _id: String,
    val amount: Int,
    val calories: Int,
    val name: String
)

data class Address(
    val city: String,
    val country: String,
    val location: Location,
    val postalCode: Int,
    val street: String
)

data class Location(
    val coordinates: List<Double>,
    val type: String
)

data class CookieMonsterDetail(
    val _id: String,
    val address: List<Addres>,
    val firstName: String,
    val lastName: String
)

data class Addres(
    val _id: String,
    val addressLine1: String,
    val addressLine2: String,
    val isDefault: Boolean,
    val location: Location,
    val nickName: String,
    val phoneNumber: Long
)