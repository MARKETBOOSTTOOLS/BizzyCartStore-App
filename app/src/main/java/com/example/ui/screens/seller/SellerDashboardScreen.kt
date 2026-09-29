package com.example.ui.screens.seller

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
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ProductEntity
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary
import com.example.ui.viewmodel.UserProfile

@Composable
fun SellerDashboardScreen(
    userProfile: UserProfile,
    products: List<ProductEntity>,
    onRegisterSeller: (storeName: String) -> Unit,
    onAddProduct: (ProductEntity) -> Unit,
    onShowToast: (String) -> Unit
) {
    var showRegisterModal by remember { mutableStateOf(false) }
    var showAddSellerProductModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("seller_dashboard_screen")
    ) {
        // Seller Top Banner
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BizzyAccent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = BizzyAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (userProfile.isSeller) userProfile.sellerStoreName else "Sell on BizzyCart",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Text(
                            text = if (userProfile.isSeller) "GSTIN Verified Seller • All India Delivery" else "Register your Indian business in 2 minutes",
                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                        )
                    }
                }

                if (!userProfile.isSeller) {
                    Button(
                        onClick = { showRegisterModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("seller_register_btn")
                    ) {
                        Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { showAddSellerProductModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Item", fontSize = 12.sp)
                    }
                }
            }
        }

        // Dashboard or Landing Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!userProfile.isSeller) {
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BizzyBorder, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Why Sell on BizzyCart?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BizzyTextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            SellerBenefit(
                                title = "Access to Millions of Shoppers",
                                description = "Reach customers across Tier 1, 2, and 3 cities throughout India."
                            )
                            SellerBenefit(
                                title = "Lowest Commission Rates",
                                description = "Keep more of your profits with 0% listing fee and transparent commissions."
                            )
                            SellerBenefit(
                                title = "Pan-India Logistics & COD Support",
                                description = "Doorstep pickup and cash on delivery supported by Bluedart & Delhivery."
                            )
                            SellerBenefit(
                                title = "Fast Weekly Settlements",
                                description = "Money deposited directly into your Indian bank account every 7 days."
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { showRegisterModal = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Start Selling on BizzyCart", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Active Seller Metrics
                item {
                    Text(
                        text = "Store Overview (Last 30 Days)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SellerMetricCard(
                            title = "Total Revenue",
                            value = "₹84,520",
                            icon = Icons.Default.Paid,
                            color = BizzyGreen,
                            modifier = Modifier.weight(1f)
                        )
                        SellerMetricCard(
                            title = "Orders Shipped",
                            value = "48 Orders",
                            icon = Icons.Default.ShoppingBag,
                            color = BizzyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SellerMetricCard(
                            title = "Active SKUs",
                            value = "${products.size}",
                            icon = Icons.Default.Inventory,
                            color = BizzyAccent,
                            modifier = Modifier.weight(1f)
                        )
                        SellerMetricCard(
                            title = "Pending Dispatch",
                            value = "3 Orders",
                            icon = Icons.Default.LocalShipping,
                            color = BizzyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your Top Selling Products",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "+ Add New",
                                    color = BizzyPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { showAddSellerProductModal = true }
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            products.take(4).forEach { p ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = p.name, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), maxLines = 1)
                                        Text(text = "Stock: ${p.stockQuantity} units available", style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp))
                                    }
                                    Text(text = "₹${p.price.toInt()}", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Register Modal
    if (showRegisterModal) {
        var storeNameInput by remember { mutableStateOf("") }
        var gstinInput by remember { mutableStateOf("") }
        var phoneInput by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showRegisterModal = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Register as BizzyCart Seller",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = storeNameInput,
                        onValueChange = { storeNameInput = it },
                        label = { Text("Store / Enterprise Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = gstinInput,
                        onValueChange = { gstinInput = it.uppercase() },
                        label = { Text("GSTIN Number (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { if (it.length <= 10) phoneInput = it.filter { c -> c.isDigit() } },
                        label = { Text("Registered Business Phone *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRegisterModal = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (storeNameInput.isNotBlank()) {
                                    onRegisterSeller(storeNameInput)
                                    showRegisterModal = false
                                } else {
                                    onShowToast("Please enter your store name")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Submit & Launch", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add Seller Product Modal
    if (showAddSellerProductModal) {
        var pName by remember { mutableStateOf("") }
        var pPrice by remember { mutableStateOf("") }
        var pCategory by remember { mutableStateOf("Electronics") }

        Dialog(onDismissRequest = { showAddSellerProductModal = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "List New Product on BizzyCart",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = pName,
                        onValueChange = { pName = it },
                        label = { Text("Product Title *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pCategory,
                        onValueChange = { pCategory = it },
                        label = { Text("Category *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = pPrice,
                        onValueChange = { pPrice = it.filter { c -> c.isDigit() } },
                        label = { Text("Selling Price (₹) *") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddSellerProductModal = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                val price = pPrice.toDoubleOrNull() ?: 0.0
                                if (pName.isNotBlank() && price > 0) {
                                    val newP = ProductEntity(
                                        name = pName,
                                        category = pCategory,
                                        brand = userProfile.sellerStoreName,
                                        price = price,
                                        mrp = price * 1.5,
                                        discountPercent = 33,
                                        rating = 5.0f,
                                        reviewCount = 0,
                                        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                                        description = "Genuine product sold by ${userProfile.sellerStoreName} on BizzyCart.",
                                        features = "Brand New Original\nSecure Packaging\nFree Delivery",
                                        specifications = "Seller: ${userProfile.sellerStoreName}\nCategory: $pCategory",
                                        sellerName = userProfile.sellerStoreName,
                                        isNewArrival = true
                                    )
                                    onAddProduct(newP)
                                    showAddSellerProductModal = false
                                } else {
                                    onShowToast("Please enter product title and valid price")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("List Product", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SellerBenefit(title: String, description: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = BizzyGreen,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BizzyTextPrimary))
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp))
        }
    }
}

@Composable
private fun SellerMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier.border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = BizzyTextPrimary))
        }
    }
}
