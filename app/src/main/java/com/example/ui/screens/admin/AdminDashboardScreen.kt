package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGreen
import com.example.ui.theme.BizzyGreenLight
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyRed
import com.example.ui.theme.BizzyRedLight
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary

@Composable
fun AdminDashboardScreen(
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    orders: List<OrderEntity>,
    coupons: List<CouponEntity>,
    onAddProduct: (ProductEntity) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onAddCategory: (CategoryEntity) -> Unit,
    onDeleteCategory: (CategoryEntity) -> Unit,
    onUpdateOrderStatus: (orderId: String, newStatus: String) -> Unit,
    onShowToast: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Products", "Categories", "Orders", "Coupons")

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    val totalRevenue = remember(orders) { orders.sumOf { it.totalAmount } }
    val pendingOrdersCount = remember(orders) { orders.count { it.status != "Delivered" } }
    val lowStockCount = remember(products) { products.count { it.stockQuantity < 25 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "BizzyCart Admin Portal",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = BizzyPrimary
                            )
                        )
                        Text(
                            text = "Marketplace Operations & Inventory Control",
                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                        )
                    }

                    Surface(
                        color = BizzyGreenLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "LIVE STORE",
                            color = BizzyGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 12.dp,
                    containerColor = Color.White,
                    contentColor = BizzyPrimary
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> { // Overview
                    item {
                        Text(
                            text = "Marketplace Performance",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricStatCard(
                                title = "Total Sales",
                                value = "₹${totalRevenue.toInt()}",
                                icon = Icons.Default.Paid,
                                color = BizzyGreen,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                title = "Total Orders",
                                value = "${orders.size}",
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
                            MetricStatCard(
                                title = "Total Products",
                                value = "${products.size}",
                                icon = Icons.Default.Inventory,
                                color = BizzyAccent,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                title = "Pending Orders",
                                value = "$pendingOrdersCount",
                                icon = Icons.Default.LocalShipping,
                                color = BizzyPrimary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricStatCard(
                                title = "Low Stock Alerts",
                                value = "$lowStockCount Items",
                                icon = Icons.Default.Warning,
                                color = BizzyRed,
                                modifier = Modifier.weight(1f)
                            )
                            MetricStatCard(
                                title = "Active Categories",
                                value = "${categories.size}",
                                icon = Icons.Default.Category,
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
                                Text(
                                    text = "Recent Platform Activity",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "• ${orders.size} customer orders processed\n• ${products.size} live SKUs published across ${categories.size} categories\n• Server-side health: 100% operational on https://bizzycart.in/",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = BizzyTextSecondary,
                                        lineHeight = 20.sp
                                    )
                                )
                            }
                        }
                    }
                }

                1 -> { // Products
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "All Products (${products.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = { showAddProductDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("admin_add_product_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Product", fontSize = 12.sp)
                            }
                        }
                    }

                    items(products) { product ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BizzyTextPrimary
                                        ),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Category: ${product.category} • Brand: ${product.brand}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp)
                                    )
                                    Text(
                                        text = "Price: ₹${product.price.toInt()} | MRP: ₹${product.mrp.toInt()} | Stock: ${product.stockQuantity}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyPrimary, fontWeight = FontWeight.SemiBold)
                                    )
                                }

                                IconButton(onClick = { onDeleteProduct(product) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Product",
                                        tint = BizzyRed,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> { // Categories
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Configured Categories (${categories.size})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = { showAddCategoryDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Category", fontSize = 12.sp)
                            }
                        }
                    }

                    items(categories) { cat ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(10.dp))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = cat.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Slug: ${cat.slug} • Products: ${cat.productCount}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = BizzyPrimary, fontSize = 11.sp)
                                    )
                                }

                                IconButton(onClick = { onDeleteCategory(cat) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BizzyRed)
                                }
                            }
                        }
                    }
                }

                3 -> { // Orders
                    item {
                        Text(
                            text = "Customer Orders Management",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(orders) { ord ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Order: ${ord.id}",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BizzyPrimary
                                        )
                                    )
                                    Text(
                                        text = "Status: ${ord.status}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (ord.status == "Delivered") BizzyGreen else BizzyAccent
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Buyer: ${ord.shippingName} (${ord.shippingCity})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                                )
                                Text(
                                    text = "Items: ${ord.itemsSummary}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextPrimary),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Total Amount: ₹${ord.totalAmount.toInt()} (${ord.paymentMethod})",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Packed", "Shipped", "Delivered").forEach { status ->
                                        OutlinedButton(
                                            onClick = { onUpdateOrderStatus(ord.id, status) },
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(status, fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                4 -> { // Coupons
                    item {
                        Text(
                            text = "Promotional Discount Coupons",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(coupons) { coupon ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BizzyBorder, RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = coupon.code,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = BizzyPrimary
                                        )
                                    )
                                    Surface(color = BizzyGreenLight, shape = RoundedCornerShape(4.dp)) {
                                        Text(
                                            text = "${coupon.discountPercent}% OFF",
                                            color = BizzyGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = coupon.description, style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary))
                                Text(
                                    text = "Min Order: ₹${coupon.minOrderValue.toInt()} | Max Discount: ₹${coupon.maxDiscount.toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextPrimary, fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Product Modal
    if (showAddProductDialog) {
        var pName by remember { mutableStateOf("") }
        var pBrand by remember { mutableStateOf("BizzyCart") }
        var pCategory by remember { mutableStateOf(categories.firstOrNull()?.name ?: "Electronics") }
        var pPrice by remember { mutableStateOf("") }
        var pMrp by remember { mutableStateOf("") }
        var pDesc by remember { mutableStateOf("") }
        var pBadge by remember { mutableStateOf("New") }

        Dialog(onDismissRequest = { showAddProductDialog = false }) {
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
                            text = "Add New Marketplace Product",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pName,
                            onValueChange = { pName = it },
                            label = { Text("Product Name *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = pBrand,
                            onValueChange = { pBrand = it },
                            label = { Text("Brand *") },
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = pPrice,
                                onValueChange = { pPrice = it.filter { c -> c.isDigit() } },
                                label = { Text("Sale Price (₹) *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pMrp,
                                onValueChange = { pMrp = it.filter { c -> c.isDigit() } },
                                label = { Text("MRP (₹) *") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = pDesc,
                            onValueChange = { pDesc = it },
                            label = { Text("Product Description *") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showAddProductDialog = false },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }
                            Button(
                                onClick = {
                                    val price = pPrice.toDoubleOrNull() ?: 0.0
                                    val mrp = pMrp.toDoubleOrNull() ?: price
                                    if (pName.isNotBlank() && price > 0) {
                                        val discount = if (mrp > price) (((mrp - price) / mrp) * 100).toInt() else 0
                                        val newProduct = ProductEntity(
                                            name = pName,
                                            category = pCategory,
                                            brand = pBrand,
                                            price = price,
                                            mrp = mrp,
                                            discountPercent = discount,
                                            rating = 4.7f,
                                            reviewCount = 1,
                                            imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=600&auto=format&fit=crop&q=80",
                                            description = pDesc.ifBlank { "Authentic high quality product on BizzyCart." },
                                            features = "100% Original Genuine\nFree Express Delivery\n7-Day Replacement Guarantee",
                                            specifications = "Brand: $pBrand\nCategory: $pCategory",
                                            badge = pBadge,
                                            isNewArrival = true
                                        )
                                        onAddProduct(newProduct)
                                        showAddProductDialog = false
                                    } else {
                                        onShowToast("Please enter product name and valid price")
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Add Product", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Category Modal
    if (showAddCategoryDialog) {
        var catName by remember { mutableStateOf("") }
        var catDesc by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddCategoryDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Create New Category",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = catName,
                        onValueChange = { catName = it },
                        label = { Text("Category Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = catDesc,
                        onValueChange = { catDesc = it },
                        label = { Text("Category Description") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showAddCategoryDialog = false },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (catName.isNotBlank()) {
                                    val newCategory = CategoryEntity(
                                        name = catName,
                                        slug = catName.lowercase().replace(" ", "-"),
                                        iconName = "category",
                                        imageUrl = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&auto=format&fit=crop&q=80",
                                        description = catDesc.ifBlank { "Discover $catName products on BizzyCart" },
                                        productCount = 1,
                                        displayOrder = categories.size + 1
                                    )
                                    onAddCategory(newCategory)
                                    showAddCategoryDialog = false
                                } else {
                                    onShowToast("Please enter a category name")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricStatCard(
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
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BizzyTextSecondary,
                        fontSize = 11.sp
                    )
                )
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
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = BizzyTextPrimary
                )
            )
        }
    }
}
