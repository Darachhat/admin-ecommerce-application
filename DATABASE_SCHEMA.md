# Fly Sneaker - Database Schema

## Overview

This document describes the Firebase Realtime Database schema for the Fly Sneaker e-commerce application.

## Database Structure

### 1. Products (`/products`)

Stores core product information (sneakers).

```json
{
  "products": {
    "prod_001": {
      "title": "Nike Air Max 270",
      "brandId": "brand_nike",
      "categoryId": "cat_running",
      "price": 150,
      "currency": "USD",
      "thumbnail": "https://cdn.example.com/p1.png",
      "active": true,
      "createdAt": 1734095200
    }
  }
}
```

**Fields:**

- `title` (string): Product name
- `brandId` (string): Reference to brand
- `categoryId` (string): Reference to category
- `price` (number): Base price in specified currency
- `currency` (string): Currency code (USD, EUR, etc.)
- `thumbnail` (string): Main product image URL
- `active` (boolean): Whether product is visible to customers
- `createdAt` (timestamp): Unix timestamp of creation

---

### 2. Product Variants (`/productVariants`)

Stores size/color combinations with individual pricing and stock.

```json
{
  "productVariants": {
    "var_001": {
      "productId": "prod_001",
      "size": "8",
      "color": "black",
      "sku": "NIKE-AM270-BLK-8",
      "price": 150,
      "stock": 45,
      "active": true
    }
  }
}
```

**Fields:**

- `productId` (string): Reference to parent product
- `size` (string): Shoe size
- `color` (string): Color variant
- `sku` (string): Stock Keeping Unit (unique identifier)
- `price` (number): Variant-specific price (can differ from base)
- `stock` (number): Available quantity
- `active` (boolean): Whether variant is available for purchase

---

### 3. Product Variants by Product (`/productVariantsByProduct`)

Index for quick lookup of all variants belonging to a product.

```json
{
  "productVariantsByProduct": {
    "prod_001": {
      "var_001": true,
      "var_002": true
    }
  }
}
```

**Purpose:** Efficiently query all variants for a specific product without scanning entire variants collection.

---

### 4. Categories (`/categories`)

Product categories (Running, Basketball, Casual, etc.).

```json
{
  "categories": {
    "cat_running": {
      "name": "Running Shoes",
      "active": true
    }
  }
}
```

**Fields:**

- `name` (string): Category display name
- `active` (boolean): Whether category is visible

---

### 5. Category Products (`/categoryProducts`)

Index for products in each category.

```json
{
  "categoryProducts": {
    "cat_running": {
      "prod_001": true,
      "prod_005": true
    }
  }
}
```

**Purpose:** Efficiently query all products in a specific category.

---

### 6. Brands (`/brands`)

Sneaker brands (Nike, Adidas, Puma, etc.).

```json
{
  "brands": {
    "brand_nike": {
      "name": "Nike",
      "active": true
    }
  }
}
```

**Fields:**

- `name` (string): Brand display name
- `active` (boolean): Whether brand is visible

---

### 7. Users (`/users`)

User accounts (customers and admins).

```json
{
  "users": {
    "uid_user_1": {
      "email": "user@example.com",
      "role": "user",
      "createdAt": 1734095200
    },
    "uid_admin_1": {
      "email": "admin@example.com",
      "role": "admin",
      "createdAt": 1734095200
    }
  }
}
```

**Fields:**

- `email` (string): User email address
- `role` (string): "user" or "admin"
- `createdAt` (timestamp): Unix timestamp of registration

---

### 8. Carts (`/carts`)

Shopping cart data per user.

```json
{
  "carts": {
    "uid_user_1": {
      "var_001": {
        "quantity": 2,
        "priceSnapshot": 150
      }
    }
  }
}
```

**Structure:** `/carts/{userId}/{variantId}`

**Fields:**

- `quantity` (number): Number of items
- `priceSnapshot` (number): Price at time of adding to cart

---

### 9. Orders (`/orders`)

Order records.

```json
{
  "orders": {
    "order_001": {
      "userId": "uid_user_1",
      "status": "paid",
      "total": 300,
      "currency": "USD",
      "createdAt": 1734095300
    }
  }
}
```

**Fields:**

- `userId` (string): Customer who placed the order
- `status` (string): "pending", "paid", "shipped", "delivered", "cancelled"
- `total` (number): Total order amount
- `currency` (string): Currency code
- `createdAt` (timestamp): Unix timestamp

---

### 10. Order Items (`/orderItems`)

Line items for each order.

```json
{
  "orderItems": {
    "order_001": {
      "var_001": {
        "productId": "prod_001",
        "price": 150,
        "quantity": 2,
        "subtotal": 300
      }
    }
  }
}
```

