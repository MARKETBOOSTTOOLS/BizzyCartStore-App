package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.BottomTab
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary

@Composable
fun BizzyBottomNav(
    currentTab: BottomTab,
    cartCount: Int,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .testTag("bizzy_bottom_navigation")
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentTab == BottomTab.HOME,
            onClick = { onTabSelected(BottomTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BizzyPrimary,
                selectedTextColor = BizzyPrimary,
                unselectedIconColor = BizzyTextMuted,
                unselectedTextColor = BizzyTextMuted,
                indicatorColor = BizzyPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_home")
        )

        // 2. Categories
        NavigationBarItem(
            selected = currentTab == BottomTab.CATEGORIES,
            onClick = { onTabSelected(BottomTab.CATEGORIES) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = "Categories"
                )
            },
            label = {
                Text(
                    text = "Categories",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomTab.CATEGORIES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BizzyPrimary,
                selectedTextColor = BizzyPrimary,
                unselectedIconColor = BizzyTextMuted,
                unselectedTextColor = BizzyTextMuted,
                indicatorColor = BizzyPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_categories")
        )

        // 3. Search
        NavigationBarItem(
            selected = currentTab == BottomTab.SEARCH,
            onClick = { onTabSelected(BottomTab.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Search"
                )
            },
            label = {
                Text(
                    text = "Search",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomTab.SEARCH) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BizzyPrimary,
                selectedTextColor = BizzyPrimary,
                unselectedIconColor = BizzyTextMuted,
                unselectedTextColor = BizzyTextMuted,
                indicatorColor = BizzyPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_search")
        )

        // 4. Cart
        NavigationBarItem(
            selected = currentTab == BottomTab.CART,
            onClick = { onTabSelected(BottomTab.CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = BizzyAccent,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = "$cartCount",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BottomTab.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = {
                Text(
                    text = "Cart",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomTab.CART) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BizzyPrimary,
                selectedTextColor = BizzyPrimary,
                unselectedIconColor = BizzyTextMuted,
                unselectedTextColor = BizzyTextMuted,
                indicatorColor = BizzyPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_cart")
        )

        // 5. Account
        NavigationBarItem(
            selected = currentTab == BottomTab.ACCOUNT,
            onClick = { onTabSelected(BottomTab.ACCOUNT) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BottomTab.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account"
                )
            },
            label = {
                Text(
                    text = "Account",
                    fontSize = 11.sp,
                    fontWeight = if (currentTab == BottomTab.ACCOUNT) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BizzyPrimary,
                selectedTextColor = BizzyPrimary,
                unselectedIconColor = BizzyTextMuted,
                unselectedTextColor = BizzyTextMuted,
                indicatorColor = BizzyPrimary.copy(alpha = 0.12f)
            ),
            modifier = Modifier.testTag("nav_tab_account")
        )
    }
}
