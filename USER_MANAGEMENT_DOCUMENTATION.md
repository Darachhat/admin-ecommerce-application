# User Management Feature - Admin App

## Overview

A complete User Management system has been implemented for the Fly Sneaker Admin App, allowing administrators to view, search, manage user roles, and delete users.

## Features Implemented

### 1. User Repository (`UserRepository.kt`)

**Location**: `com.ecommerce.adminapp.repository.UserRepository`

**Capabilities**:

- **Get All Users**: Real-time Flow-based user list retrieval
- **Get Single User**: Fetch user details by ID
- **Update User**: Update user information with custom fields
- **Delete User**: Remove users from the system
- **Update User Role**: Toggle between "user" and "admin" roles
- **Search Users**: Search users by email
- **Filter by Role**: Get users filtered by their role (admin/user)

### 2. Users Fragment (`UsersFragment.kt`)

**Location**: `com.ecommerce.adminapp.ui.users.UsersFragment`

**Features**:

- **Real-time User List**: Displays all users with automatic updates
- **Search Functionality**: Search by email, ID, or role
- **User Statistics**: Shows total users, admin count, and user count
- **User Details Dialog**: View and edit user information
- **Role Management**: Quick toggle between admin and user roles
- **Delete Functionality**: Remove users with confirmation dialog
- **Empty State**: Displays message when no users found
- **Loading States**: Progress bar during operations

### 3. User Adapter (`UserAdapter.kt`)

**Location**: `com.ecommerce.adminapp.ui.adapter.UserAdapter`

**Features**:

- **Efficient RecyclerView**: Uses DiffUtil for optimal performance
- **User Information Display**: Shows email, ID, creation date
- **Role Badge**: Visual indicator for user/admin status
- **Interactive Controls**: Click to view details, delete button, role toggle
- **Date Formatting**: Human-readable creation date display

## Layouts

### 1. Fragment Users Layout (`fragment_users.xml`)

**Components**:

- SearchView for filtering users
- Statistics card showing user counts
- RecyclerView for user list
- Empty state view
- Progress bar for loading states

### 2. User Item Layout (`item_user.xml`)

**Components**:

- User icon
- Email display
- User ID
- Creation date
- Role chip (clickable for quick toggle)
- Delete button
- Material Card design

### 3. User Details Dialog (`dialog_user_details.xml`)

**Components**:

- Email input (read-only)
- User ID display
- Role selection toggle (User/Admin)
- Creation date display
- Update and Delete actions

## Navigation Integration

### Bottom Navigation Menu

Added "Users" tab to the bottom navigation menu, accessible alongside:

- Products
- Categories
- Orders
- Banners
- Brands

### Navigation Graph

Users fragment registered in the navigation graph with proper routing.

## String Resources

All user-facing text is properly externalized:

- `nav_users`: Navigation label
- `search_users`: Search hint
- `no_users_found`: Empty state message
- `role`, `role_user`, `role_admin`: Role labels
- And more...

## Database Structure

Uses Firebase Realtime Database with path: `users/`

Each user has:

- `id`: Unique identifier
- `email`: User email address
- `role`: "user" or "admin"
- `createdAt`: Unix timestamp (seconds)

## Usage

### Accessing User Management

1. Open the admin app
2. Tap the "Users" tab in the bottom navigation
3. View all registered users

### Managing Users

- **Search**: Use the search bar to filter by email, ID, or role
- **View Details**: Tap on any user card
- **Change Role**: Click the role chip or use the details dialog
- **Delete User**: Click the delete button with confirmation

### Viewing Statistics

The top statistics card shows:

- Total number of users
- Number of admins
- Number of regular users

## Security Considerations

- Only authenticated admins can access this feature
- All operations are performed through the repository layer
- Firebase security rules should be configured to restrict user management operations to admin users only

## Future Enhancements (Recommendations)

1. Add bulk operations (select multiple users)
2. Export user list to CSV
3. Advanced filters (date range, registration source)
4. User activity logs
5. Email verification status
6. Password reset capability from admin panel
7. User blocking/suspension feature
8. Custom user metadata fields

## Dependencies

- Firebase Realtime Database
- Kotlin Coroutines
- AndroidX RecyclerView
- Material Design Components
- ViewBinding

## Testing Checklist

- [ ] Users list loads correctly
- [ ] Search filters users properly
- [ ] Role toggle updates in real-time
- [ ] Delete confirmation works
- [ ] Statistics update correctly
- [ ] Empty state displays when no users
- [ ] Loading states show during operations
- [ ] Error handling works properly
