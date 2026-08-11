---
title: LAB 9 SPRING SECURITY & JWT AUTHENTICATION

---

# LAB 9: SPRING SECURITY & JWT AUTHENTICATION
## Setup Guide & Sample Code

**Course:** Web Application Development  
**Duration:** 2.5 hours  
**Prerequisites:** Lab 8 completed (REST API + DTO Pattern)

> **Note:** This lab focuses on securing REST APIs with JWT authentication. Read this BEFORE the lab session.

---

## 📋 TABLE OF CONTENTS

1. [Why Spring Security?](#1-why-spring-security)
2. [Authentication vs Authorization](#2-authentication-vs-authorization)
3. [Understanding JWT](#3-understanding-jwt)
4. [Project Setup](#4-project-setup)
5. [Sample Code - Security Implementation](#5-sample-code-security-implementation)
6. [JWT Token Flow](#6-jwt-token-flow)
7. [Testing Authentication](#7-testing-authentication)
8. [Comparison: Session vs JWT](#8-comparison-session-vs-jwt)

---

## 1. WHY SPRING SECURITY?

### Problems with Unsecured APIs

**From Lab 8 (No Security):**
```java
@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {
    
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        // Anyone can access this!
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        // Anyone can delete!
        customerService.deleteCustomer(id);
        return ResponseEntity.ok().build();
    }
}
```

**Security Issues:**

1. ❌ **No Authentication** - Can't identify who is making requests
2. ❌ **No Authorization** - Any user can access any endpoint
3. ❌ **No Data Protection** - Sensitive operations are exposed
4. ❌ **No User Management** - Can't track user activities
5. ❌ **Easy to Attack** - Vulnerable to unauthorized access
6. ❌ **No Role-Based Access** - Can't differentiate admin vs regular user

### Benefits of Spring Security

✅ **Authentication** - Verify user identity (login)  
✅ **Authorization** - Control access to resources (roles/permissions)  
✅ **Password Encryption** - Secure password storage with BCrypt  
✅ **Token Management** - JWT for stateless authentication  
✅ **Protection** - CSRF, XSS, SQL Injection prevention  
✅ **Industry Standard** - Battle-tested security framework

---

## 2. AUTHENTICATION VS AUTHORIZATION

### Authentication (Who are you?)

**Definition:** Verifying the identity of a user.

**Example:**
```
User: "I am john@example.com"
System: "Prove it with your password"
User: "My password is: ********"
System: ✅ "Confirmed! You are John Doe"
```

**Implementation:**
- Login with username/password
- Issue authentication token (JWT)
- User includes token in subsequent requests

---

### Authorization (What can you do?)

**Definition:** Determining what an authenticated user can access.

**Example:**
```
User John (Role: USER): 
✅ Can view customers
✅ Can update own profile
❌ Cannot delete customers
❌ Cannot access admin panel

User Admin (Role: ADMIN):
✅ Can view customers
✅ Can delete customers
✅ Can access admin panel
✅ Full system access
```

**Implementation:**
- Role-based access control (RBAC)
- Method-level security with @PreAuthorize
- Endpoint protection with configuration

---

### Comparison

| Aspect | Authentication | Authorization |
|--------|---------------|---------------|
| **Question** | Who are you? | What can you do? |
| **Process** | Login verification | Permission checking |
| **When** | Before accessing system | Before accessing resources |
| **Example** | Username/Password | User roles/permissions |
| **Spring Security** | UserDetailsService | @PreAuthorize, SecurityConfig |
| **HTTP Status** | 401 Unauthorized | 403 Forbidden |

---

## 3. UNDERSTANDING JWT

### What is JWT?

**JWT (JSON Web Token)** - A compact, URL-safe token for securely transmitting information between parties.

**Structure:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

HEADER.PAYLOAD.SIGNATURE
```

---

### JWT Components

**1. HEADER** (Algorithm & Token Type)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**2. PAYLOAD** (Claims - User Data)
```json
{
  "sub": "john@example.com",
  "name": "John Doe",
  "role": "USER",
  "iat": 1516239022,
  "exp": 1516242622
}
```

**3. SIGNATURE** (Verification)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

---

### How JWT Works

```
┌─────────────┐                                    ┌─────────────┐
│   CLIENT    │                                    │   SERVER    │
│  (Browser/  │                                    │  (Spring    │
│   Mobile)   │                                    │   Boot)     │
└──────┬──────┘                                    └──────┬──────┘
       │                                                  │
       │ 1. POST /api/auth/login                         │
       │    {username, password}                         │
       ├────────────────────────────────────────────────>│
       │                                                  │
       │                                 2. Verify User   │
       │                                    Hash Password │
       │                                    Generate JWT  │
       │                                                  │
       │ 3. Response: 200 OK                             │
       │    {token: "eyJhbG..."}                         │
       │<────────────────────────────────────────────────┤
       │                                                  │
       │ 4. Store token (localStorage)                   │
       │                                                  │
       │ 5. GET /api/customers                           │
       │    Header: Authorization: Bearer eyJhbG...      │
       ├────────────────────────────────────────────────>│
       │                                                  │
       │                                 6. Validate JWT  │
       │                                    Extract User  │
       │                                    Check Permissions
       │                                                  │
       │ 7. Response: 200 OK                             │
       │    {customers: [...]}                           │
       │<────────────────────────────────────────────────┤
```

---

### JWT vs Session

| Feature | JWT | Session |
|---------|-----|---------|
| **Storage** | Client-side (localStorage) | Server-side (memory/database) |
| **Scalability** | Excellent (stateless) | Limited (server state) |
| **Mobile Apps** | Perfect | Challenging |
| **Microservices** | Ideal | Complex |
| **Logout** | Manual (token expiry) | Simple (destroy session) |
| **Size** | Larger (contains data) | Smaller (just ID) |
| **Security** | Token can be stolen | Session ID can be stolen |
| **Usage** | Modern SPAs, Mobile Apps | Traditional web apps |

---

## 4. PROJECT SETUP

### 4.1 Add Dependencies

**Update `pom.xml`:**

```xml
<dependencies>
    <!-- Existing dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- NEW: Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- NEW: JWT Support -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

### 4.2 Project Structure

```
secure-customer-api/
│
├── src/main/java/com/example/securecustomerapi/
│   ├── SecureCustomerApiApplication.java
│   │
│   ├── entity/
│   │   ├── User.java
│   │   └── Role.java (enum)
│   │
│   ├── dto/
│   │   ├── LoginRequestDTO.java
│   │   ├── LoginResponseDTO.java
│   │   ├── RegisterRequestDTO.java
│   │   └── UserResponseDTO.java
│   │
│   ├── repository/
│   │   └── UserRepository.java
│   │
│   ├── service/
│   │   ├── UserService.java
│   │   ├── UserServiceImpl.java
│   │   └── CustomUserDetailsService.java
│   │
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── SecurityConfig.java
│   │   └── JwtAuthenticationEntryPoint.java
│   │
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── CustomerRestController.java (updated)
│   │
│   └── exception/
│       └── GlobalExceptionHandler.java (updated)
│
└── src/main/resources/
    └── application.properties
```

---

### 4.3 Database Setup

**Create tables:**

```sql
USE customer_management;

-- Users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Sample users (password: "password123" hashed with BCrypt)
INSERT INTO users (username, email, password, full_name, role, is_active) VALUES
('admin', 'admin@example.com', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'Admin User', 'ADMIN', true),
('john', 'john@example.com', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'John Doe', 'USER', true),
('jane', 'jane@example.com', '$2a$10$XptfskLsT1l/bRTLRiiCgejHqOpgXFreUnNUa35gJdCr2v2QbVFzu', 'Jane Smith', 'USER', true);
```

**Note:** Password is "password123" for all users (for testing only!)

---

### 4.4 Configuration

**Update `application.properties`:**

```properties
# Application
spring.application.name=secure-customer-api
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/customer_management?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough256Bits
jwt.expiration=86400000

# Security
spring.security.user.name=admin
spring.security.user.password=admin

# Logging
logging.level.com.example.securecustomerapi=DEBUG
logging.level.org.springframework.security=DEBUG
```

**Important:** Change `jwt.secret` to a strong, unique secret in production!

---

## 5. SAMPLE CODE - SECURITY IMPLEMENTATION

### 5.1 ENTITY Layer

**File:** `src/main/java/com/example/securecustomerapi/entity/Role.java`

```java
package com.example.securecustomerapi.entity;

public enum Role {
    USER,
    ADMIN
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/entity/User.java`

```java
package com.example.securecustomerapi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;  // Hashed password
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {
    }
    
    public User(String username, String email, String password, String fullName, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
```

---

### 5.2 DTO Layer

**File:** `src/main/java/com/example/securecustomerapi/dto/LoginRequestDTO.java`

```java
package com.example.securecustomerapi.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    public LoginRequestDTO() {
    }
    
    public LoginRequestDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/dto/LoginResponseDTO.java`

```java
package com.example.securecustomerapi.dto;

public class LoginResponseDTO {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    
    public LoginResponseDTO() {
    }
    
    public LoginResponseDTO(String token, String username, String email, String role) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/dto/RegisterRequestDTO.java`

```java
package com.example.securecustomerapi.dto;

import jakarta.validation.constraints.*;

public class RegisterRequestDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be 2-100 characters")
    private String fullName;
    
    // Constructors
    public RegisterRequestDTO() {
    }
    
    public RegisterRequestDTO(String username, String email, String password, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/dto/UserResponseDTO.java`

```java
package com.example.securecustomerapi.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {
    
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    // Constructors
    public UserResponseDTO() {
    }
    
    public UserResponseDTO(Long id, String username, String email, String fullName, 
                          String role, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
```

---

### 5.3 REPOSITORY Layer

**File:** `src/main/java/com/example/securecustomerapi/repository/UserRepository.java`

```java
package com.example.securecustomerapi.repository;

import com.example.securecustomerapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
}
```

---

### 5.4 SECURITY Layer

**File:** `src/main/java/com/example/securecustomerapi/security/JwtTokenProvider.java`

```java
package com.example.securecustomerapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    // Generate JWT token
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    // Get username from token
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        
        return claims.getSubject();
    }
    
    // Validate token
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            
            return true;
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims string is empty");
        }
        return false;
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/security/JwtAuthenticationFilter.java`

```java
package com.example.securecustomerapi.security;

import com.example.securecustomerapi.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/security/JwtAuthenticationEntryPoint.java`

```java
package com.example.securecustomerapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", LocalDateTime.now().toString());
        data.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        data.put("error", "Unauthorized");
        data.put("message", "Authentication required. Please provide valid JWT token.");
        data.put("path", request.getRequestURI());
        
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/security/SecurityConfig.java`

```java
package com.example.securecustomerapi.security;

import com.example.securecustomerapi.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(authenticationEntryPoint)
            )
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/customers/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")
                // All other requests need authentication
                .anyRequest().authenticated()
            );
        
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

### 5.5 SERVICE Layer

**File:** `src/main/java/com/example/securecustomerapi/service/CustomUserDetailsService.java`

```java
package com.example.securecustomerapi.service;

import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                true,
                true,
                true,
                getAuthorities(user)
        );
    }
    
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/service/UserService.java`

```java
package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.LoginRequestDTO;
import com.example.securecustomerapi.dto.LoginResponseDTO;
import com.example.securecustomerapi.dto.RegisterRequestDTO;
import com.example.securecustomerapi.dto.UserResponseDTO;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);
}
```

---

**File:** `src/main/java/com/example/securecustomerapi/service/UserServiceImpl.java`

```java
package com.example.securecustomerapi.service;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.entity.Role;
import com.example.securecustomerapi.entity.User;
import com.example.securecustomerapi.exception.DuplicateResourceException;
import com.example.securecustomerapi.exception.ResourceNotFoundException;
import com.example.securecustomerapi.repository.UserRepository;
import com.example.securecustomerapi.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String token = tokenProvider.generateToken(authentication);
        
        // Get user details
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return new LoginResponseDTO(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
        );
    }
    
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToDTO(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }
}
```

---

### 5.6 CONTROLLER Layer

**File:** `src/main/java/com/example/securecustomerapi/controller/AuthController.java`

```java
package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.*;
import com.example.securecustomerapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // In JWT, logout is handled client-side by removing token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully. Please remove token from client.");
        return ResponseEntity.ok(response);
    }
}
```

---

**Update Customer Controller for Security:**

**File:** `src/main/java/com/example/securecustomerapi/controller/CustomerRestController.java`

```java
package com.example.securecustomerapi.controller;

