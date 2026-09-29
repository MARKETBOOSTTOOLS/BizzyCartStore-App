package com.example.data.firebase

import android.util.Log
import com.example.data.model.AddressEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirebaseService {

    private val tag = "BizzyCartFirebase"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize Firestore", e)
            null
        }
    }

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize FirebaseAuth", e)
            null
        }
    }

    fun isConnected(): Boolean {
        return firestore != null
    }

    // Save a new order to Firebase Cloud Firestore
    suspend fun saveOrder(order: OrderEntity): Boolean {
        return try {
            val db = firestore ?: return false
            val orderMap = hashMapOf(
                "id" to order.id,
                "createdAt" to order.createdAt,
                "totalAmount" to order.totalAmount,
                "subtotal" to order.subtotal,
                "discountAmount" to order.discountAmount,
                "deliveryFee" to order.deliveryFee,
                "couponCode" to (order.couponCode ?: ""),
                "status" to order.status,
                "paymentMethod" to order.paymentMethod,
                "paymentStatus" to order.paymentStatus,
                "shippingName" to order.shippingName,
                "shippingPhone" to order.shippingPhone,
                "shippingAddress" to order.shippingAddress,
                "shippingCity" to order.shippingCity,
                "shippingState" to order.shippingState,
                "shippingPincode" to order.shippingPincode,
                "estimatedDelivery" to order.estimatedDelivery,
                "itemsSummary" to order.itemsSummary
            )
            db.collection("orders").document(order.id).set(orderMap).await()
            Log.d(tag, "Order ${order.id} saved to Cloud Firestore")
            true
        } catch (e: Exception) {
            Log.e(tag, "Error saving order to Firestore: ${e.message}")
            false
        }
    }

    // Update Order Status in Cloud
    suspend fun updateOrderStatus(orderId: String, status: String): Boolean {
        return try {
            val db = firestore ?: return false
            db.collection("orders").document(orderId)
                .update("status", status)
                .await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error updating order status in Firestore: ${e.message}")
            false
        }
    }

    // Save Customer Review to Firestore
    suspend fun saveReview(review: ReviewEntity): Boolean {
        return try {
            val db = firestore ?: return false
            val reviewMap = hashMapOf(
                "productId" to review.productId,
                "authorName" to review.authorName,
                "rating" to review.rating,
                "title" to review.title,
                "comment" to review.comment,
                "date" to review.date,
                "isVerifiedPurchase" to review.isVerifiedPurchase
            )
            db.collection("reviews").add(reviewMap).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error saving review to Firestore: ${e.message}")
            false
        }
    }

    // Push Product to Cloud Catalog
    suspend fun saveProduct(product: ProductEntity): Boolean {
        return try {
            val db = firestore ?: return false
            val productMap = hashMapOf(
                "id" to product.id,
                "name" to product.name,
                "category" to product.category,
                "brand" to product.brand,
                "price" to product.price,
                "mrp" to product.mrp,
                "discountPercent" to product.discountPercent,
                "rating" to product.rating,
                "reviewCount" to product.reviewCount,
                "imageUrl" to product.imageUrl,
                "description" to product.description,
                "features" to product.features,
                "specifications" to product.specifications,
                "inStock" to product.inStock,
                "stockQuantity" to product.stockQuantity,
                "badge" to product.badge,
                "isFeatured" to product.isFeatured,
                "isDealOfDay" to product.isDealOfDay,
                "isNewArrival" to product.isNewArrival
            )
            db.collection("products").document(product.id.toString()).set(productMap, SetOptions.merge()).await()
            true
        } catch (e: Exception) {
            Log.e(tag, "Error saving product to Firestore: ${e.message}")
            false
        }
    }

    // Push Entire Catalog to Firebase
    suspend fun syncAllCatalog(products: List<ProductEntity>, categories: List<CategoryEntity>): Int {
        var count = 0
        val db = firestore ?: return 0
        try {
            for (p in products) {
                saveProduct(p)
                count++
            }
            for (c in categories) {
                val catMap = hashMapOf(
                    "id" to c.id,
                    "name" to c.name,
                    "slug" to c.slug,
                    "imageUrl" to c.imageUrl,
                    "description" to c.description,
                    "productCount" to c.productCount,
                    "displayOrder" to c.displayOrder
                )
                db.collection("categories").document(c.slug).set(catMap, SetOptions.merge()).await()
            }
        } catch (e: Exception) {
            Log.e(tag, "Sync error: ${e.message}")
        }
        return count
    }

    // Fetch Products from Cloud
    suspend fun fetchCloudProducts(): List<ProductEntity> {
        return try {
            val db = firestore ?: return emptyList()
            val snapshot = db.collection("products").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    ProductEntity(
                        id = (doc.getLong("id") ?: 0).toInt(),
                        name = doc.getString("name") ?: "",
                        category = doc.getString("category") ?: "Electronics",
                        brand = doc.getString("brand") ?: "BizzyCart",
                        price = doc.getDouble("price") ?: 0.0,
                        mrp = doc.getDouble("mrp") ?: 0.0,
                        discountPercent = (doc.getLong("discountPercent") ?: 0).toInt(),
                        rating = (doc.getDouble("rating") ?: 4.5).toFloat(),
                        reviewCount = (doc.getLong("reviewCount") ?: 0).toInt(),
                        imageUrl = doc.getString("imageUrl") ?: "",
                        description = doc.getString("description") ?: "",
                        features = doc.getString("features") ?: "",
                        specifications = doc.getString("specifications") ?: "",
                        inStock = doc.getBoolean("inStock") ?: true,
                        stockQuantity = (doc.getLong("stockQuantity") ?: 20).toInt(),
                        badge = doc.getString("badge") ?: "",
                        isFeatured = doc.getBoolean("isFeatured") ?: false,
                        isDealOfDay = doc.getBoolean("isDealOfDay") ?: false,
                        isNewArrival = doc.getBoolean("isNewArrival") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching from Firestore: ${e.message}")
            emptyList()
        }
    }
}
