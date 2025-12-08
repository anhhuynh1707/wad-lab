# Customer API Documentation
## Base URL: 
`http://localhost:8080/api/customers`
# 📂 Endpoints Overview

| Method | Endpoint | Description |
|--------|-----------|-------------|
| GET | `/` | Get all customers |
| GET | `/{id}` | Get customer by ID |
| POST | `/` | Create customer |
| PUT | `/{id}` | Update customer |
| PATCH | `/{id}` | Partial update |
| DELETE | `/{id}` | Delete customer |
| GET | `/search?keyword=` | Search customers |
| GET | `/status/{status}` | Filter customers by status |
| GET | `?page=&size=` | Pagination |
| GET | `?sortBy=&sortDir=` | Sorting |
| GET | `?page=&size=&sortBy=&sortDir=` | Pagination + Sorting |

---

# 🟦 1. Get All Customers
### GET `/api/customers`

### ✔ 200 OK
```json
{
  "totalItems": 7,
  "totalPages": 1,
  "sortBy": "id",
  "customers": [
    {
      "id": 1,
      "customerCode": "C001",
      "fullName": "John Doe",
      "email": "john.doe@gmail.com",
      "phone": "+1-555-0101",
      "address": "123 Main St, New York, NY 10001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 2,
      "customerCode": "C002",
      "fullName": "Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "+1-555-0102",
      "address": "456 Oak Ave, Los Angeles, CA 90001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 3,
      "customerCode": "C003",
      "fullName": "Bob Johnson",
      "email": "bob.johnson@example.com",
      "phone": "+1-555-0103",
      "address": "789 Pine Rd, Chicago, IL 60601",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 4,
      "customerCode": "C004",
      "fullName": "Alice Brown",
      "email": "alice.brown@example.com",
      "phone": "+1-555-0104",
      "address": "321 Elm St, Houston, TX 77001",
      "status": "INACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 5,
      "customerCode": "C005",
      "fullName": "Charlie Wilson",
      "email": "charlie.wilson@example.com",
      "phone": "+1-555-0105",
      "address": "654 Maple Dr, Phoenix, AZ 85001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 7,
      "customerCode": "C006",
      "fullName": "David Miller",
      "email": "david.miller@example.com",
      "phone": "+9847592378",
      "address": "999 Broadway, Seattle, WA 98101",
      "status": "ACTIVE",
      "createdAt": "2025-12-07T16:38:49"
    },
    {
      "id": 8,
      "customerCode": "C010",
      "fullName": "David Nguyen",
      "email": "david@example.com",
      "phone": "0909009009",
      "address": "HCMC",
      "status": "ACTIVE",
      "createdAt": "2025-12-08T16:38:53"
    }
  ],
  "currentPage": 0,
  "sortDir": "asc"
}
```

# 🟦 2. Get Customer by ID
**GET** `/api/customers/{id}`