import com.example.securecustomerapi.dto.CustomerRequestDTO;
import com.example.securecustomerapi.dto.CustomerResponseDTO;
import com.example.securecustomerapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerRestController {
    
    @Autowired
    private CustomerService customerService;
    
    // GET - All users can view
    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {
        List<CustomerResponseDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }
    
    // GET by ID - All users can view
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        CustomerResponseDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }
    
    // POST - Only ADMIN can create
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO created = customerService.createCustomer(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT - Only ADMIN can update
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO requestDTO) {
        CustomerResponseDTO updated = customerService.updateCustomer(id, requestDTO);
        return ResponseEntity.ok(updated);
    }
    
    // DELETE - Only ADMIN can delete
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    // SEARCH - All authenticated users
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponseDTO>> searchCustomers(@RequestParam String keyword) {
        List<CustomerResponseDTO> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(customers);
    }
}
```

---

## 6. JWT TOKEN FLOW

### Complete Authentication Flow

```
1. REGISTRATION
   Client → POST /api/auth/register {username, email, password}
   Server → Hash password with BCrypt
   Server → Save user to database
   Server → Return: 201 Created

2. LOGIN
   Client → POST /api/auth/login {username, password}
   Server → Verify username exists
   Server → Compare password with hashed password
   Server → Generate JWT token with username, role, expiry
   Server → Return: {token: "eyJhbG...", username, email, role}
   Client → Store token in localStorage

