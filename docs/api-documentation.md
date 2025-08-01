# My Bakery Management System - API Documentation

## Overview

This document provides detailed information about the RESTful API endpoints available in the My Bakery Management System. The API allows developers to integrate with the bakery system programmatically, enabling custom integrations and extensions.

## Base URL

All API endpoints are relative to the base URL:

```
https://your-domain.com/api
```

For local development:

```
http://localhost:8080/api
```

## Authentication

The API uses JWT (JSON Web Token) authentication. To access protected endpoints, you must include the JWT token in the Authorization header of your requests.

### Obtaining a Token

To obtain a token, make a POST request to the `/auth/login` endpoint with your credentials.

**Request:**

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "your-password"
}
```

**Response:**

```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMIN"
  }
}
```

### Using the Token

Include the token in the Authorization header of your requests:

```http
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Refreshing a Token

To refresh an expired token, make a POST request to the `/auth/refresh-token` endpoint with your refresh token.

**Request:**

```http
POST /api/auth/refresh-token
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**

```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Error Handling

The API uses standard HTTP status codes to indicate the success or failure of requests. In case of an error, the response body will contain additional information about the error.

**Example Error Response:**

```json
{
  "success": false,
  "message": "Invalid credentials",
  "status": 401,
  "timestamp": "2025-08-01T14:30:00.000Z"
}
```

## API Endpoints

### Authentication

#### Register a new user

```http
POST /api/auth/register
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

**Response:**

```json
{
  "success": true,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Login

```http
POST /api/auth/login
```

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**

```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMIN"
  }
}
```

#### Get Current User

```http
GET /api/auth/me
```

**Response:**

```json
{
  "success": true,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "ADMIN"
  }
}
```

#### Refresh Token

```http
POST /api/auth/refresh-token
```

**Request Body:**

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**

```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Logout

```http
POST /api/auth/logout
```

**Response:**

```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Products

#### Get All Products

```http
GET /api/products
```

**Query Parameters:**

- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: "id")
- `direction` (optional): Sort direction (default: "asc")
- `category` (optional): Filter by category ID
- `search` (optional): Search term for product name or description

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Chocolate Cake",
        "description": "Delicious chocolate cake",
        "price": 25.99,
        "category": {
          "id": 1,
          "name": "Cakes"
        },
        "imageUrl": "/images/products/chocolate-cake.jpg",
        "active": true,
        "createdAt": "2025-07-15T10:30:00.000Z",
        "updatedAt": "2025-07-15T10:30:00.000Z"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 50,
    "totalPages": 3,
    "last": false,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 20,
    "first": true,
    "empty": false
  }
}
```

#### Get Product by ID

```http
GET /api/products/{id}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Chocolate Cake",
    "description": "Delicious chocolate cake",
    "price": 25.99,
    "category": {
      "id": 1,
      "name": "Cakes"
    },
    "imageUrl": "/images/products/chocolate-cake.jpg",
    "active": true,
    "createdAt": "2025-07-15T10:30:00.000Z",
    "updatedAt": "2025-07-15T10:30:00.000Z"
  }
}
```

#### Create Product

```http
POST /api/products
```

**Request Body:**

```json
{
  "name": "Vanilla Cupcake",
  "description": "Delicious vanilla cupcake with sprinkles",
  "price": 3.99,
  "categoryId": 2,
  "imageUrl": "/images/products/vanilla-cupcake.jpg",
  "active": true
}
```

**Response:**

```json
{
  "success": true,
  "message": "Product created successfully",
  "data": {
    "id": 51,
    "name": "Vanilla Cupcake",
    "description": "Delicious vanilla cupcake with sprinkles",
    "price": 3.99,
    "category": {
      "id": 2,
      "name": "Cupcakes"
    },
    "imageUrl": "/images/products/vanilla-cupcake.jpg",
    "active": true,
    "createdAt": "2025-08-01T14:30:00.000Z",
    "updatedAt": "2025-08-01T14:30:00.000Z"
  }
}
```

#### Update Product

```http
PUT /api/products/{id}
```

**Request Body:**

```json
{
  "name": "Vanilla Cupcake Deluxe",
  "description": "Delicious vanilla cupcake with premium sprinkles",
  "price": 4.99,
  "categoryId": 2,
  "imageUrl": "/images/products/vanilla-cupcake-deluxe.jpg",
  "active": true
}
```

**Response:**

```json
{
  "success": true,
  "message": "Product updated successfully",
  "data": {
    "id": 51,
    "name": "Vanilla Cupcake Deluxe",
    "description": "Delicious vanilla cupcake with premium sprinkles",
    "price": 4.99,
    "category": {
      "id": 2,
      "name": "Cupcakes"
    },
    "imageUrl": "/images/products/vanilla-cupcake-deluxe.jpg",
    "active": true,
    "createdAt": "2025-08-01T14:30:00.000Z",
    "updatedAt": "2025-08-01T14:35:00.000Z"
  }
}
```

#### Delete Product

```http
DELETE /api/products/{id}
```

**Response:**

```json
{
  "success": true,
  "message": "Product deleted successfully"
}
```

### Orders

#### Get All Orders

```http
GET /api/orders
```

**Query Parameters:**

- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: "id")
- `direction` (optional): Sort direction (default: "asc")
- `status` (optional): Filter by order status
- `customerId` (optional): Filter by customer ID
- `startDate` (optional): Filter by start date
- `endDate` (optional): Filter by end date

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "orderNumber": "ORD-2025-0001",
        "customer": {
          "id": 1,
          "firstName": "John",
          "lastName": "Doe",
          "email": "john@example.com"
        },
        "orderDate": "2025-07-30T10:30:00.000Z",
        "status": "COMPLETED",
        "total": 78.97,
        "items": [
          {
            "id": 1,
            "product": {
              "id": 1,
              "name": "Chocolate Cake"
            },
            "quantity": 2,
            "price": 25.99,
            "subtotal": 51.98
          },
          {
            "id": 2,
            "product": {
              "id": 3,
              "name": "Croissant"
            },
            "quantity": 6,
            "price": 4.50,
            "subtotal": 27.00
          }
        ],
        "createdAt": "2025-07-30T10:30:00.000Z",
        "updatedAt": "2025-07-30T11:45:00.000Z"
      },
      // More orders...
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 45,
    "totalPages": 3,
    "last": false,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 20,
    "first": true,
    "empty": false
  }
}
```

