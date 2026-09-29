package com.example.ui.navigation

sealed class Screen {
    object Home : Screen()
    object Categories : Screen()
    data class ProductListing(
        val category: String? = null,
        val filterBadge: String? = null,
        val title: String = category ?: "All Products"
    ) : Screen()
    data class ProductDetail(val productId: Int) : Screen()
    data class Search(val initialQuery: String = "") : Screen()
    object Cart : Screen()
    object Checkout : Screen()
    data class OrderSuccess(val orderId: String) : Screen()
    object Orders : Screen()
    data class OrderDetail(val orderId: String) : Screen()
    object Wishlist : Screen()
    object Account : Screen()
    object AdminDashboard : Screen()
    object SellerDashboard : Screen()
}

enum class BottomTab {
    HOME,
    CATEGORIES,
    SEARCH,
    CART,
    ACCOUNT
}
