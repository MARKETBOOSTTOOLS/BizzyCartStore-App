package com.example.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.model.ProductEntity
import com.example.data.model.ReviewEntity
import com.example.data.repository.BizzyCartRepository
import com.example.ui.components.ProductCard
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGold
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyRed
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    allProducts: List<ProductEntity>,
    wishlistProducts: List<ProductEntity>,
    currentPincode: String,
    repository: BizzyCartRepository,
    onAddToCart: (ProductEntity, Int) -> Unit,
    onBuyNow: (ProductEntity, Int) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddReview: (productId: Int, rating: Float, title: String, comment: String) -> Unit,
    onShowToast: (String) -> Unit
) {
    val isWishlisted = remember(wishlistProducts, product) {
        wishlistProducts.any { it.id == product.id }
    }

    val reviews by repository.getReviewsForProduct(product.id)
        .collectAsState(initial = emptyList())

    val relatedProducts = remember(allProducts, product) {
        allProducts.filter { it.category == product.category && it.id != product.id }.take(6)
    }

    var quantity by remember { mutableIntStateOf(1) }
    var enteredPincode by remember { mutableStateOf(currentPincode) }
    var pincodeStatus by remember {
        mutableStateOf("Delivery by Tomorrow, 5 PM | Free Delivery (Standard)")
    }

    // Image gallery list
    val imageList = remember(product) {
        val list = mutableListOf(product.imageUrl)
        if (product.additionalImages.isNotBlank()) {
            list.addAll(product.additionalImages.split(",").map { it.trim() }.filter { it.isNotEmpty() })
        }
        list
    }
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    var showAddReviewDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
                .background(Color(0xFFF8FAFC))
                .testTag("product_detail_screen")
        ) {
            // Main Product Image Preview
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f)
                        .background(Color.White)
                ) {
                    AsyncImage(
                        model = imageList.getOrNull(selectedImageIndex) ?: product.imageUrl,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Wishlist toggle button
                    IconButton(
                        onClick = { onToggleWishlist(product) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .testTag("detail_wishlist_button")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isWishlisted) BizzyRed else BizzyTextMuted,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Badge
                    if (product.badge.isNotBlank()) {
                        Surface(
                            color = if (product.badge == "Best Seller") BizzyGold else BizzyAccent,
                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = product.badge,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Thumbnail Strip
                if (imageList.size > 1) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                    ) {
                        items(imageList.indices.toList()) { index ->
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        2.dp,
                                        if (selectedImageIndex == index) BizzyPrimary else BizzyBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedImageIndex = index }
                            ) {
                                AsyncImage(
                                    model = imageList[index],
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            }

            // Title, Brand & Ratings Card
            item {
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Brand: ${product.brand}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = BizzyPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Surface(
                                color = BizzyGreenLight,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = BizzyGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "In Stock",
                                        color = BizzyGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating row
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = BizzyGreen,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "%.1f".format(product.rating),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.reviewCount} Ratings & ${reviews.size + 14} Reviews",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BizzyTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = BizzyBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Price & Savings
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "₹${product.price.toInt()}",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BizzyTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "MRP ₹${product.mrp.toInt()}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = BizzyTextMuted,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${product.discountPercent}% OFF",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = BizzyGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Text(
                            text = "Inclusive of all taxes (GST included). Free delivery across India.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BizzyTextSecondary,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Available Offers Card
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = BizzyAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Available Offers",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OfferItem(
                            title = "Bank Offer",
                            description = "10% Instant Discount up to ₹500 on ICICI & HDFC Bank Cards."
                        )
                        OfferItem(
                            title = "Special BizzyCart Coupon",
                            description = "Use code 'FIRSTORDER' to get ₹150 off on orders above ₹799."
                        )
                        OfferItem(
                            title = "UPI Payment Offer",
                            description = "Extra 5% cashback when paying via Google Pay or PhonePe."
                        )
                    }
                }
            }

            // Indian PIN Code Delivery Checker
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = BizzyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Delivery Options & PIN Code",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = enteredPincode,
                                onValueChange = {
                                    if (it.length <= 6) enteredPincode = it.filter { c -> c.isDigit() }
                                },
                                label = { Text("Enter 6-digit PIN code", fontSize = 12.sp) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("detail_pincode_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (enteredPincode.length == 6) {
                                        pincodeStatus = "Delivery in 2 Days to $enteredPincode | Free Delivery"
                                        onShowToast("Serviceable at PIN $enteredPincode! Fast delivery available.")
                                    } else {
                                        onShowToast("Please enter a valid 6-digit PIN code")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(52.dp)
                                    .testTag("detail_check_pincode_btn")
                            ) {
                                Text("Check", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = BizzyGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = pincodeStatus,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BizzyTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Trust Badges Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            ServiceBadge(icon = Icons.Default.Replay, text = "7 Days\nReplacement")
                            ServiceBadge(icon = Icons.Default.LocalShipping, text = "Free Delivery\nAbove ₹499")
                            ServiceBadge(icon = Icons.Default.Payments, text = "Cash on\nDelivery")
                            ServiceBadge(icon = Icons.Default.Security, text = "BizzyCart\nAssured")
                        }
                    }
                }
            }

            // Quantity Selector
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Quantity",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, BizzyBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }
                            Text(
                                text = "$quantity",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            IconButton(
                                onClick = { if (quantity < 10) quantity++ },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // Product Description & Specifications
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Product Overview",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = BizzyTextSecondary,
                                lineHeight = 22.sp
                            )
                        )

                        if (product.features.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Key Highlights",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            product.features.split("\n").forEach { feature ->
                                if (feature.isNotBlank()) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = BizzyGreen,
                                            modifier = Modifier
                                                .size(16.dp)
                                                .padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = feature,
                                            style = MaterialTheme.typography.bodyMedium.copy(color = BizzyTextPrimary)
                                        )
                                    }
                                }
                            }
                        }

                        if (product.specifications.isNotBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Specifications",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            product.specifications.split("\n").forEach { spec ->
                                if (spec.contains(":")) {
                                    val parts = spec.split(":", limit = 2)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = parts[0].trim(),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BizzyTextSecondary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = parts[1].trim(),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BizzyTextPrimary,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.weight(1.5f)
                                        )
                                    }
                                    Divider(color = Color(0xFFF1F5F9))
                                }
                            }
                        }
                    }
                }
            }

            // Customer Reviews Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ratings & Reviews (${reviews.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            OutlinedButton(
                                onClick = { showAddReviewDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("write_review_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RateReview,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Write Review", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (reviews.isEmpty()) {
                            Text(
                                text = "No reviews yet. Be the first verified customer to write a review!",
                                style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            reviews.forEach { review ->
                                ReviewItemCard(review = review)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // Related Products Row
            if (relatedProducts.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = "Similar Products You Might Like",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(relatedProducts) { related ->
                                ProductCard(
                                    product = related,
                                    isWishlisted = wishlistProducts.any { it.id == related.id },
                                    onProductClick = onProductClick,
                                    onAddToCart = { onAddToCart(it, 1) },
                                    onToggleWishlist = onToggleWishlist,
                                    modifier = Modifier.width(170.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Action Bar (Add to Cart / Buy Now)
        Surface(
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add to Cart Button
                OutlinedButton(
                    onClick = { onAddToCart(product, quantity) },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, BizzyPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_add_to_cart_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = BizzyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Add to Cart",
                        color = BizzyPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Buy Now Button
                Button(
                    onClick = { onBuyNow(product, quantity) },
                    colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_buy_now_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Buy Now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // Add Review Dialog
    if (showAddReviewDialog) {
        var userRating by remember { mutableStateOf(5.0f) }
        var reviewTitle by remember { mutableStateOf("") }
        var reviewComment by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddReviewDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .testTag("add_review_dialog")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Write a Customer Review",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Overall Rating: ${userRating.toInt()} Stars",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star Stars",
                                tint = if (star <= userRating) BizzyGold else BizzyBorder,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { userRating = star.toFloat() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewTitle,
                        onValueChange = { reviewTitle = it },
                        label = { Text("Headline / Summary") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Detailed Review") },
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddReviewDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (reviewComment.isNotBlank()) {
                                    onAddReview(
                                        product.id,
                                        userRating,
                                        reviewTitle.ifBlank { "Great Product" },
                                        reviewComment
                                    )
                                    showAddReviewDialog = false
                                } else {
                                    onShowToast("Please enter review comments")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Submit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferItem(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = BizzyAccent.copy(alpha = 0.1f),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = title,
                color = BizzyAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(
                color = BizzyTextSecondary,
                lineHeight = 16.sp
            )
        )
    }
}

@Composable
private fun ServiceBadge(icon: ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BizzySurfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BizzyPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp,
                color = BizzyTextSecondary,
                lineHeight = 13.sp
            ),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ReviewItemCard(review: ReviewEntity) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = BizzyGreen,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "%.0f".format(review.rating),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(10.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = review.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                }
                Text(
                    text = review.date,
                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextMuted, fontSize = 10.sp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, lineHeight = 16.sp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = review.authorName,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = BizzyTextPrimary
                    )
                )
                if (review.isVerifiedPurchase) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified Purchase",
                        tint = BizzyGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Verified Purchase",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = BizzyGreen,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}