#### Get Order by ID

```http
GET /api/orders/{id}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "orderNumber": "ORD-2025-0001",
    "customer": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com"
    },
    "orderDate": "2025-07-30T10:30:00.000Z",
    "status": "COMPLETED",
    "total": 78.97,
    "items": [
      {
        "id": 1,
        "product": {
          "id": 1,
          "name": "Chocolate Cake"
        },
        "quantity": 2,
        "price": 25.99,
        "subtotal": 51.98
      },
      {
        "id": 2,
        "product": {
          "id": 3,
          "name": "Croissant"
        },
        "quantity": 6,
        "price": 4.50,
        "subtotal": 27.00
      }
    ],
    "createdAt": "2025-07-30T10:30:00.000Z",
    "updatedAt": "2025-07-30T11:45:00.000Z"
  }
}
```

#### Create Order

```http
POST /api/orders
```

**Request Body:**

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 6
    }
  ]
}
```

**Response:**

```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 46,
    "orderNumber": "ORD-2025-0046",
    "customer": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com"
    },
    "orderDate": "2025-08-01T14:30:00.000Z",
    "status": "PENDING",
    "total": 78.97,
    "items": [
      {
        "id": 91,
        "product": {
          "id": 1,
          "name": "Chocolate Cake"
        },
        "quantity": 2,
        "price": 25.99,
        "subtotal": 51.98
      },
      {
        "id": 92,
        "product": {
          "id": 3,
          "name": "Croissant"
        },
        "quantity": 6,
        "price": 4.50,
        "subtotal": 27.00
      }
    ],
    "createdAt": "2025-08-01T14:30:00.000Z",
    "updatedAt": "2025-08-01T14:30:00.000Z"
  }
}
```

#### Update Order Status

```http
PUT /api/orders/{id}/status
```

**Request Body:**

```json
{
  "status": "PROCESSING"
}
```

**Response:**

```json
{
  "success": true,
  "message": "Order status updated successfully",
  "data": {
    "id": 46,
    "orderNumber": "ORD-2025-0046",
    "status": "PROCESSING",
    "updatedAt": "2025-08-01T14:35:00.000Z"
  }
}
```

#### Cancel Order

```http
POST /api/orders/{id}/cancel
```

**Response:**

```json
{
  "success": true,
  "message": "Order cancelled successfully",
  "data": {
    "id": 46,
    "orderNumber": "ORD-2025-0046",
    "status": "CANCELLED",
    "updatedAt": "2025-08-01T14:40:00.000Z"
  }
}
```

### Inventory

#### Get All Inventory Items

```http
GET /api/inventory
```

**Query Parameters:**

- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: "id")
- `direction` (optional): Sort direction (default: "asc")
- `status` (optional): Filter by inventory status

**Response:**

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "product": {
          "id": 1,
          "name": "Chocolate Cake"
        },
        "quantity": 15,
        "minStock": 5,
        "status": "IN_STOCK",
        "lastRestocked": "2025-07-28T09:00:00.000Z",
        "expiryDate": "2025-08-05T00:00:00.000Z",
        "createdAt": "2025-07-01T10:00:00.000Z",
        "updatedAt": "2025-07-28T09:00:00.000Z"
      },
      // More inventory items...
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalElements": 40,
    "totalPages": 2,
    "last": false,
    "size": 20,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 20,
    "first": true,
    "empty": false
  }
}
```