3. ACCESSING PROTECTED ENDPOINT
   Client → GET /api/customers
           Header: Authorization: Bearer eyJhbG...
   Server → JwtAuthenticationFilter intercepts request
   Server → Extract token from Authorization header
   Server → Validate token (signature, expiry)
   Server → Extract username from token
   Server → Load user details from database
   Server → Set authentication in SecurityContext
   Server → Process request
   Server → Return response

4. AUTHORIZATION CHECK
   Server → Check if user has required role (@PreAuthorize)
   If authorized → Process request
   If not authorized → Return 403 Forbidden

5. LOGOUT
   Client → POST /api/auth/logout
   Client → Remove token from localStorage
   Server → Return: {message: "Logged out"}
```

---

### Token Structure Example

**Request:**
```
POST /api/auth/login
{
    "username": "admin",
    "password": "password123"
}
```

**Response:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTczMDY0MDAwMCwiZXhwIjoxNzMwNzI2NDAwfQ.rD8_X2kZvxK5M7nN4pQqR3sT6uV7wX8yZ9aAbBcCdDeEfFgG",
    "type": "Bearer",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

**Decoded JWT Payload:**
```json
{
    "sub": "admin",
    "iat": 1730640000,
    "exp": 1730726400
}
```

---

## 7. TESTING AUTHENTICATION

### 7.1 Test Registration

```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "fullName": "Test User"
}

