package com.example.ui.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AddressEntity
import com.example.data.model.CouponEntity
import com.example.data.repository.CartItemWithProduct
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary

enum class CheckoutStep(val stepNumber: Int, val title: String) {
    ADDRESS(1, "Address"),
    DELIVERY(2, "Delivery"),
    PAYMENT(3, "Payment"),
    REVIEW(4, "Review")
}

@Composable
fun CheckoutScreen(
    cartItems: List<CartItemWithProduct>,
    appliedCoupon: CouponEntity?,
    addresses: List<AddressEntity>,
    onSaveAddress: (AddressEntity) -> Unit,
    onPlaceOrder: (AddressEntity, String, String) -> String,
    onOrderPlaced: (String) -> Unit,
    onShowToast: (String) -> Unit
) {
    var currentStep by remember { mutableStateOf(CheckoutStep.ADDRESS) }

    var selectedAddressId by remember(addresses) {
        mutableStateOf(addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id ?: 0)
    }
    val selectedAddress = remember(addresses, selectedAddressId) {
        addresses.find { it.id == selectedAddressId } ?: addresses.firstOrNull()
    }

    var selectedDeliveryOption by remember { mutableStateOf("Standard Free Delivery (2-3 Days)") }
    var selectedPaymentMethod by remember { mutableStateOf("UPI (Google Pay / PhonePe / Paytm)") }

    var showAddAddressDialog by remember { mutableStateOf(false) }

    // Totals
    val subtotal = remember(cartItems) {
        cartItems.sumOf { it.product.price * it.cartItem.quantity }
    }
    val couponDiscount = remember(subtotal, appliedCoupon) {
        appliedCoupon?.let { coupon ->
            val calc = (subtotal * coupon.discountPercent) / 100.0
            calc.coerceAtMost(coupon.maxDiscount)
        } ?: 0.0
    }
    val deliveryFee = remember(selectedDeliveryOption, subtotal) {
        if (selectedDeliveryOption.contains("Express")) 49.0 else if (subtotal >= 499) 0.0 else 40.0
    }
    val totalAmount = remember(subtotal, couponDiscount, deliveryFee) {
        (subtotal - couponDiscount + deliveryFee).coerceAtLeast(0.0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("checkout_screen")
    ) {
        // Step Indicator Progress Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CheckoutStep.values().forEach { step ->
                    val isPassed = step.stepNumber < currentStep.stepNumber
                    val isCurrent = step == currentStep

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable(enabled = isPassed) { currentStep = step }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isPassed -> BizzyGreen
                                        isCurrent -> BizzyPrimary
                                        else -> BizzyBorder
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPassed) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else {
                                Text(
                                    text = "${step.stepNumber}",
                                    color = if (isCurrent) Color.White else BizzyTextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = step.title,
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) BizzyPrimary else BizzyTextSecondary
                        )
                    }

                    if (step.stepNumber < 4) {
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .height(1.5.dp)
                                .background(if (isPassed) BizzyGreen else BizzyBorder)
                        )
                    }
                }
            }
        }

        // Main Step Content
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            when (currentStep) {
                CheckoutStep.ADDRESS -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Delivery Address",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            Button(
                                onClick = { showAddAddressDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("add_new_address_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add New", fontSize = 12.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (addresses.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                                    .padding(20.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No saved addresses yet.",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = BizzyTextSecondary)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { showAddAddressDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary)
                                    ) {
                                        Text("Add Delivery Address")
                                    }
                                }
                            }
                        }
                    } else {
                        items(addresses) { addr ->
                            val isSelected = addr.id == selectedAddressId
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) BizzyPrimary else BizzyBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedAddressId = addr.id }
                                    .testTag("address_card_${addr.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedAddressId = addr.id },
                                        colors = RadioButtonDefaults.colors(selectedColor = BizzyPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = addr.fullName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = BizzyTextPrimary
                                                )
                                            )
                                            if (addr.isDefault) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Surface(
                                                    color = BizzyPrimary.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "DEFAULT",
                                                        color = BizzyPrimary,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = "${addr.houseFlat}, ${addr.street}, ${addr.area}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                        )
                                        Text(
                                            text = "${addr.city}, ${addr.state} - ${addr.pincode}",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = BizzyTextPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        )
                                        Text(
                                            text = "Phone: +91 ${addr.phone}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                CheckoutStep.DELIVERY -> {
                    item {
                        Text(
                            text = "Choose Delivery Speed",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        listOf(
                            "Standard Free Delivery (2-3 Days)" to (if (subtotal >= 499) "FREE" else "₹40"),
                            "BizzyFast Express Delivery (Next Day by 5 PM)" to "₹49"
                        ).forEach { (option, price) ->
                            val isSelected = selectedDeliveryOption == option
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) BizzyPrimary else BizzyBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedDeliveryOption = option }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedDeliveryOption = option },
                                        colors = RadioButtonDefaults.colors(selectedColor = BizzyPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = BizzyTextPrimary
                                            )
                                        )
                                        Text(
                                            text = if (option.contains("Express")) "Guaranteed next day delivery across major Indian cities" else "Dispatched within 24 hours with full tracking",
                                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                        )
                                    }
                                    Text(
                                        text = price,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (price == "FREE") BizzyGreen else BizzyTextPrimary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = BizzyGreenLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = BizzyGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "All BizzyCart packages are sanitized and sealed in tamper-evident safety packaging.",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyGreen, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }

                CheckoutStep.PAYMENT -> {
                    item {
                        Text(
                            text = "Select Payment Method",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val paymentMethods = listOf(
                            PaymentOption(
                                "UPI (Google Pay / PhonePe / Paytm)",
                                "Instant 0% transaction fee via any UPI app",
                                Icons.Default.QrCode
                            ),
                            PaymentOption(
                                "Credit / Debit Card",
                                "Visa, MasterCard, RuPay, Maestro",
                                Icons.Default.CreditCard
                            ),
                            PaymentOption(
                                "Net Banking",
                                "SBI, HDFC, ICICI, Axis & 50+ other Indian banks",
                                Icons.Default.Payments
                            ),
                            PaymentOption(
                                "Cash on Delivery (COD)",
                                "Pay cash or UPI at your doorstep upon arrival",
                                Icons.Default.Payments
                            )
                        )

                        paymentMethods.forEach { method ->
                            val isSelected = selectedPaymentMethod == method.name
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) BizzyPrimary else BizzyBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedPaymentMethod = method.name }
                                    .testTag("payment_method_${method.name.take(3)}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedPaymentMethod = method.name },
                                        colors = RadioButtonDefaults.colors(selectedColor = BizzyPrimary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = method.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) BizzyPrimary else BizzyTextMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = method.name,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = BizzyTextPrimary
                                            )
                                        )
                                        Text(
                                            text = method.desc,
                                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = BizzyGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "256-Bit Bank Grade SSL Encrypted Checkout. CVV details are never stored.",
                                style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp)
                            )
                        }
                    }
                }

                CheckoutStep.REVIEW -> {
                    item {
                        Text(
                            text = "Order Review & Confirmation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Delivery details card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Delivering To:",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BizzyTextPrimary
                                        )
                                    )
                                    Text(
                                        text = "Change",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = BizzyPrimary,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier.clickable { currentStep = CheckoutStep.ADDRESS }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                if (selectedAddress != null) {
                                    Text(
                                        text = selectedAddress.fullName,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${selectedAddress.houseFlat}, ${selectedAddress.street}, ${selectedAddress.city} - ${selectedAddress.pincode}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                    )
                                    Text(
                                        text = "Phone: +91 ${selectedAddress.phone}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Payment & speed card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Payment Method:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BizzyTextPrimary
                                    )
                                )
                                Text(
                                    text = selectedPaymentMethod,
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Delivery Method:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BizzyTextPrimary
                                    )
                                )
                                Text(
                                    text = selectedDeliveryOption,
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Items summary
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Items in Order (${cartItems.size})",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BizzyTextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                cartItems.forEach { item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${item.product.name.take(28)}... x ${item.cartItem.quantity}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextPrimary),
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "₹${(item.product.price * item.cartItem.quantity).toInt()}",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = BizzyBorder)
                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Grand Total",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "₹${totalAmount.toInt()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = BizzyPrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom Step Navigation
        Surface(
            color = Color.White,
            shadowElevation = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentStep != CheckoutStep.ADDRESS) {
                    OutlinedButton(
                        onClick = {
                            currentStep = when (currentStep) {
                                CheckoutStep.DELIVERY -> CheckoutStep.ADDRESS
                                CheckoutStep.PAYMENT -> CheckoutStep.DELIVERY
                                CheckoutStep.REVIEW -> CheckoutStep.PAYMENT
                                else -> CheckoutStep.ADDRESS
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Button(
                    onClick = {
                        when (currentStep) {
                            CheckoutStep.ADDRESS -> {
                                if (selectedAddress != null) {
                                    currentStep = CheckoutStep.DELIVERY
                                } else {
                                    onShowToast("Please select or add a delivery address")
                                }
                            }
                            CheckoutStep.DELIVERY -> currentStep = CheckoutStep.PAYMENT
                            CheckoutStep.PAYMENT -> currentStep = CheckoutStep.REVIEW
                            CheckoutStep.REVIEW -> {
                                if (selectedAddress != null) {
                                    val orderId = onPlaceOrder(
                                        selectedAddress,
                                        selectedPaymentMethod,
                                        selectedDeliveryOption
                                    )
                                    if (orderId.isNotBlank()) {
                                        onOrderPlaced(orderId)
                                    }
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (currentStep == CheckoutStep.REVIEW) BizzyGreen else BizzyAccent
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("checkout_continue_btn")
                ) {
                    Text(
                        text = if (currentStep == CheckoutStep.REVIEW) "Place Order • ₹${totalAmount.toInt()}" else "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        var fullName by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var houseFlat by remember { mutableStateOf("") }
        var street by remember { mutableStateOf("") }
        var area by remember { mutableStateOf("") }
        var city by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("Haryana") }
        var pincode by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddAddressDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(16.dp)) {
                    item {
                        Text(
                            text = "Add New Delivery Address",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { if (it.length <= 10) phone = it.filter { c -> c.isDigit() } },
                            label = { Text("Mobile Number (10 digits) *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = houseFlat,
                            onValueChange = { houseFlat = it },
                            label = { Text("Flat, House No., Apartment *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = street,
                            onValueChange = { street = it },
                            label = { Text("Street, Sector, Landmark") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City *") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { if (it.length <= 6) pincode = it.filter { c -> c.isDigit() } },
                                label = { Text("PIN Code *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showAddAddressDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    if (fullName.isNotBlank() && phone.length == 10 && houseFlat.isNotBlank() && city.isNotBlank() && pincode.length == 6) {
                                        val newAddress = AddressEntity(
                                            fullName = fullName,
                                            phone = phone,
                                            houseFlat = houseFlat,
                                            street = street,
                                            area = area,
                                            city = city,
                                            state = state,
                                            pincode = pincode,
                                            isDefault = addresses.isEmpty()
                                        )
                                        onSaveAddress(newAddress)
                                        showAddAddressDialog = false
                                    } else {
                                        onShowToast("Please fill all required address fields correctly")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Save Address", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class PaymentOption(
    val name: String,
    val desc: String,
    val icon: ImageVector
)