#### Get Inventory by Product ID

```http
GET /api/inventory/product/{productId}
```

**Response:**

```json
{
  "success": true,
  "data": {
    "id": 1,
    "product": {
      "id": 1,
      "name": "Chocolate Cake"
    },
    "quantity": 15,
    "minStock": 5,
    "status": "IN_STOCK",
    "lastRestocked": "2025-07-28T09:00:00.000Z",
    "expiryDate": "2025-08-05T00:00:00.000Z",
    "createdAt": "2025-07-01T10:00:00.000Z",
    "updatedAt": "2025-07-28T09:00:00.000Z"
  }
}
```

#### Update Inventory Quantity

```http
PUT /api/inventory/product/{productId}/quantity
```

**Request Body:**

```json
{
  "quantity": 20
}
```

**Response:**

```json
{
  "success": true,
  "message": "Inventory updated successfully",
  "data": {
    "id": 1,
    "product": {
      "id": 1,
      "name": "Chocolate Cake"
    },
    "quantity": 20,
    "status": "IN_STOCK",
    "updatedAt": "2025-08-01T14:45:00.000Z"
  }
}
```

#### Get Low Stock Inventory

```http
GET /api/inventory/low-stock
```

**Response:**

```json
{
  "success": true,
  "data": [
    {
      "id": 3,
      "product": {
        "id": 3,
        "name": "Croissant"
      },
      "quantity": 4,
      "minStock": 10,
      "status": "LOW_STOCK",
      "lastRestocked": "2025-07-25T09:00:00.000Z",
      "expiryDate": "2025-08-02T00:00:00.000Z",
      "createdAt": "2025-07-01T10:00:00.000Z",
      "updatedAt": "2025-07-30T15:30:00.000Z"
    },
    // More low stock items...
  ]
}
```

### Analytics

#### Get Dashboard Summary

```http
GET /api/analytics/dashboard-summary
```

**Query Parameters:**

- `period` (optional): Time period (default: "MONTH", options: "DAY", "WEEK", "MONTH", "YEAR")

**Response:**

```json
{
  "success": true,
  "data": {
    "totalOrders": 45,
    "totalRevenue": 3567.85,
    "lowStockCount": 3,
    "pendingOrdersCount": 5,
    "salesData": [
      {
        "date": "2025-07-01",
        "amount": 120.50
      },
      {
        "date": "2025-07-02",
        "amount": 95.75
      },
      // More daily sales data...
    ],
    "topProducts": [
      {
        "id": 1,
        "name": "Chocolate Cake",
        "quantity": 42
      },
      {
        "id": 3,
        "name": "Croissant",
        "quantity": 36
      },
      // More top products...
    ]
  }
}
```

#### Get Sales Report

```http
GET /api/analytics/sales-report
```

**Query Parameters:**

- `startDate` (required): Start date (format: "YYYY-MM-DD")
- `endDate` (required): End date (format: "YYYY-MM-DD")
- `groupBy` (optional): Group by (default: "DAY", options: "DAY", "WEEK", "MONTH")

**Response:**

```json
{
  "success": true,
  "data": {
    "totalSales": 3567.85,
    "totalOrders": 45,
    "averageOrderValue": 79.29,
    "salesByPeriod": [
      {
        "period": "2025-07-01",
        "sales": 120.50,
        "orders": 2
      },
      {
        "period": "2025-07-02",
        "sales": 95.75,
        "orders": 1
      },
      // More periods...
    ],
    "salesByCategory": [
      {
        "category": "Cakes",
        "sales": 1250.75,
        "percentage": 35.1
      },
      {
        "category": "Bread",
        "sales": 875.50,
        "percentage": 24.5
      },
      // More categories...
    ],
    "salesByProduct": [
      {
        "product": "Chocolate Cake",
        "sales": 1091.58,
        "quantity": 42,
        "percentage": 30.6
      },
      {
        "product": "Croissant",
        "sales": 162.00,
        "quantity": 36,
        "percentage": 4.5
      },
      // More products...
    ]
  }
}
```

## Rate Limiting

The API implements rate limiting to prevent abuse. The current limits are:

- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated users

If you exceed these limits, you will receive a 429 Too Many Requests response.

## Versioning

The current API version is v1. The version is included in the URL path:

```
https://your-domain.com/api/v1/products
```

## Support

If you have any questions or need assistance with the API, please contact our support team:

- Email: api-support@mybakery.com
- Hours: Monday to Friday, 9:00 AM to 5:00 PM

## Changelog

### v1.0.0 (2025-08-01)

- Initial release of the API