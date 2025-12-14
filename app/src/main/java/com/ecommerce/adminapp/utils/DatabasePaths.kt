package com.ecommerce.adminapp.utils

/**
 * Central location for all Firebase Realtime Database paths
 * Based on the Fly Sneaker database schema
 */
object DatabasePaths {
    // Main nodes
    const val PRODUCTS = "products"
    const val PRODUCT_VARIANTS = "productVariants"
    const val PRODUCT_VARIANTS_BY_PRODUCT = "productVariantsByProduct"
    const val CATEGORIES = "categories"
    const val CATEGORY_PRODUCTS = "categoryProducts"
    const val BRANDS = "brands"
    const val USERS = "users"
    const val CARTS = "carts"
    const val ORDERS = "orders"
    const val ORDER_ITEMS = "orderItems"
    const val USER_ORDERS = "userOrders"
    const val INVENTORY_LOGS = "inventoryLogs"
    const val BANNERS = "banners"
    
    // Legacy support (for backward compatibility)
    const val ADMINS = "Admins"
    const val ITEMS = "Items"
    const val CATEGORY_LEGACY = "Category"
    const val BANNER_LEGACY = "Banner"
    
    /**
     * Get path for a specific product
     */
    fun product(productId: String) = "$PRODUCTS/$productId"
    
    /**
     * Get path for a specific product variant
     */
    fun productVariant(variantId: String) = "$PRODUCT_VARIANTS/$variantId"
    
    /**
     * Get path for variants of a specific product
     */
    fun productVariantsByProduct(productId: String) = "$PRODUCT_VARIANTS_BY_PRODUCT/$productId"
    
    /**
     * Get path for a specific category
     */
    fun category(categoryId: String) = "$CATEGORIES/$categoryId"
    
    /**
     * Get path for products in a specific category
     */
    fun categoryProducts(categoryId: String) = "$CATEGORY_PRODUCTS/$categoryId"
    
    /**
     * Get path for a specific brand
     */
    fun brand(brandId: String) = "$BRANDS/$brandId"
    
    /**
     * Get path for a specific user
     */
    fun user(userId: String) = "$USERS/$userId"
    
    /**
     * Get path for a user's cart
     */
    fun cart(userId: String) = "$CARTS/$userId"
    
    /**
     * Get path for a specific cart item
     */
    fun cartItem(userId: String, variantId: String) = "$CARTS/$userId/$variantId"
    
    /**
     * Get path for a specific order
     */
    fun order(orderId: String) = "$ORDERS/$orderId"
    
    /**
     * Get path for items in a specific order
     */
    fun orderItems(orderId: String) = "$ORDER_ITEMS/$orderId"
    
    /**
     * Get path for a user's orders
     */
    fun userOrders(userId: String) = "$USER_ORDERS/$userId"
    
    /**
     * Get path for a specific inventory log
     */
    fun inventoryLog(logId: String) = "$INVENTORY_LOGS/$logId"
    
    /**
     * Get path for a specific banner
     */
    fun banner(bannerId: String) = "$BANNERS/$bannerId"
}
