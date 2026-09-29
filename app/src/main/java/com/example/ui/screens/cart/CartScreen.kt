package com.example.ui.screens.cart

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DeleteOutline
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CouponEntity
import com.example.data.model.ProductEntity
import com.example.data.repository.CartItemWithProduct
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyRed
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary

@Composable
fun CartScreen(
    cartItems: List<CartItemWithProduct>,
    appliedCoupon: CouponEntity?,
    onUpdateQuantity: (productId: Int, quantity: Int) -> Unit,
    onRemoveItem: (productId: Int) -> Unit,
    onSaveForLater: (product: ProductEntity) -> Unit,
    onApplyCoupon: (code: String, subtotal: Double) -> Unit,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onContinueShopping: () -> Unit,
    onProductClick: (ProductEntity) -> Unit
) {
    if (cartItems.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.RemoveShoppingCart,
            title = "Your Shopping Cart is Empty",
            subtitle = "Explore thousands of products across Electronics, Fashion, Beauty and more.",
            buttonText = "Start Shopping",
            onButtonClick = onContinueShopping,
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.product.price * it.cartItem.quantity }
    }
    val totalMrp = remember(cartItems) {
        cartItems.sumOf { it.product.mrp * it.cartItem.quantity }
    }
    val productSavings = remember(subtotal, totalMrp) {
        (totalMrp - subtotal).coerceAtLeast(0.0)
    }

    val couponDiscount = remember(subtotal, appliedCoupon) {
        appliedCoupon?.let { coupon ->
            val calc = (subtotal * coupon.discountPercent) / 100.0
            calc.coerceAtMost(coupon.maxDiscount)
        } ?: 0.0
    }

    val deliveryFee = remember(subtotal) {
        if (subtotal >= 499) 0.0 else 40.0
    }

    val totalAmount = remember(subtotal, couponDiscount, deliveryFee) {
        (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)
    }

    val totalSavings = remember(productSavings, couponDiscount) {
        productSavings + couponDiscount
    }

    var couponInput by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp)
                .background(Color(0xFFF8FAFC))
                .testTag("cart_items_list")
        ) {
            // Savings alert banner
            if (totalSavings > 0) {
                item {
                    Surface(
                        color = BizzyGreenLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BizzyGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Yay! You are saving ₹${totalSavings.toInt()} on this order",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BizzyGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }

            // Items Count Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cart Items (${cartItems.size})",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = "Deliver to Current Location",
                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                    )
                }
            }

            // Cart Items
            items(cartItems) { item ->
                CartItemRow(
                    item = item,
                    onIncrease = { onUpdateQuantity(item.product.id, item.cartItem.quantity + 1) },
                    onDecrease = { onUpdateQuantity(item.product.id, item.cartItem.quantity - 1) },
                    onRemove = { onRemoveItem(item.product.id) },
                    onSaveForLater = { onSaveForLater(item.product) },
                    onClick = { onProductClick(item.product) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Coupons & Offers Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
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
                                text = "Coupons & Discounts",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (appliedCoupon != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BizzyGreenLight,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BizzyGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "'${appliedCoupon.code}' Applied",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = BizzyGreen
                                            )
                                        )
                                        Text(
                                            text = "Saved ₹${couponDiscount.toInt()} on this order",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BizzyGreen,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                    Text(
                                        text = "Remove",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = BizzyRed,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .clickable { onRemoveCoupon() }
                                            .padding(4.dp)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it.uppercase() },
                                    placeholder = { Text("Enter coupon code", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_input_field")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponInput.isNotBlank()) {
                                            onApplyCoupon(couponInput, subtotal)
                                            couponInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(52.dp)
                                        .testTag("apply_coupon_btn")
                                ) {
                                    Text("Apply", fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Available Coupons for You:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextSecondary
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("FIRSTORDER", "WELCOME10", "SAVE20").forEach { code ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BizzyPrimary.copy(alpha = 0.08f),
                                        modifier = Modifier.clickable {
                                            onApplyCoupon(code, subtotal)
                                        }
                                    ) {
                                        Text(
                                            text = code,
                                            color = BizzyPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Order Price Summary
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Price Details (${cartItems.sumOf { it.cartItem.quantity }} Items)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PriceRow(label = "Total MRP", value = "₹${totalMrp.toInt()}")
                        PriceRow(
                            label = "Discount on MRP",
                            value = "-₹${productSavings.toInt()}",
                            isHighlight = true
                        )

                        if (appliedCoupon != null) {
                            PriceRow(
                                label = "Coupon Discount (${appliedCoupon.code})",
                                value = "-₹${couponDiscount.toInt()}",
                                isHighlight = true
                            )
                        }

                        PriceRow(
                            label = "Delivery Charges",
                            value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                            isHighlight = deliveryFee == 0.0
                        )

                        PriceRow(
                            label = "Taxes & Duties (GST)",
                            value = "Included in MRP"
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = BizzyBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Amount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            Text(
                                text = "₹${totalAmount.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = BizzyPrimary
                                )
                            )
                        }

                        if (deliveryFee > 0.0) {
                            Text(
                                text = "Add ₹${(499 - subtotal).toInt()} more to qualify for Free Delivery!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BizzyAccent,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Sticky Bottom Checkout Bar
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total: ₹${totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = "Saved ₹${totalSavings.toInt()}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                Button(
                    onClick = onProceedToCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("proceed_to_checkout_btn")
                ) {
                    Text(
                        text = "Proceed to Checkout",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItemWithProduct,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit,
    onSaveForLater: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("cart_item_${item.product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BizzySurfaceVariant)
                ) {
                    AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.product.name,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = BizzyTextPrimary,
                            lineHeight = 18.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${item.product.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "₹${item.product.mrp.toInt()}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BizzyTextMuted,
                                textDecoration = TextDecoration.LineThrough,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${item.product.discountPercent}% off",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BizzyGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "In Stock • Eligible for Free Delivery",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = BizzyBorder.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Stepper and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, BizzyBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (item.cartItem.quantity == 1) Icons.Outlined.DeleteOutline else Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp),
                            tint = if (item.cartItem.quantity == 1) BizzyRed else BizzyTextPrimary
                        )
                    }

                    Text(
                        text = "${item.cartItem.quantity}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp),
                            tint = BizzyTextPrimary
                        )
                    }
                }

                // Save for Later / Remove
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Save for Later",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier
                            .clickable { onSaveForLater() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Text(
                        text = "Remove",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyRed,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier
                            .clickable { onRemove() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = BizzyTextSecondary)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isHighlight) BizzyGreen else BizzyTextPrimary
            )
        )
    }
}
