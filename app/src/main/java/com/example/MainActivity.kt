package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BizzyBottomNav
import com.example.ui.components.BizzyTopAppBar
import com.example.ui.components.PincodeDialog
import com.example.ui.components.QuickViewDialog
import com.example.ui.navigation.BottomTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.account.AccountScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.auth.AuthDialog
import com.example.ui.screens.cart.CartScreen
import com.example.ui.screens.categories.CategoriesScreen
import com.example.ui.screens.checkout.CheckoutScreen
import com.example.ui.screens.detail.ProductDetailScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.listing.ProductListingScreen
import com.example.ui.screens.order.OrderDetailScreen
import com.example.ui.screens.order.OrderSuccessScreen
import com.example.ui.screens.order.OrdersScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.seller.SellerDashboardScreen
import com.example.ui.screens.wishlist.WishlistScreen
import com.example.ui.theme.BizzyCartTheme
import com.example.ui.viewmodel.BizzyCartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BizzyCartTheme {
                BizzyCartApp()
            }
        }
    }
}

@Composable
fun BizzyCartApp(viewModel: BizzyCartViewModel = viewModel()) {
    val activeScreen by viewModel.activeScreen.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val cartCount by viewModel.cartCount.collectAsState()
    val deliveryPincode by viewModel.deliveryPincode.collectAsState()
    val deliveryCity by viewModel.deliveryCity.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val showAuthDialog by viewModel.showAuthDialog.collectAsState()
    val quickViewProduct by viewModel.quickViewProduct.collectAsState()

    val allProducts by viewModel.allProducts.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlistProducts by viewModel.wishlistProducts.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    val coupons by viewModel.coupons.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showPincodeDialog by remember { mutableStateOf(false) }

    // Collect toast messages
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // System BackHandler
    BackHandler(enabled = activeScreen !is Screen.Home) {
        if (!viewModel.popBackStack()) {
            // Already at root Home
        }
    }

    Scaffold(
        topBar = {
            val showBackButton = activeScreen !is Screen.Home
            val title = when (activeScreen) {
                is Screen.Home -> null
                is Screen.Categories -> "All Categories"
                is Screen.ProductListing -> (activeScreen as Screen.ProductListing).title
                is Screen.ProductDetail -> "Product Details"
                is Screen.Search -> "Search Products"
                is Screen.Cart -> "My Shopping Cart"
                is Screen.Checkout -> "Checkout"
                is Screen.OrderSuccess -> "Order Placed"
                is Screen.Orders -> "My Orders"
                is Screen.OrderDetail -> "Order Status"
                is Screen.Wishlist -> "My Wishlist"
                is Screen.Account -> "My Account"
                is Screen.AdminDashboard -> "Admin Control Panel"
                is Screen.SellerDashboard -> "Seller Hub"
            }

            val showSearchInput = activeScreen is Screen.Home

            BizzyTopAppBar(
                title = title,
                showBackButton = showBackButton,
                onBackClick = { viewModel.popBackStack() },
                deliveryPincode = deliveryPincode,
                deliveryCity = deliveryCity,
                onLocationClick = { showPincodeDialog = true },
                onSearchClick = { viewModel.navigateTo(Screen.Search()) },
                cartCount = cartCount,
                onCartClick = { viewModel.navigateTo(Screen.Cart) },
                onWishlistClick = { viewModel.navigateTo(Screen.Wishlist) },
                onSellerClick = { viewModel.navigateTo(Screen.SellerDashboard) },
                showSearchBar = showSearchInput
            )
        },
        bottomBar = {
            BizzyBottomNav(
                currentTab = currentTab,
                cartCount = cartCount,
                onTabSelected = { tab -> viewModel.switchTab(tab) }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = activeScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        categories = allCategories,
                        products = allProducts,
                        wishlistProducts = wishlistProducts,
                        onCategoryClick = { cat ->
                            viewModel.navigateTo(Screen.ProductListing(category = cat.name, title = cat.name))
                        },
                        onProductClick = { p ->
                            viewModel.navigateTo(Screen.ProductDetail(p.id))
                        },
                        onAddToCart = { p -> viewModel.addToCart(p, 1) },
                        onToggleWishlist = { p ->
                            val isWishlisted = wishlistProducts.any { it.id == p.id }
                            viewModel.toggleWishlist(p, isWishlisted)
                        },
                        onQuickView = { p -> viewModel.openQuickView(p) },
                        onViewAllProducts = { cat, badge ->
                            viewModel.navigateTo(
                                Screen.ProductListing(
                                    category = cat,
                                    filterBadge = badge,
                                    title = cat ?: badge ?: "All Products"
                                )
                            )
                        },
                        onSellerRegisterClick = { viewModel.navigateTo(Screen.SellerDashboard) },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }

                is Screen.Categories -> {
                    CategoriesScreen(
                        categories = allCategories,
                        onCategoryClick = { cat ->
                            viewModel.navigateTo(Screen.ProductListing(category = cat.name, title = cat.name))
                        }
                    )
                }

                is Screen.ProductListing -> {
                    ProductListingScreen(
                        title = screen.title,
                        initialCategory = screen.category,
                        initialBadge = screen.filterBadge,
                        categories = allCategories,
                        products = allProducts,
                        wishlistProducts = wishlistProducts,
                        onProductClick = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) },
                        onAddToCart = { p -> viewModel.addToCart(p, 1) },
                        onToggleWishlist = { p ->
                            val isWishlisted = wishlistProducts.any { it.id == p.id }
                            viewModel.toggleWishlist(p, isWishlisted)
                        },
                        onQuickView = { p -> viewModel.openQuickView(p) }
                    )
                }

                is Screen.ProductDetail -> {
                    val product = allProducts.find { it.id == screen.productId }
                        ?: allProducts.firstOrNull()

                    if (product != null) {
                        ProductDetailScreen(
                            product = product,
                            allProducts = allProducts,
                            wishlistProducts = wishlistProducts,
                            currentPincode = deliveryPincode,
                            repository = viewModel.repository,
                            onAddToCart = { p, qty -> viewModel.addToCart(p, qty) },
                            onBuyNow = { p, qty ->
                                viewModel.addToCart(p, qty)
                                viewModel.navigateTo(Screen.Checkout)
                            },
                            onToggleWishlist = { p ->
                                val isWishlisted = wishlistProducts.any { it.id == p.id }
                                viewModel.toggleWishlist(p, isWishlisted)
                            },
                            onProductClick = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) },
                            onAddReview = { pid, r, t, c -> viewModel.addReview(pid, r, t, c) },
                            onShowToast = { msg -> viewModel.showToast(msg) }
                        )
                    }
                }

                is Screen.Search -> {
                    SearchScreen(
                        initialQuery = screen.initialQuery,
                        products = allProducts,
                        wishlistProducts = wishlistProducts,
                        recentSearches = recentSearches,
                        onProductClick = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) },
                        onAddToCart = { p -> viewModel.addToCart(p, 1) },
                        onToggleWishlist = { p ->
                            val isWishlisted = wishlistProducts.any { it.id == p.id }
                            viewModel.toggleWishlist(p, isWishlisted)
                        },
                        onQuickView = { p -> viewModel.openQuickView(p) },
                        onQuerySearched = { q -> viewModel.updateSearchQuery(q) },
                        onClearRecentSearches = { viewModel.clearRecentSearches() },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }

                is Screen.Cart -> {
                    CartScreen(
                        cartItems = cartItems,
                        appliedCoupon = appliedCoupon,
                        onUpdateQuantity = { pid, qty -> viewModel.updateCartQuantity(pid, qty) },
                        onRemoveItem = { pid -> viewModel.removeFromCart(pid) },
                        onSaveForLater = { p -> viewModel.moveWishlistToCart(p) },
                        onApplyCoupon = { code, subtotal -> viewModel.applyCoupon(code, subtotal) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onProceedToCheckout = { viewModel.navigateTo(Screen.Checkout) },
                        onContinueShopping = { viewModel.navigateTo(Screen.Home) },
                        onProductClick = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) }
                    )
                }

                is Screen.Checkout -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        appliedCoupon = appliedCoupon,
                        addresses = addresses,
                        onSaveAddress = { addr -> viewModel.saveAddress(addr) },
                        onPlaceOrder = { addr, pMethod, dOption ->
                            viewModel.placeOrder(addr, pMethod, dOption)
                        },
                        onOrderPlaced = { orderId ->
                            viewModel.navigateTo(Screen.OrderSuccess(orderId))
                        },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }

                is Screen.OrderSuccess -> {
                    val order = orders.find { it.id == screen.orderId } ?: orders.firstOrNull()
                    OrderSuccessScreen(
                        orderId = screen.orderId,
                        order = order,
                        onTrackOrder = { orderId ->
                            viewModel.navigateTo(Screen.OrderDetail(orderId))
                        },
                        onContinueShopping = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Orders -> {
                    OrdersScreen(
                        orders = orders,
                        onOrderClick = { ord -> viewModel.navigateTo(Screen.OrderDetail(ord.id)) },
                        onShopNow = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.OrderDetail -> {
                    val order = orders.find { it.id == screen.orderId } ?: orders.firstOrNull()
                    if (order != null) {
                        OrderDetailScreen(
                            order = order,
                            onContactSupport = {
                                viewModel.showToast("Support email: support@bizzycart.in | Helpline: 1800-249-9922")
                            }
                        )
                    }
                }

                is Screen.Wishlist -> {
                    WishlistScreen(
                        wishlistProducts = wishlistProducts,
                        onMoveToCart = { p -> viewModel.moveWishlistToCart(p) },
                        onRemoveFromWishlist = { p -> viewModel.toggleWishlist(p, true) },
                        onProductClick = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) },
                        onExploreClick = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Account -> {
                    AccountScreen(
                        userProfile = userProfile,
                        ordersCount = orders.size,
                        wishlistCount = wishlistProducts.size,
                        addressCount = addresses.size,
                        onNavigateToOrders = { viewModel.navigateTo(Screen.Orders) },
                        onNavigateToWishlist = { viewModel.navigateTo(Screen.Wishlist) },
                        onNavigateToAdmin = { viewModel.navigateTo(Screen.AdminDashboard) },
                        onNavigateToSeller = { viewModel.navigateTo(Screen.SellerDashboard) },
                        onOpenAuthDialog = { viewModel.openAuthDialog() },
                        onLogout = { viewModel.logout() },
                        onSyncFirebase = { viewModel.syncFirebaseDatabase() },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        products = allProducts,
                        categories = allCategories,
                        orders = orders,
                        coupons = coupons,
                        onAddProduct = { p -> viewModel.adminAddProduct(p) },
                        onDeleteProduct = { p -> viewModel.adminDeleteProduct(p) },
                        onAddCategory = { c -> viewModel.adminAddCategory(c) },
                        onDeleteCategory = { c -> viewModel.adminDeleteCategory(c) },
                        onUpdateOrderStatus = { oid, status -> viewModel.adminUpdateOrderStatus(oid, status) },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }

                is Screen.SellerDashboard -> {
                    SellerDashboardScreen(
                        userProfile = userProfile,
                        products = allProducts,
                        onRegisterSeller = { storeName -> viewModel.registerAsSeller(storeName) },
                        onAddProduct = { p -> viewModel.adminAddProduct(p) },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )
                }
            }
        }
    }

    // Pincode Selector Dialog
    if (showPincodeDialog) {
        PincodeDialog(
            currentPincode = deliveryPincode,
            onDismiss = { showPincodeDialog = false },
            onApply = { pin, city -> viewModel.setDeliveryPincode(pin, city) }
        )
    }

    // Quick View Dialog
    quickViewProduct?.let { product ->
        QuickViewDialog(
            product = product,
            onDismiss = { viewModel.closeQuickView() },
            onAddToCart = { p -> viewModel.addToCart(p, 1) },
            onViewFullDetails = { p -> viewModel.navigateTo(Screen.ProductDetail(p.id)) }
        )
    }

    // Auth Dialog
    if (showAuthDialog) {
        AuthDialog(
            onDismiss = { viewModel.closeAuthDialog() },
            onLogin = { email, name -> viewModel.login(email, name) },
            onShowToast = { msg -> viewModel.showToast(msg) }
        )
    }
}
