package com.example.ui.screens.account

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BizzyAccent
import com.example.ui.theme.BizzyBorder
import com.example.ui.theme.BizzyPrimary
import com.example.ui.theme.BizzyRed
import com.example.ui.theme.BizzySurfaceVariant
import com.example.ui.theme.BizzyTextMuted
import com.example.ui.theme.BizzyTextPrimary
import com.example.ui.theme.BizzyTextSecondary
import com.example.ui.viewmodel.UserProfile

@Composable
fun AccountScreen(
    userProfile: UserProfile,
    ordersCount: Int,
    wishlistCount: Int,
    addressCount: Int,
    onNavigateToOrders: () -> Unit,
    onNavigateToWishlist: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToSeller: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onLogout: () -> Unit,
    onSyncFirebase: () -> Unit = {},
    onShowToast: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(14.dp)
            .testTag("account_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Profile Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BizzyBorder, RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(BizzyPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            tint = BizzyPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (userProfile.isLoggedIn) userProfile.name else "Guest Shopper",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BizzyTextPrimary
                            )
                        )
                        Text(
                            text = if (userProfile.isLoggedIn) userProfile.email else "Sign in to track orders & save items",
                            style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary)
                        )
                        if (userProfile.isLoggedIn) {
                            Text(
                                text = userProfile.phone,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BizzyPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    if (!userProfile.isLoggedIn) {
                        OutlinedButton(
                            onClick = onOpenAuthDialog,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("account_login_btn")
                        ) {
                            Text("Sign In", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick Stats row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickStatCard(
                    title = "My Orders",
                    count = "$ordersCount",
                    icon = Icons.Default.ShoppingBag,
                    onClick = onNavigateToOrders,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Wishlist",
                    count = "$wishlistCount",
                    icon = Icons.Default.Favorite,
                    onClick = onNavigateToWishlist,
                    modifier = Modifier.weight(1f)
                )
                QuickStatCard(
                    title = "Addresses",
                    count = "$addressCount",
                    icon = Icons.Default.LocationOn,
                    onClick = { onShowToast("You have $addressCount saved delivery addresses.") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Business & Marketplace Dashboards
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BizzyBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AccountNavRow(
                        icon = Icons.Default.Storefront,
                        title = "Seller Hub / Sell on BizzyCart",
                        subtitle = if (userProfile.isSeller) "Active Store: ${userProfile.sellerStoreName}" else "Reach millions of buyers across India",
                        onClick = onNavigateToSeller
                    )

                    Divider(color = BizzyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 14.dp))

                    AccountNavRow(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Admin Dashboard",
                        subtitle = "Manage Products, Categories, Orders & Coupons",
                        onClick = onNavigateToAdmin
                    )

                    Divider(color = BizzyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 14.dp))

                    AccountNavRow(
                        icon = Icons.Default.Cloud,
                        title = "Firebase Cloud Database (Connected)",
                        subtitle = "Project: bizzycart • Sync Catalog & Orders to Cloud Firestore",
                        onClick = onSyncFirebase
                    )
                }
            }
        }

        // Customer Support & Policies
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BizzyBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AccountNavRow(
                        icon = Icons.Default.Payment,
                        title = "Saved Payment Methods",
                        subtitle = "UPI, Cards & Net Banking (Safe & Encrypted)",
                        onClick = { onShowToast("Payments are securely processed via certified UPI & Razorpay gateways.") }
                    )

                    Divider(color = BizzyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 14.dp))

                    AccountNavRow(
                        icon = Icons.Default.Notifications,
                        title = "Order Notifications",
                        subtitle = "Push alerts for discounts & order delivery",
                        onClick = { onShowToast("Notifications are active for your orders.") }
                    )

                    Divider(color = BizzyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 14.dp))

                    AccountNavRow(
                        icon = Icons.Default.HelpCenter,
                        title = "Help & 24/7 Customer Support",
                        subtitle = "Toll-Free: 1800-BIZZY-CART | support@bizzycart.in",
                        onClick = { onShowToast("Support: support@bizzycart.in | +91 1800-249-9922") }
                    )

                    Divider(color = BizzyBorder.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 14.dp))

                    AccountNavRow(
                        icon = Icons.Default.Policy,
                        title = "Privacy Policy & Terms",
                        subtitle = "https://bizzycart.in/terms",
                        onClick = { onShowToast("BizzyCart adheres strictly to Indian eCommerce consumer protection laws.") }
                    )
                }
            }
        }

        // Login / Logout action
        item {
            if (userProfile.isLoggedIn) {
                OutlinedButton(
                    onClick = onLogout,
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = BizzyRed
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("account_logout_btn")
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = BizzyRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout from BizzyCart", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onOpenAuthDialog,
                    shape = RoundedCornerShape(10.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = BizzyPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("account_login_button")
                ) {
                    Icon(imageVector = Icons.Default.Login, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign In / Register", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    count: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .border(1.dp, BizzyBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BizzyPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = count, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = title, style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 10.sp))
        }
    }
}

@Composable
private fun AccountNavRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BizzySurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BizzyPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = BizzyTextPrimary))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = BizzyTextSecondary, fontSize = 11.sp))
        }

        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = BizzyTextMuted, modifier = Modifier.size(18.dp))
    }
}