**Structure:** `/orderItems/{orderId}/{variantId}`

**Fields:**

- `productId` (string): Product reference
- `price` (number): Price at time of purchase
- `quantity` (number): Number of items
- `subtotal` (number): price × quantity

---

### 11. User Orders (`/userOrders`)

Index for user's order history.

```json
{
  "userOrders": {
    "uid_user_1": {
      "order_001": {
        "total": 300,
        "createdAt": 1734095300
      }
    }
  }
}
```

**Purpose:** Efficiently query all orders for a specific user.

---

### 12. Inventory Logs (`/inventoryLogs`)

Audit trail for stock changes.

```json
{
  "inventoryLogs": {
    "log_001": {
      "variantId": "var_001",
      "delta": -2,
      "reason": "order",
      "refId": "order_001",
      "createdAt": 1734095301
    }
  }
}
```

**Fields:**

- `variantId` (string): Which variant was affected
- `delta` (number): Change in stock (negative for sales, positive for restocks)
- `reason` (string): "order", "restock", "adjustment", "return"
- `refId` (string): Reference ID (e.g., order ID)
- `createdAt` (timestamp): Unix timestamp

---

### 13. Banners (`/banners`)

Homepage promotional banners.

```json
{
  "banners": {
    "banner_001": {
      "imageUrl": "https://cdn.example.com/banner.png",
      "active": true
    }
  }
}
```

**Fields:**

- `imageUrl` (string): Banner image URL
- `active` (boolean): Whether banner is displayed

---

## Security Rules

See `database.rules.json` for detailed security rules. Key principles:

1. **Products/Categories/Brands/Banners**:

   - Read: Public
   - Write: Admins only

2. **Users**:

   - Read: Self only
   - Write: Self only (limited fields)

3. **Carts**:

   - Read/Write: Owner only

4. **Orders**:

   - Read: Owner or admin
   - Write: Owner (creation only), admin (status updates)

5. **Inventory Logs**:
   - Read: Admin only
   - Write: System/Admin only

---

## Indexing Strategy

The schema uses denormalization for common queries:

- **productVariantsByProduct**: Quick variant lookup by product
- **categoryProducts**: Quick product lookup by category
- **userOrders**: Quick order history for users

---

## Data Model Classes

All entities have corresponding Kotlin data classes in `/models`:

- `Product.kt`
- `ProductVariant.kt`
- `Category.kt`
- `Brand.kt`
- `User.kt`
- `CartItem.kt`
- `Order.kt`
- `OrderItem.kt`
- `InventoryLog.kt`
- `Banner.kt`

---

## Database Paths

Use `DatabasePaths.kt` utility for consistent path references:

```kotlin
import com.ecommerce.adminapp.utils.DatabasePaths

// Get reference to products
val productsRef = database.getReference(DatabasePaths.PRODUCTS)

// Get reference to specific product
val productRef = database.getReference(DatabasePaths.product("prod_001"))

// Get variants for a product
val variantsRef = database.getReference(DatabasePaths.productVariantsByProduct("prod_001"))
```

---

## Migration from Legacy Schema

Legacy paths are still supported:

- `/Admins` → `/users` (with role="admin")
- `/Items` → `/products`
- `/Category` → `/categories`
- `/Banner` → `/banners`

Update your code to use new paths for consistency.

---

## Examples

### Add a Product with Variants

```kotlin
// 1. Add product
val product = Product(
    title = "Nike Air Max 270",
    brandId = "brand_nike",
    categoryId = "cat_running",
    price = 150.0,
    thumbnail = "https://..."
)
val productId = productRepository.addProduct(product).getOrThrow()

// 2. Add variants
val variant1 = ProductVariant(
    productId = productId,
    size = "8",
    color = "black",
    sku = "NIKE-AM270-BLK-8",
    price = 150.0,
    stock = 45
)
productRepository.addVariant(variant1)
```

### Query Products by Category

```kotlin
val categoryId = "cat_running"
database.getReference(DatabasePaths.categoryProducts(categoryId))
    .addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val productIds = snapshot.children.mapNotNull { it.key }
            // Fetch full product data for each ID
        }
    })
```

---

## Best Practices

1. **Always use transactions** when updating stock to prevent race conditions
2. **Create inventory logs** for every stock change
3. **Snapshot prices** in carts and orders (products prices may change)
4. **Use indexes** (productVariantsByProduct, categoryProducts, etc.)
5. **Validate data** on client before saving
6. **Use DatabasePaths** constants for consistency

---

## Sample Data

See `/Backend/sample-products.json` for sample data to populate the database.