Expected: 201 Created
{
    "id": 4,
    "username": "testuser",
    "email": "test@example.com",
    "fullName": "Test User",
    "role": "USER",
    "isActive": true,
    "createdAt": "2024-11-03T10:00:00"
}
```

---

### 7.2 Test Login

```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "password123"
}

Expected: 200 OK
{
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "username": "admin",
    "email": "admin@example.com",
    "role": "ADMIN"
}
```

---

### 7.3 Test Protected Endpoint (Without Token)

```
GET http://localhost:8080/api/customers

Expected: 401 Unauthorized
{
    "timestamp": "2024-11-03T10:05:00",
    "status": 401,
    "error": "Unauthorized",
    "message": "Authentication required. Please provide valid JWT token.",
    "path": "/api/customers"
}
```

---

### 7.4 Test Protected Endpoint (With Token)

```
GET http://localhost:8080/api/customers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Expected: 200 OK
[
    {
        "id": 1,
        "customerCode": "C001",
        "fullName": "John Doe",
        ...
    }
]
```

---

### 7.5 Test Authorization (USER trying to DELETE)

```
DELETE http://localhost:8080/api/customers/1
Authorization: Bearer <USER_TOKEN>

Expected: 403 Forbidden
{
    "timestamp": "2024-11-03T10:10:00",
    "status": 403,
    "error": "Forbidden",
    "message": "Access denied. Insufficient permissions.",
    "path": "/api/customers/1"
}
```

---

### 7.6 Test Authorization (ADMIN can DELETE)

```
DELETE http://localhost:8080/api/customers/1
Authorization: Bearer <ADMIN_TOKEN>

