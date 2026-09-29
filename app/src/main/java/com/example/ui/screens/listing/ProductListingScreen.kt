package com.example.ui.screens.listing

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.EmptyStateView
import com.example.ui.components.ProductCard
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyGold
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary
import com.example.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductListingScreen(
    title: String,
    initialCategory: String?,
    initialBadge: String?,
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    wishlistProducts: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onQuickView: (ProductEntity) -> Unit
) {
    val wishlistedIds = remember(wishlistProducts) { wishlistProducts.map { it.id }.toSet() }

    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var selectedBadge by remember { mutableStateOf(initialBadge) }
    var selectedSort by remember { mutableStateOf(SortOption.RELEVANCE) }
    var minRating by remember { mutableStateOf<Float?>(null) }
    var inStockOnly by remember { mutableStateOf(false) }

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }

    // Filtering & Sorting
    val displayedProducts = remember(
        products, selectedCategory, selectedBadge, selectedSort, minRating, inStockOnly
    ) {
        var list = products

        if (selectedCategory != null) {
            list = list.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
        if (selectedBadge != null) {
            list = list.filter { it.badge.equals(selectedBadge, ignoreCase = true) }
        }
        if (minRating != null) {
            list = list.filter { it.rating >= minRating!! }
        }
        if (inStockOnly) {
            list = list.filter { it.inStock }
        }

        when (selectedSort) {
            SortOption.RELEVANCE -> list
            SortOption.POPULARITY -> list.sortedByDescending { it.reviewCount }
            SortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> list.sortedByDescending { it.price }
            SortOption.HIGHEST_RATED -> list.sortedByDescending { it.rating }
            SortOption.NEWEST -> list.sortedByDescending { it.id }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("product_listing_screen")
    ) {
        // Sticky Filter & Sort Bar
        Surface(
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Sort Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BizzyBorder),
                        color = Color.White,
                        modifier = Modifier
                            .clickable { showSortSheet = true }
                            .testTag("sort_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sort,
                                contentDescription = "Sort",
                                tint = BizzyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedSort.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BizzyTextPrimary
                            )
                        }
                    }

                    // Filter Button
                    val activeFilterCount = (if (selectedCategory != null) 1 else 0) +
                            (if (minRating != null) 1 else 0) +
                            (if (inStockOnly) 1 else 0)

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (activeFilterCount > 0) BizzyPrimary else BizzyBorder
                        ),
                        color = if (activeFilterCount > 0) BizzyPrimary.copy(alpha = 0.08f) else Color.White,
                        modifier = Modifier
                            .clickable { showFilterSheet = true }
                            .testTag("filter_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter",
                                tint = BizzyPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (activeFilterCount > 0) "Filters ($activeFilterCount)" else "Filters",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BizzyPrimary
                            )
                        }
                    }
                }

                Text(
                    text = "${displayedProducts.size} Products",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BizzyTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // Horizontal Category Quick Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BizzyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat.name,
                    onClick = {
                        selectedCategory = if (selectedCategory == cat.name) null else cat.name
                    },
                    label = { Text(cat.name, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BizzyPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Products Grid or Empty State
        if (displayedProducts.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.SearchOff,
                title = "No Products Found",
                subtitle = "Try adjusting your filters or search terms to find what you're looking for.",
                buttonText = "Clear Filters",
                onButtonClick = {
                    selectedCategory = null
                    selectedBadge = null
                    minRating = null
                    inStockOnly = false
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedProducts) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = product.id in wishlistedIds,
                        onProductClick = onProductClick,
                        onAddToCart = onAddToCart,
                        onToggleWishlist = onToggleWishlist,
                        onQuickView = onQuickView,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = BizzyTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                SortOption.values().forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedSort = option
                                showSortSheet = false
                            }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSort == option,
                            onClick = {
                                selectedSort = option
                                showSortSheet = false
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = BizzyPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option.label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (selectedSort == option) FontWeight.Bold else FontWeight.Normal,
                                color = BizzyTextPrimary
                            )
                        )
                    }
                }
            }
        }
    }

    // Filter Bottom Sheet
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Products",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BizzyTextPrimary
                        )
                    )
                    Text(
                        text = "Reset All",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = BizzyAccent,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable {
                            selectedCategory = null
                            minRating = null
                            inStockOnly = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rating Filter
                Text(
                    text = "Customer Rating",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = BizzyTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(4.0f to "4★ & above", 4.5f to "4.5★ & above").forEach { (rating, label) ->
                        FilterChip(
                            selected = minRating == rating,
                            onClick = { minRating = if (minRating == rating) null else rating },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BizzyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Availability toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "In Stock Only",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = BizzyTextPrimary
                            )
                        )
                        Text(
                            text = "Hide currently unavailable items",
                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                        )
                    }
                    Switch(
                        checked = inStockOnly,
                        onCheckedChange = { inStockOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = BizzyPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { showFilterSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BizzyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_filter_sheet_btn")
                ) {
                    Text(
                        text = "Apply Filters (${displayedProducts.size} results)",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
