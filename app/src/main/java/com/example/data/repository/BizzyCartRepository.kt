package com.example.data.repository

import com.example.data.firebase.FirebaseService
import com.example.data.initial.InitialData
import com.example.data.local.AppDatabase
import com.example.data.model.AddressEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

data class CartItemWithProduct(
    val cartItem: CartItemEntity,
    val product: ProductEntity
)

class BizzyCartRepository(private val database: AppDatabase) {

    private val productDao = database.productDao()
    private val categoryDao = database.categoryDao()
    private val cartDao = database.cartDao()
    private val wishlistDao = database.wishlistDao()
    private val orderDao = database.orderDao()
    private val addressDao = database.addressDao()
    private val couponDao = database.couponDao()
    private val reviewDao = database.reviewDao()

    val firebaseService = FirebaseService()

    suspend fun initializeIfEmpty() = withContext(Dispatchers.IO) {
        if (productDao.getProductCount() == 0) {
            productDao.insertProducts(InitialData.products)
        }
        if (categoryDao.getCategoryCount() == 0) {
            categoryDao.insertCategories(InitialData.categories)
        }
        val coupons = couponDao.getCouponByCode("WELCOME10")
        if (coupons == null) {
            couponDao.insertCoupons(InitialData.coupons)
        }
        // Initialize addresses and orders if not present
        if (InitialData.addresses.isNotEmpty()) {
            for (addr in InitialData.addresses) {
                addressDao.insertAddress(addr)
            }
        }
        if (orderDao.getOrderCount() == 0) {
            for (order in InitialData.orders) {
                orderDao.insertOrder(order)
            }
        }
        if (InitialData.reviews.isNotEmpty()) {
            reviewDao.insertReviews(InitialData.reviews)
        }

        // Initialize Firebase Firestore collections
        try {
            firebaseService.syncAllCatalog(InitialData.products, InitialData.categories)
        } catch (_: Exception) {}
    }

    // Products
    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()
    fun getProductById(id: Int): Flow<ProductEntity?> = productDao.getProductById(id)
    suspend fun getProductByIdDirect(id: Int): ProductEntity? = productDao.getProductByIdDirect(id)
    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> = productDao.getProductsByCategory(category)
    fun getTrendingProducts(): Flow<List<ProductEntity>> = productDao.getTrendingProducts()
    fun getDealsOfDay(): Flow<List<ProductEntity>> = productDao.getDealsOfDay()
    fun getBestSellers(): Flow<List<ProductEntity>> = productDao.getBestSellers()
    fun getNewArrivals(): Flow<List<ProductEntity>> = productDao.getNewArrivals()
    fun searchProducts(query: String): Flow<List<ProductEntity>> = productDao.searchProducts(query)

    suspend fun addProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        val id = productDao.insertProduct(product)
        firebaseService.saveProduct(product.copy(id = id.toInt()))
        id
    }
    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
        firebaseService.saveProduct(product)
    }
    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    // Categories
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    suspend fun addCategory(category: CategoryEntity): Long = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    // Cart
    val cartTotalCount: Flow<Int> = cartDao.getCartTotalCount()

    fun getCartItemsWithProducts(): Flow<List<CartItemWithProduct>> {
        return combine(cartDao.getAllCartItems(), productDao.getAllProducts()) { cartItems, products ->
            val productMap = products.associateBy { it.id }
            cartItems.mapNotNull { cartItem ->
                productMap[cartItem.productId]?.let { product ->
                    CartItemWithProduct(cartItem, product)
                }
            }
        }
    }

    suspend fun addToCart(productId: Int, quantity: Int = 1) = withContext(Dispatchers.IO) {
        val existing = cartDao.getCartItemByProductId(productId)
        if (existing != null) {
            cartDao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            cartDao.insertCartItem(CartItemEntity(productId = productId, quantity = quantity))
        }
    }

    suspend fun updateCartQuantity(productId: Int, quantity: Int) = withContext(Dispatchers.IO) {
        if (quantity <= 0) {
            cartDao.deleteCartItemByProductId(productId)
        } else {
            val existing = cartDao.getCartItemByProductId(productId)
            if (existing != null) {
                cartDao.updateCartItem(existing.copy(quantity = quantity))
            }
        }
    }

    suspend fun removeFromCart(productId: Int) = withContext(Dispatchers.IO) {
        cartDao.deleteCartItemByProductId(productId)
    }

    suspend fun clearCart() = withContext(Dispatchers.IO) {
        cartDao.clearCart()
    }

    // Wishlist
    fun getWishlistProducts(): Flow<List<ProductEntity>> {
        return combine(wishlistDao.getAllWishlistItems(), productDao.getAllProducts()) { wishlistItems, products ->
            val wishlistedIds = wishlistItems.map { it.productId }.toSet()
            products.filter { it.id in wishlistedIds }
        }
    }

    fun isWishlisted(productId: Int): Flow<Boolean> = wishlistDao.isWishlisted(productId)

    suspend fun toggleWishlist(productId: Int, isWishlisted: Boolean) = withContext(Dispatchers.IO) {
        if (isWishlisted) {
            wishlistDao.deleteWishlistItem(productId)
        } else {
            wishlistDao.insertWishlistItem(WishlistItemEntity(productId = productId))
        }
    }

    // Orders
    fun getAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()
    fun getOrderById(orderId: String): Flow<OrderEntity?> = orderDao.getOrderById(orderId)
    suspend fun placeOrder(order: OrderEntity) = withContext(Dispatchers.IO) {
        orderDao.insertOrder(order)
        cartDao.clearCart()
        firebaseService.saveOrder(order)
    }
    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
        firebaseService.updateOrderStatus(orderId, status)
    }
    suspend fun getOrderCount(): Int = orderDao.getOrderCount()
    suspend fun getTotalRevenue(): Double = orderDao.getTotalRevenue()

    // Addresses
    fun getAllAddresses(): Flow<List<AddressEntity>> = addressDao.getAllAddresses()
    suspend fun addAddress(address: AddressEntity): Long = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            addressDao.clearDefaults()
        }
        addressDao.insertAddress(address)
    }
    suspend fun updateAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            addressDao.clearDefaults()
        }
        addressDao.updateAddress(address)
    }
    suspend fun setDefaultAddress(id: Int) = withContext(Dispatchers.IO) {
        addressDao.clearDefaults()
        addressDao.setDefaultAddress(id)
    }
    suspend fun deleteAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        addressDao.deleteAddress(address)
    }

    // Coupons
    fun getAllCoupons(): Flow<List<CouponEntity>> = couponDao.getAllCoupons()
    suspend fun validateCoupon(code: String): CouponEntity? = withContext(Dispatchers.IO) {
        couponDao.getCouponByCode(code.trim().uppercase())
    }
    suspend fun addCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        couponDao.insertCoupon(coupon)
    }
    suspend fun deleteCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        couponDao.deleteCoupon(coupon)
    }

    // Reviews
    fun getReviewsForProduct(productId: Int): Flow<List<ReviewEntity>> = reviewDao.getReviewsForProduct(productId)
    suspend fun addReview(review: ReviewEntity) = withContext(Dispatchers.IO) {
        reviewDao.insertReview(review)
        firebaseService.saveReview(review)
    }

    // Cloud Database Sync
    suspend fun syncWithFirebase(): Int = withContext(Dispatchers.IO) {
        firebaseService.syncAllCatalog(InitialData.products, InitialData.categories)
    }
}
