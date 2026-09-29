package com.example.ui.screens.home

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.CategoryCircleItem
import com.example.ui.components.CategoryWideCard
import com.example.ui.components.ProductCard
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyPrimaryDark
import com.example.ui.theme.BizzyRed
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary
import kotlinx.coroutines.delay

data class HeroSlide(
    val headline: String,
    val description: String,
    val cta: String,
    val tag: String,
    val imageUrl: String,
    val bgGradient: Brush
)

@Composable
fun HomeScreen(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    wishlistProducts: List<ProductEntity>,
    onCategoryClick: (CategoryEntity) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onQuickView: (ProductEntity) -> Unit,
    onViewAllProducts: (String?, String?) -> Unit,
    onSellerRegisterClick: () -> Unit,
    onShowToast: (String) -> Unit
) {
    val wishlistedIds = remember(wishlistProducts) { wishlistProducts.map { it.id }.toSet() }

    val trendingProducts = remember(products) {
        products.filter { it.badge == "Trending" || it.isFeatured }.take(8)
    }
    val bestSellers = remember(products) {
        products.filter { it.badge == "Best Seller" || it.rating >= 4.7f }.take(8)
    }
    val dealsOfDay = remember(products) {
        products.filter { it.isDealOfDay || it.discountPercent >= 50 }.take(8)
    }
    val newArrivals = remember(products) {
        products.filter { it.isNewArrival || it.badge == "New" }.take(8)
    }

    val heroSlides = listOf(
        HeroSlide(
            headline = "Everything You Need,\nAll in One Cart",
            description = "Discover genuine products at prices you'll love with fast India-wide delivery.",
            cta = "Shop Now",
            tag = "FESTIVE SALE LIVE",
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&auto=format&fit=crop&q=80",
            bgGradient = Brush.horizontalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E3A8A)))
        ),
        HeroSlide(
            headline = "Mega Savings – Up to 65% Off",
            description = "Electronics, True Wireless Audio & Smart Wearables from certified brands.",
            cta = "Grab Deals",
            tag = "LIMITED TIME OFFERS",
            imageUrl = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80",
            bgGradient = Brush.horizontalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF2563EB)))
        ),
        HeroSlide(
            headline = "Home & Kitchen Masterclass",
            description = "Air fryers, heavy duty mixer grinders & natural cookware for Indian kitchens.",
            cta = "Explore Range",
            tag = "TOP RATED",
            imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=800&auto=format&fit=crop&q=80",
            bgGradient = Brush.horizontalGradient(listOf(Color(0xFF7C2D12), Color(0xFFEA580C)))
        )
    )

    var currentHeroIndex by remember { mutableIntStateOf(0) }

    // Auto slider
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentHeroIndex = (currentHeroIndex + 1) % heroSlides.size
        }
    }

    var newsletterEmail by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_scroll")
    ) {
        // 1. Promotional Hero Banner Slider
        item {
            val slide = heroSlides[currentHeroIndex]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(slide.bgGradient)
                    .testTag("home_hero_banner")
            ) {
                // Background subtle image
                AsyncImage(
                    model = slide.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp)),
                    alpha = 0.25f
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Tag Badge
                    Surface(
                        color = BizzyAccent,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = slide.tag,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = slide.headline,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 28.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        ),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onViewAllProducts(null, null) },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("hero_cta_button")
                        ) {
                            Text(
                                text = slide.cta,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        // Slide Dots
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            heroSlides.indices.forEach { index ->
                                Box(
                                    modifier = Modifier
                                        .size(if (index == currentHeroIndex) 8.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == currentHeroIndex) BizzyAccent else Color.White.copy(alpha = 0.5f)
                                        )
                                        .clickable { currentHeroIndex = index }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Shop by Category (Horizontal Circles)
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Shop by Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyPrimary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.clickable { onViewAllProducts(null, null) }
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(categories) { cat ->
                        CategoryCircleItem(
                            category = cat,
                            onClick = onCategoryClick
                        )
                    }
                }
            }
        }

        // 3. Deals of the Day (with Countdown Deal Header)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = BizzyRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Deals of the Day",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                        }
                        Surface(
                            color = BizzyRed,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Ends in 05h:32m",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(dealsOfDay) { product ->
                            ProductCard(
                                product = product,
                                isWishlisted = product.id in wishlistedIds,
                                onProductClick = onProductClick,
                                onAddToCart = onAddToCart,
                                onToggleWishlist = onToggleWishlist,
                                onQuickView = onQuickView,
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }
            }
        }

        // 4. Trending Products
        item {
            ProductSectionRow(
                title = "Trending Right Now",
                subtitle = "Most loved by shoppers across India this week",
                products = trendingProducts,
                wishlistedIds = wishlistedIds,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onToggleWishlist = onToggleWishlist,
                onQuickView = onQuickView,
                onViewAll = { onViewAllProducts(null, "Trending") }
            )
        }

        // 5. Best Sellers
        item {
            ProductSectionRow(
                title = "Best Sellers",
                subtitle = "Top-rated items with thousands of 5-star reviews",
                products = bestSellers,
                wishlistedIds = wishlistedIds,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onToggleWishlist = onToggleWishlist,
                onQuickView = onQuickView,
                onViewAll = { onViewAllProducts(null, "Best Seller") }
            )
        }

        // 6. New Arrivals
        item {
            ProductSectionRow(
                title = "New Arrivals",
                subtitle = "Freshly launched gadgets, styles, and essentials",
                products = newArrivals,
                wishlistedIds = wishlistedIds,
                onProductClick = onProductClick,
                onAddToCart = onAddToCart,
                onToggleWishlist = onToggleWishlist,
                onQuickView = onQuickView,
                onViewAll = { onViewAllProducts(null, "New") }
            )
        }

        // 7. Popular Categories (Wide Grid Cards)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                Text(
                    text = "Explore Popular Collections",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))

                val halfCategories = categories.take(4)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        halfCategories.take(2).forEach { cat ->
                            CategoryWideCard(
                                category = cat,
                                onClick = onCategoryClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        halfCategories.drop(2).take(2).forEach { cat ->
                            CategoryWideCard(
                                category = cat,
                                onClick = onCategoryClick,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // 8. Why Shop With BizzyCart
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Why Shop With BizzyCart?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextPrimary
                    )
                )
                Text(
                    text = "Shop Smart. Shop Easy. Shop BizzyCart.",
                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TrustFeatureCard(
                        icon = Icons.Default.LocalShipping,
                        title = "Free & Fast",
                        subtitle = "On orders above ₹499",
                        modifier = Modifier.weight(1f)
                    )
                    TrustFeatureCard(
                        icon = Icons.Default.Verified,
                        title = "100% Genuine",
                        subtitle = "Direct from brand sellers",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TrustFeatureCard(
                        icon = Icons.Default.Refresh,
                        title = "7-Day Returns",
                        subtitle = "Hassle-free replacements",
                        modifier = Modifier.weight(1f)
                    )
                    TrustFeatureCard(
                        icon = Icons.Default.Lock,
                        title = "Secure Checkout",
                        subtitle = "UPI, Cards & COD",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 9. Seller / Marketplace Promo Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(BizzyPrimaryDark, BizzyPrimary)
                        )
                    )
                    .padding(20.dp)
                    .testTag("home_seller_promo_card")
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BizzyAccent.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = BizzyAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Sell on BizzyCart",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Grow your business across all 28 states of India. Zero onboarding fees, lowest commission rates, and timely weekly payouts.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onSellerRegisterClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("become_seller_btn")
                    ) {
                        Text("Register as Seller", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 10. Newsletter / App Promotion
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .border(1.dp, BizzyBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Get ₹150 Off Your First Order!",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = "Subscribe to BizzyCart offers & receive coupon code FIRSTORDER directly in your inbox.",
                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newsletterEmail,
                            onValueChange = { newsletterEmail = it },
                            placeholder = { Text("Enter your email", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newsletterEmail.contains("@")) {
                                    onShowToast("Subscribed! Use coupon FIRSTORDER on checkout.")
                                    newsletterEmail = ""
                                } else {
                                    onShowToast("Please enter a valid email address.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(50.dp)
                        ) {
                            Text("Subscribe", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 11. Footer
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BizzyCart.in",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
                Text(
                    text = "India's Modern Ecommerce Marketplace",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "All prices in Indian Rupees (₹ / INR). 100% Secure Checkout powered by Razorpay & UPI standards.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "© 2026 BizzyCart India. All rights reserved.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 10.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProductSectionRow(
    title: String,
    subtitle: String,
    products: List<ProductEntity>,
    wishlistedIds: Set<Int>,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onQuickView: (ProductEntity) -> Unit,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BizzyTextSecondary,
                        fontSize = 11.sp
                    )
                )
            }
            Text(
                text = "See All",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BizzyPrimary,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    isWishlisted = product.id in wishlistedIds,
                    onProductClick = onProductClick,
                    onAddToCart = onAddToCart,
                    onToggleWishlist = onToggleWishlist,
                    onQuickView = onQuickView,
                    modifier = Modifier.width(180.dp)
                )
            }
        }
    }
}

@Composable
private fun TrustFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.border(1.dp, BizzyBorder, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = BizzyPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BizzyPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BizzyTextSecondary,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}