### ✔ 200 OK
```json
{
  "id": 1,
  "customerCode": "C001",
  "fullName": "John Doe",
  "email": "john.doe@gmail.com",
  "phone": "+1-555-0101",
  "address": "123 Main St, New York, NY 10001",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:44:03"
}
```
### Error Responses
**Status: 404 Not Found**
```json
{
  "timestamp": "2025-12-08T17:02:38.5819336",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/customers/999",
  "details": null
}
```
# 🟩 3. Create Customer
**POST** `/api/customers`
### Request Body
```json
{
  "customerCode": "C010",
  "fullName": "David Nguyen",
  "email": "david@example.com",
  "phone": "0909009009",
  "address": "HCMC"
}
```
### ✔ 201 Created
```json
{
  "id": 8,
  "customerCode": "C010",
  "fullName": "David Nguyen",
  "email": "david@example.com",
  "phone": "0909009009",
  "address": "HCMC",
  "status": "ACTIVE",
  "createdAt": "2025-12-08T16:38:53"
}
```
# 🟧 4. Update Customer (PUT)
**PUT** `/api/customers/{id}`
### Request Body
```json
{
  "customerCode": "C006",
  "fullName": "David Miller Jr.",
  "email": "david.miller.jr@example.com",
  "phone": "+9847592378",
  "address": "1000 Broadway, Seattle, WA 98101"
}
```
### ✔ 200 OK
```json
{
  "id": 6,
  "customerCode": "C006",
  "fullName": "David Miller Jr.",
  "email": "david.miller.jr@example.com",
  "phone": "+9847592378",
  "address": "1000 Broadway, Seattle, WA 98101",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T09:20:31"
}
```
# 🟦 5. Partial Update (PATCH)
**PATCH** `/api/customers/{id}`
### Request Body
```json
{
  "email": "john@example.com"
}
```
### ✔ 200 OK
```json
{
  "id": 1,
  "customerCode": "C001",
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0101",
  "address": "123 Main St, New York, NY 10001",
  "status": "ACTIVE",
  "createdAt": "2025-12-02T15:44:03"
}
```
# 🟥 6. Delete Customer
**DELETE** `/api/customers/{id}`
### ✔ 200 OK
```json
{
  "message": "Customer deleted successfully"
}
```
# 🟦 7. Search Customers
**GET** `/api/customers/search?keyword=john`
### ✔ 200 OK
```json
[
  {
    "id": 1,
    "customerCode": "C001",
    "fullName": "John Doe",
    "email": "john.doe@gmail.com",
    "phone": "+1-555-0101",
    "address": "123 Main St, New York, NY 10001",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  },
  {
    "id": 3,
    "customerCode": "C003",
    "fullName": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1-555-0103",
    "address": "789 Pine Rd, Chicago, IL 60601",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  }
]
```
# 🟧 8. Filter by Status
**GET** `/api/customers/status/ACTIVE`
### ✔ 200 OK
```json
[
  {
    "id": 1,
    "customerCode": "C001",
    "fullName": "John Doe",
    "email": "john.doe@gmail.com",
    "phone": "+1-555-0101",
    "address": "123 Main St, New York, NY 10001",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  },
  {
    "id": 2,
    "customerCode": "C002",
    "fullName": "Jane Smith",
    "email": "jane.smith@example.com",
    "phone": "+1-555-0102",
    "address": "456 Oak Ave, Los Angeles, CA 90001",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  },
  {
    "id": 3,
    "customerCode": "C003",
    "fullName": "Bob Johnson",
    "email": "bob.johnson@example.com",
    "phone": "+1-555-0103",
    "address": "789 Pine Rd, Chicago, IL 60601",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  },
  {
    "id": 5,
    "customerCode": "C005",
    "fullName": "Charlie Wilson",
    "email": "charlie.wilson@example.com",
    "phone": "+1-555-0105",
    "address": "654 Maple Dr, Phoenix, AZ 85001",
    "status": "ACTIVE",
    "createdAt": "2025-12-02T15:44:03"
  },
  {
    "id": 7,
    "customerCode": "C006",
    "fullName": "David Miller",
    "email": "david.miller@example.com",
    "phone": "+9847592378",
    "address": "999 Broadway, Seattle, WA 98101",
    "status": "ACTIVE",
    "createdAt": "2025-12-07T16:38:49"
  },
  {
    "id": 8,
    "customerCode": "C010",
    "fullName": "David Nguyen",
    "email": "david@example.com",
    "phone": "0909009009",
    "address": "HCMC",
    "status": "ACTIVE",
    "createdAt": "2025-12-08T16:38:53"
  }
]
```
# 🟪 9. Pagination Example
**GET** `/api/customers?page=0&size=5`
### ✔ 200 OK
```json
{
  "totalItems": 7,
  "totalPages": 2,
  "sortBy": "id",
  "customers": [
    {
      "id": 1,
      "customerCode": "C001",
      "fullName": "John Doe",
      "email": "john.doe@gmail.com",
      "phone": "+1-555-0101",
      "address": "123 Main St, New York, NY 10001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 2,
      "customerCode": "C002",
      "fullName": "Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "+1-555-0102",
      "address": "456 Oak Ave, Los Angeles, CA 90001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 3,
      "customerCode": "C003",
      "fullName": "Bob Johnson",
      "email": "bob.johnson@example.com",
      "phone": "+1-555-0103",
      "address": "789 Pine Rd, Chicago, IL 60601",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 4,
      "customerCode": "C004",
      "fullName": "Alice Brown",
      "email": "alice.brown@example.com",
      "phone": "+1-555-0104",
      "address": "321 Elm St, Houston, TX 77001",
      "status": "INACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 5,
      "customerCode": "C005",
      "fullName": "Charlie Wilson",
      "email": "charlie.wilson@example.com",
      "phone": "+1-555-0105",
      "address": "654 Maple Dr, Phoenix, AZ 85001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    }
  ],
  "currentPage": 0,
  "sortDir": "asc"
}
```
# 🟧 10. Sorting Example
**GET** `/api/customers?sortBy=fullName&sortDir=asc`
### ✔ 200 OK
```json
{
  "totalItems": 7,
  "totalPages": 1,
  "sortBy": "fullName",
  "customers": [
    {
      "id": 4,
      "customerCode": "C004",
      "fullName": "Alice Brown",
      "email": "alice.brown@example.com",
      "phone": "+1-555-0104",
      "address": "321 Elm St, Houston, TX 77001",
      "status": "INACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 3,
      "customerCode": "C003",
      "fullName": "Bob Johnson",
      "email": "bob.johnson@example.com",
      "phone": "+1-555-0103",
      "address": "789 Pine Rd, Chicago, IL 60601",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 5,
      "customerCode": "C005",
      "fullName": "Charlie Wilson",
      "email": "charlie.wilson@example.com",
      "phone": "+1-555-0105",
      "address": "654 Maple Dr, Phoenix, AZ 85001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 7,
      "customerCode": "C006",
      "fullName": "David Miller",
      "email": "david.miller@example.com",
      "phone": "+9847592378",
      "address": "999 Broadway, Seattle, WA 98101",
      "status": "ACTIVE",
      "createdAt": "2025-12-07T16:38:49"
    },
    {
      "id": 8,
      "customerCode": "C010",
      "fullName": "David Nguyen",
      "email": "david@example.com",
      "phone": "0909009009",
      "address": "HCMC",
      "status": "ACTIVE",
      "createdAt": "2025-12-08T16:38:53"
    },
    {
      "id": 2,
      "customerCode": "C002",
      "fullName": "Jane Smith",
      "email": "jane.smith@example.com",
      "phone": "+1-555-0102",
      "address": "456 Oak Ave, Los Angeles, CA 90001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 1,
      "customerCode": "C001",
      "fullName": "John Doe",
      "email": "john.doe@gmail.com",
      "phone": "+1-555-0101",
      "address": "123 Main St, New York, NY 10001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    }
  ],
  "currentPage": 0,
  "sortDir": "asc"
}
```
# 🟦 11. Pagination + Sorting
**GET** `/api/customers?page=0&size=5&sortBy=fullName&sortDir=asc`
### ✔ 200 OK
```json
{
  "totalItems": 7,
  "totalPages": 2,
  "sortBy": "fullName",
  "customers": [
    {
      "id": 4,
      "customerCode": "C004",
      "fullName": "Alice Brown",
      "email": "alice.brown@example.com",
      "phone": "+1-555-0104",
      "address": "321 Elm St, Houston, TX 77001",
      "status": "INACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 3,
      "customerCode": "C003",
      "fullName": "Bob Johnson",
      "email": "bob.johnson@example.com",
      "phone": "+1-555-0103",
      "address": "789 Pine Rd, Chicago, IL 60601",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 5,
      "customerCode": "C005",
      "fullName": "Charlie Wilson",
      "email": "charlie.wilson@example.com",
      "phone": "+1-555-0105",
      "address": "654 Maple Dr, Phoenix, AZ 85001",
      "status": "ACTIVE",
      "createdAt": "2025-12-02T15:44:03"
    },
    {
      "id": 7,
      "customerCode": "C006",
      "fullName": "David Miller",
      "email": "david.miller@example.com",
      "phone": "+9847592378",
      "address": "999 Broadway, Seattle, WA 98101",
      "status": "ACTIVE",
      "createdAt": "2025-12-07T16:38:49"
    },
    {
      "id": 8,
      "customerCode": "C010",
      "fullName": "David Nguyen",
      "email": "david@example.com",
      "phone": "0909009009",
      "address": "HCMC",
      "status": "ACTIVE",
      "createdAt": "2025-12-08T16:38:53"
    }
  ],
  "currentPage": 0,
  "sortDir": "asc"
}
```
# ⚠️ Error Responses
### ❌ 400 Bad Request
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": [
    "email: must be a valid email"
  ]
}
```
### ❌ 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 99"
}
```
### ❌ 500 Internal Server Error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unexpected error occurred"
}
```