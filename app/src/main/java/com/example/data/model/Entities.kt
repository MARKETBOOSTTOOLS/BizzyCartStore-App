package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val category: String,
    val brand: String,
    val price: Double,
    val mrp: Double,
    val discountPercent: Int,
    val rating: Float,
    val reviewCount: Int,
    val imageUrl: String,
    val additionalImages: String = "", // Comma-separated URLs
    val description: String,
    val features: String = "", // Newline-separated
    val specifications: String = "", // Key:Value separated by newline
    val inStock: Boolean = true,
    val stockQuantity: Int = 25,
    val badge: String = "", // "Best Seller", "Trending", "New", "Limited Stock", "Sale"
    val isFeatured: Boolean = false,
    val isDealOfDay: Boolean = false,
    val isNewArrival: Boolean = false,
    val sellerName: String = "BizzyCart Verified Seller"
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val slug: String,
    val iconName: String,
    val imageUrl: String,
    val description: String,
    val productCount: Int = 0,
    val displayOrder: Int = 0
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val quantity: Int = 1,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey
    val productId: Int,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String, // e.g. "BZY-981245"
    val createdAt: Long = System.currentTimeMillis(),
    val totalAmount: Double,
    val subtotal: Double,
    val discountAmount: Double,
    val deliveryFee: Double,
    val couponCode: String? = null,
    val status: String, // "Order Placed", "Confirmed", "Packed", "Shipped", "Out for Delivery", "Delivered"
    val paymentMethod: String,
    val paymentStatus: String = "Paid",
    val shippingName: String,
    val shippingPhone: String,
    val shippingAddress: String,
    val shippingCity: String,
    val shippingState: String,
    val shippingPincode: String,
    val estimatedDelivery: String,
    val itemsSummary: String // Formatted summary string
)

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val phone: String,
    val houseFlat: String,
    val street: String,
    val area: String,
    val city: String,
    val state: String,
    val pincode: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey
    val code: String,
    val discountPercent: Int,
    val maxDiscount: Double,
    val minOrderValue: Double,
    val description: String,
    val isActive: Boolean = true
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int,
    val authorName: String,
    val rating: Float,
    val title: String,
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean = true
)