Expected: 200 OK
{
    "message": "Customer deleted successfully"
}
```

---

## 8. COMPARISON: SESSION VS JWT

### Session-Based Authentication (Traditional)

**How it works:**
```
1. User logs in
2. Server creates session, stores user data
3. Server sends session ID as cookie
4. Client sends cookie with each request
5. Server looks up session to identify user
```

**Pros:**
- Server controls sessions (easy revocation)
- Smaller cookie size
- Familiar pattern

**Cons:**
- Server must store session data
- Doesn't scale well (sticky sessions needed)
- Not suitable for mobile apps
- Problematic for microservices

---

### JWT Authentication (Modern)

**How it works:**
```
1. User logs in
2. Server generates JWT token
3. Client stores token (localStorage)
4. Client sends token in Authorization header
5. Server validates token signature
```

**Pros:**
- Stateless (no server storage)
- Perfect for microservices
- Great for mobile apps
- Scales horizontally
- Cross-domain support

**Cons:**
- Token size larger than session ID
- Difficult to revoke (until expiry)
- Token theft risk (store securely)
- Need to handle token refresh

---

## 9. BEST PRACTICES

### Security

✅ **Use strong JWT secret:**
```properties
# Minimum 256 bits
jwt.secret=myVeryLongAndSecureSecretKeyThatIsAtLeast256BitsLong123456789
```

✅ **Set appropriate token expiration:**
```properties
jwt.expiration=86400000  # 24 hours
# Short expiration = more secure, but more frequent logins
```

✅ **Hash passwords with BCrypt:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

✅ **Validate all inputs:**
```java
@PostMapping("/login")
public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
    // @Valid triggers validation
}
```

✅ **Use HTTPS in production** - Never send JWT over HTTP

---

### Token Management

✅ **Store token securely:**
```javascript
// Client-side (React/Angular/Vue)
localStorage.setItem('token', response.token);

// Include in requests
headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`
}
```

✅ **Handle token expiration:**
```java
// Implement token refresh mechanism
// Or force re-login when token expires
```

✅ **Logout properly:**
```javascript
// Remove token on logout
localStorage.removeItem('token');
```

---

### Authorization

✅ **Use method-level security:**
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
    // Only ADMIN can execute
}
```

✅ **Check roles consistently:**
```java
// In SecurityConfig
.requestMatchers(HttpMethod.DELETE, "/api/customers/**").hasRole("ADMIN")

// In Controller
@PreAuthorize("hasRole('ADMIN')")
```

---

## 10. SUMMARY

### What You Learned

✅ **Spring Security Basics** - Authentication and authorization  
✅ **JWT Implementation** - Token generation and validation  
✅ **Password Encryption** - BCrypt hashing  
✅ **Security Configuration** - SecurityFilterChain setup  
✅ **Method Security** - @PreAuthorize annotations  
✅ **Custom Filters** - JwtAuthenticationFilter  
✅ **Exception Handling** - 401 Unauthorized, 403 Forbidden

---

### Key Concepts

**Authentication:**
- UserDetailsService implementation
- Password encoding with BCrypt
- JWT token generation
- Token validation

**Authorization:**
- Role-based access control
- Method-level security
- Endpoint protection
- @PreAuthorize checks

**JWT:**
- Stateless authentication
- Token structure (header.payload.signature)
- Token expiration
- Client-side storage

---

### Next Steps

**After Lab 9, you can:**
1. Add token refresh mechanism
2. Implement password reset
3. Add email verification
4. Create user profile management
5. Implement activity logging
6. Add OAuth2 integration (Google, Facebook)

---

**End of Setup Guide**

*Review this before Lab 9. Understand Spring Security and JWT thoroughly!*

**⚠️ IMPORTANT SECURITY NOTES:**
- NEVER commit JWT secret to version control
- NEVER send tokens over HTTP (use HTTPS)
- NEVER store sensitive data in JWT payload
- ALWAYS validate tokens on every request
- ALWAYS use strong passwords
- ALWAYS set token expiration