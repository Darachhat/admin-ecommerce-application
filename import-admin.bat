@echo off
echo Importing admin data to Firebase...
echo.
echo Make sure you have Firebase CLI installed and logged in:
echo   npm install -g firebase-tools
echo   firebase login
echo.
pause

firebase database:set /Admins/HIBqqlGl1ieUrWScq2aQALpTZ3K3 admin-data.json --project ecommerce-app-ba8ed --confirm

echo.
echo Done! Now try logging in with:
echo Email: admin@ecommerce.com
echo Password: (whatever you set in Firebase Authentication)
pause
