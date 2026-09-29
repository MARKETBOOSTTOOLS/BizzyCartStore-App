package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AddressEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.repository.BizzyCartRepository
import com.example.data.repository.CartItemWithProduct
import com.example.ui.navigation.BottomTab
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class UserProfile(
    val name: String = "Aarav Sharma",
    val email: String = "aarav.sharma@example.com",
    val phone: String = "+91 98765 43210",
    val isLoggedIn: Boolean = true,
    val isSeller: Boolean = false,
    val sellerStoreName: String = "Sharma Electronics & Lifestyle"
)

enum class SortOption(val label: String) {
    RELEVANCE("Relevance"),
    POPULARITY("Most Popular"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    HIGHEST_RATED("Customer Rating"),
    NEWEST("New Arrivals")
}

data class FilterState(
    val category: String? = null,
    val minRating: Float? = null,
    val maxPrice: Double? = null,
    val inStockOnly: Boolean = false,
    val brand: String? = null,
    val sortOption: SortOption = SortOption.RELEVANCE
)

class BizzyCartViewModel(application: Application) : AndroidViewModel(application) {

    val repository: BizzyCartRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BizzyCartRepository(database)
        viewModelScope.launch {
            repository.initializeIfEmpty()
        }
    }

    // Navigation BackStack
    private val _backStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home)
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val activeScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(BottomTab.HOME)
    val currentTab: StateFlow<BottomTab> = _currentTab.asStateFlow()

    // Location / Pincode
    private val _deliveryPincode = MutableStateFlow("110001")
    val deliveryPincode: StateFlow<String> = _deliveryPincode.asStateFlow()

    private val _deliveryCity = MutableStateFlow("New Delhi")
    val deliveryCity: StateFlow<String> = _deliveryCity.asStateFlow()

    // User Profile / Auth
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    // Cart coupon
    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    // Filter & Search
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _recentSearches = MutableStateFlow(
        listOf("Wireless Earbuds", "Travel Pillow", "Air Fryer", "Face Serum", "Trolley Bag")
    )
    val recentSearches: StateFlow<List<String>> = _recentSearches.asStateFlow()

    // Quick View
    private val _quickViewProduct = MutableStateFlow<ProductEntity?>(null)
    val quickViewProduct: StateFlow<ProductEntity?> = _quickViewProduct.asStateFlow()

    // Notifications / Toasts
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    // Repository Flows
    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemWithProduct>> = repository.getCartItemsWithProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartCount: StateFlow<Int> = repository.cartTotalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val wishlistProducts: StateFlow<List<ProductEntity>> = repository.getWishlistProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val addresses: StateFlow<List<AddressEntity>> = repository.getAllAddresses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons: StateFlow<List<CouponEntity>> = repository.getAllCoupons()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation Methods
    fun navigateTo(screen: Screen) {
        val current = _backStack.value
        if (current.lastOrNull() != screen) {
            _backStack.value = current + screen
            _currentScreen.value = screen
            updateTabForScreen(screen)
        }
    }

    fun popBackStack(): Boolean {
        val current = _backStack.value
        if (current.size > 1) {
            val updated = current.dropLast(1)
            _backStack.value = updated
            val newScreen = updated.last()
            _currentScreen.value = newScreen
            updateTabForScreen(newScreen)
            return true
        }
        return false
    }

    fun switchTab(tab: BottomTab) {
        _currentTab.value = tab
        val screen = when (tab) {
            BottomTab.HOME -> Screen.Home
            BottomTab.CATEGORIES -> Screen.Categories
            BottomTab.SEARCH -> Screen.Search()
            BottomTab.CART -> Screen.Cart
            BottomTab.ACCOUNT -> Screen.Account
        }
        navigateTo(screen)
    }

    private fun updateTabForScreen(screen: Screen) {
        _currentTab.value = when (screen) {
            is Screen.Home -> BottomTab.HOME
            is Screen.Categories -> BottomTab.CATEGORIES
            is Screen.Search -> BottomTab.SEARCH
            is Screen.Cart, is Screen.Checkout, is Screen.OrderSuccess -> BottomTab.CART
            is Screen.Account, is Screen.Orders, is Screen.OrderDetail, is Screen.Wishlist,
            is Screen.AdminDashboard, is Screen.SellerDashboard -> BottomTab.ACCOUNT
            else -> _currentTab.value
        }
    }

    // Quick View
    fun openQuickView(product: ProductEntity) {
        _quickViewProduct.value = product
    }

    fun closeQuickView() {
        _quickViewProduct.value = null
    }

    // Delivery Pincode
    fun setDeliveryPincode(pincode: String, city: String) {
        _deliveryPincode.value = pincode
        _deliveryCity.value = city
        showToast("Delivery address updated to $city ($pincode)")
    }

    // Cart Operations
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product.id, quantity)
            _toastMessage.emit("Added ${product.name.take(25)}... to cart!")
        }
    }

    fun updateCartQuantity(productId: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, quantity)
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            _toastMessage.emit("Item removed from cart")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Wishlist Operations
    fun toggleWishlist(product: ProductEntity, isCurrentlyWishlisted: Boolean) {
        viewModelScope.launch {
            repository.toggleWishlist(product.id, isCurrentlyWishlisted)
            val msg = if (isCurrentlyWishlisted) "Removed from wishlist" else "Added to wishlist"
            _toastMessage.emit(msg)
        }
    }

    fun moveWishlistToCart(product: ProductEntity) {
        viewModelScope.launch {
            repository.addToCart(product.id, 1)
            repository.toggleWishlist(product.id, true)
            _toastMessage.emit("Moved to cart!")
        }
    }

    // Coupons
    fun applyCoupon(code: String, subtotal: Double) {
        viewModelScope.launch {
            val coupon = repository.validateCoupon(code)
            if (coupon == null) {
                _toastMessage.emit("Invalid coupon code")
            } else if (subtotal < coupon.minOrderValue) {
                _toastMessage.emit("Minimum order value for ${coupon.code} is ₹${coupon.minOrderValue.toInt()}")
            } else {
                _appliedCoupon.value = coupon
                _toastMessage.emit("Coupon ${coupon.code} applied successfully!")
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        showToast("Coupon removed")
    }

    // Checkout & Order Placement
    fun placeOrder(
        shippingAddress: AddressEntity,
        paymentMethod: String,
        deliveryOption: String = "Standard Delivery"
    ): String {
        val cart = cartItems.value
        if (cart.isEmpty()) return ""

        val subtotal = cart.sumOf { it.product.price * it.cartItem.quantity }
        val discount = _appliedCoupon.value?.let { coupon ->
            val calc = (subtotal * coupon.discountPercent) / 100.0
            calc.coerceAtMost(coupon.maxDiscount)
        } ?: 0.0

        val deliveryFee = if (deliveryOption.contains("Express")) 49.0 else if (subtotal >= 499) 0.0 else 40.0
        val total = (subtotal - discount + deliveryFee).coerceAtLeast(0.0)

        val randomId = "BZY-${Random.nextInt(100000, 999999)}"
        val summary = cart.joinToString(", ") { "${it.product.name.take(24)} x ${it.cartItem.quantity}" }

        val estDelivery = SimpleDateFormat("EEEE, d MMM", Locale.getDefault()).format(
            Date(System.currentTimeMillis() + if (deliveryOption.contains("Express")) 86400000L else 86400000L * 3)
        )

        val order = OrderEntity(
            id = randomId,
            createdAt = System.currentTimeMillis(),
            totalAmount = total,
            subtotal = subtotal,
            discountAmount = discount,
            deliveryFee = deliveryFee,
            couponCode = _appliedCoupon.value?.code,
            status = "Order Placed",
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "Cash on Delivery") "Pending (COD)" else "Paid",
            shippingName = shippingAddress.fullName,
            shippingPhone = shippingAddress.phone,
            shippingAddress = "${shippingAddress.houseFlat}, ${shippingAddress.street}, ${shippingAddress.area}",
            shippingCity = shippingAddress.city,
            shippingState = shippingAddress.state,
            shippingPincode = shippingAddress.pincode,
            estimatedDelivery = estDelivery,
            itemsSummary = summary
        )

        viewModelScope.launch {
            repository.placeOrder(order)
            _appliedCoupon.value = null
            _toastMessage.emit("Order placed successfully! Order ID: $randomId")
        }

        return randomId
    }

    // Filter & Search
    fun updateSearchQuery(query: String) {
        if (query.isNotBlank()) {
            val list = _recentSearches.value.toMutableList()
            list.remove(query)
            list.add(0, query)
            _recentSearches.value = list.take(8)
        }
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun updateFilter(filter: FilterState) {
        _filterState.value = filter
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // Addresses
    fun saveAddress(address: AddressEntity) {
        viewModelScope.launch {
            if (address.id == 0) {
                repository.addAddress(address)
                _toastMessage.emit("New delivery address added")
            } else {
                repository.updateAddress(address)
                _toastMessage.emit("Address updated")
            }
        }
    }

    fun setDefaultAddress(id: Int) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
            _toastMessage.emit("Default address updated")
        }
    }

    fun deleteAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.deleteAddress(address)
            _toastMessage.emit("Address deleted")
        }
    }

    // Reviews
    fun addReview(productId: Int, rating: Float, title: String, comment: String) {
        viewModelScope.launch {
            val dateStr = SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date())
            val review = ReviewEntity(
                productId = productId,
                authorName = _userProfile.value.name,
                rating = rating,
                title = title,
                comment = comment,
                date = dateStr,
                isVerifiedPurchase = true
            )
            repository.addReview(review)
            _toastMessage.emit("Thank you! Your verified review has been posted.")
        }
    }

    // Admin & Seller methods
    fun adminUpdateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            _toastMessage.emit("Order $orderId status updated to $newStatus")
        }
    }

    fun adminAddProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.addProduct(product)
            _toastMessage.emit("Product '${product.name}' added to catalog")
        }
    }

    fun adminDeleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _toastMessage.emit("Product '${product.name}' removed")
        }
    }

    fun adminAddCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.addCategory(category)
            _toastMessage.emit("Category '${category.name}' created")
        }
    }

    fun adminDeleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _toastMessage.emit("Category '${category.name}' removed")
        }
    }

    fun registerAsSeller(storeName: String) {
        _userProfile.value = _userProfile.value.copy(
            isSeller = true,
            sellerStoreName = storeName
        )
        showToast("Congratulations! You are now a registered seller on BizzyCart.")
    }

    fun syncFirebaseDatabase() {
        viewModelScope.launch {
            _toastMessage.emit("Connecting to Firebase Cloud Firestore...")
            val count = repository.syncWithFirebase()
            _toastMessage.emit("Firebase Database Connected! Synchronized with Cloud Firestore.")
        }
    }

    // Auth & Profile
    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    fun login(email: String, name: String) {
        _userProfile.value = _userProfile.value.copy(
            isLoggedIn = true,
            email = email,
            name = name.ifBlank { "BizzyCart Shopper" }
        )
        _showAuthDialog.value = false
        showToast("Welcome back, ${_userProfile.value.name}!")
    }

    fun logout() {
        _userProfile.value = _userProfile.value.copy(isLoggedIn = false)
        showToast("Logged out successfully")
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }
}
