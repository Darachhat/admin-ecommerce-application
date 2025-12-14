#!/usr/bin/env python3
"""
Script to create admin users in Firebase for the AdminApp
This script creates test admin users and sets their admin privileges
"""

import firebase_admin
from firebase_admin import credentials, auth, db
import sys

def initialize_firebase():
    """Initialize Firebase Admin SDK"""
    try:
        # Use the service account key from Backend folder
        import os
        script_dir = os.path.dirname(os.path.abspath(__file__))
        service_account_path = os.path.join(script_dir, '..', 'Backend', 'serviceAccountKey.json')
        
        if not os.path.exists(service_account_path):
            print(f"❌ Service account key not found at: {service_account_path}")
            print("📝 Please ensure serviceAccountKey.json exists in the Backend folder")
            return False
            
        cred = credentials.Certificate(service_account_path)
        firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://ecommerce-app-ba8ed-default-rtdb.firebaseio.com'
        })
        print("✅ Firebase initialized successfully")
        return True
    except Exception as e:
        print(f"❌ Error initializing Firebase: {e}")
        return False

def create_admin_user(email, password, display_name="Admin User"):
    """Create a new admin user in Firebase Authentication"""
    try:
        # Create user in Firebase Auth
        user = auth.create_user(
            email=email,
            password=password,
            display_name=display_name,
            email_verified=True
        )
        
        print(f"✅ User created successfully:")
        print(f"   Email: {email}")
        print(f"   UID: {user.uid}")
        
        # Set admin privilege in Realtime Database
        ref = db.reference('Admins')
        ref.child(user.uid).set({
            'email': email,
            'displayName': display_name,
            'isAdmin': True,
            'createdAt': firebase_admin.db.ServerValue.TIMESTAMP
        })
        
        print(f"✅ Admin privileges set in database")
        
        # Set custom claims for admin
        auth.set_custom_user_claims(user.uid, {'admin': True})
        print(f"✅ Custom admin claims set")
        
        return user.uid
        
    except auth.EmailAlreadyExistsError:
        print(f"⚠️  User with email {email} already exists")
        # Try to get existing user and update admin status
        try:
            user = auth.get_user_by_email(email)
            ref = db.reference('Admins')
            ref.child(user.uid).set({
                'email': email,
                'displayName': display_name,
                'isAdmin': True,
                'updatedAt': firebase_admin.db.ServerValue.TIMESTAMP
            })
            auth.set_custom_user_claims(user.uid, {'admin': True})
            print(f"✅ Admin privileges updated for existing user")
            return user.uid
        except Exception as e:
            print(f"❌ Error updating existing user: {e}")
            return None
            
    except Exception as e:
        print(f"❌ Error creating user: {e}")
        return None

def list_admin_users():
    """List all admin users from the database"""
    try:
        ref = db.reference('Admins')
        admins = ref.get()
        
        if admins:
            print("\n📋 Current Admin Users:")
            print("-" * 60)
            for uid, data in admins.items():
                print(f"Email: {data.get('email', 'N/A')}")
                print(f"Name: {data.get('displayName', 'N/A')}")
                print(f"UID: {uid}")
                print(f"Is Admin: {data.get('isAdmin', False)}")
                print("-" * 60)
        else:
            print("\n⚠️  No admin users found")
            
    except Exception as e:
        print(f"❌ Error listing admins: {e}")

def main():
    """Main function"""
    print("\n" + "="*60)
    print("🔧 Firebase Admin User Setup")
    print("="*60 + "\n")
    
    # Initialize Firebase
    if not initialize_firebase():
        sys.exit(1)
    
    # Create default admin users
    print("\n📝 Creating default admin users...\n")
    
    # Admin 1 - Test account
    create_admin_user(
        email="admin@ecommerce.com",
        password="admin123456",
        display_name="Admin User"
    )
    
    print("\n")
    
    # Admin 2 - Test account 2
    create_admin_user(
        email="test@admin.com",
        password="test123456",
        display_name="Test Admin"
    )
    
    # List all admin users
    print("\n")
    list_admin_users()
    
    print("\n" + "="*60)
    print("✅ Admin setup completed!")
    print("="*60)
    print("\n📱 You can now login with:")
    print("   Email: admin@ecommerce.com")
    print("   Password: admin123456")
    print("\n   OR")
    print("\n   Email: test@admin.com")
    print("   Password: test123456")
    print("\n" + "="*60 + "\n")

if __name__ == "__main__":
    main()
