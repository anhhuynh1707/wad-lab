# Secure Customer API with JWT Authentication

## Student Information
- **Name:** Huỳnh Tuấn Anh
- **Student ID:** ITITIU23003
- **Class:** Web Application Development_Group01_lab03_Tue1234

## Features Implemented

### Authentication
- [x] User registration
- [x] User login with JWT
- [x] Logout
- [x] Get current user
- [x] Password hashing with BCrypt

### Authorization
- [x] Role-based access control (USER, ADMIN)
- [x] Protected endpoints
- [x] Method-level security with @PreAuthorize

### Additional Features
- [x] Change password
- [x] Forgot password / Reset password
- [x] User profile management
- [x] Admin user management
- [x] Refresh token with rotation
- [ ] Email verification (Bonus)
- [ ] Login Activity Log (Bonus)
- [ ] Two-Factor Authentication - 2FA (Bonus)

## API Endpoints

### Public Endpoints
- POST /api/auth/register
- POST /api/auth/login
- POST /api/auth/forgot-password
- POST /api/auth/reset-password
- POST /api/auth/refresh

### Protected Endpoints (Authenticated)
- GET /api/auth/me
- POST /api/auth/logout
- GET /api/customers
- GET /api/customers/{id}
- PUT /api/auth/change-password
- GET /api/users/profile
- PUT /api/users/profile/update
- DELETE /api/users/account

### Admin Only Endpoints
- POST /api/customers
- PUT /api/customers/{id}
- DELETE /api/customers/{id}
- GET /api/admin/users
- PUT /api/admin/users/{id}/role
- PATCH /api/admin/users/{id}/status

## Test Users
| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| john | password123 | USER |
| jane | password123 | USER |

## How to Run
1. Create database: `customer_management`
2. Run SQL scripts to create tables
3. Update `application.properties` with your MySQL credentials
4. Run: `mvn spring-boot:run`
5. Test with Thunder Client using provided collection

## Testing
Import Postman collection: `Secure_Customer_API.postman_collection.json`

All endpoints tested and working.

See screenshots folder for testing with Postman.

## Security
- Passwords hashed with BCrypt
- JWT tokens with 24-hour expiration
- Stateless authentication
- CORS enabled for frontend
- Protected endpoints with Spring Security

## Known Issues
- Take more time to understand the JWT and how it works.
- Understand the access token and refresh token.
- Review and practice more with Spring Boot.
- Knowing more about what permissions the administrator can have in the management system.

## Time Spent
Approximately: 8 hours
