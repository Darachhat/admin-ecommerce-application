#!/usr/bin/env python3
"""
Script to check Firebase Auth user UID
This helps identify UID mismatches between Firebase Auth and Realtime Database
"""

import json
import sys

def main():
    print("=" * 60)
    print("FIREBASE USER UID CHECKER")
    print("=" * 60)
    
    # Read admin-data.json
    try:
        with open('admin-data.json', 'r') as f:
            admin_data = json.load(f)
        
        admins = admin_data.get('Admins', {})
        
        if not admins:
            print("\n❌ No admin users found in admin-data.json")
            return
        
        print("\n📋 Admin users in database:")
        print("-" * 60)
        
        for uid, user_data in admins.items():
            email = user_data.get('email', 'N/A')
            display_name = user_data.get('displayName', 'N/A')
            is_admin = user_data.get('isAdmin', False)
            
            print(f"\n✓ Email: {email}")
            print(f"  Name: {display_name}")
            print(f"  UID: {uid}")
            print(f"  Admin: {is_admin}")
        
        print("\n" + "=" * 60)
        print("TROUBLESHOOTING STEPS:")
        print("=" * 60)
        print("""
1. Check the logs for the actual UID from Firebase Auth
   Look for: "Login successful. User UID: XXXXX"

2. Compare with the UID in admin-data.json above

3. If they DON'T MATCH:
   a. Option 1: Update admin-data.json with the actual UID
      - Replace the UID in admin-data.json with the one from logs
      - Run: python import-admin.bat
   
   b. Option 2: Delete and recreate the Firebase Auth user
      - Go to Firebase Console → Authentication
      - Delete the existing user
      - Create new user with same email/password
      - Note the new UID
      - Update admin-data.json with new UID
      - Run: python import-admin.bat

4. After fixing, re-import the admin data:
   python import-admin.bat

5. Test login again
""")
        
    except FileNotFoundError:
        print("\n❌ Error: admin-data.json not found")
        print("Make sure you're running this from the AdminApp directory")
    except json.JSONDecodeError as e:
        print(f"\n❌ Error parsing admin-data.json: {e}")
    except Exception as e:
        print(f"\n❌ Unexpected error: {e}")

if __name__ == "__main__":
    main()
