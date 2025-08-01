# Bakery Business Backend API Specification

## Project Overview

A comprehensive Spring Boot REST API for managing a bakery business, including product management, order processing, customer management, inventory tracking, and business analytics.

## Technology Stack

### Backend:
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL/MySQL
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI 3.0 (Swagger)
- **Testing**: JUnit 5, Mockito
- **Build Tool**: Maven/Gradle
- **Cache**: Redis (optional)
- **File Storage**: AWS S3/Local Storage

### Frontend:
- **Framework**: Flutter
- **Platform**: iOS & Android
- **State Management**: Provider/Riverpod/Bloc
- **HTTP Client**: Dio/http
- **Local Storage**: Hive/SharedPreferences
- **Push Notifications**: Firebase Cloud Messaging
- **Image Handling**: cached_network_image

## Core Features

### 1. Authentication & Authorization

#### Features:
- User registration and login
- JWT-based authentication with refresh tokens
- Role-based access control (ADMIN, MANAGER, STAFF, CUSTOMER)
- Password reset functionality
- Account verification via email/SMS
- Session management
- **Flutter-specific**: Biometric authentication support
- **Flutter-specific**: Auto-login with stored tokens
- **Flutter-specific**: Social login (Google, Apple, Facebook)

#### Endpoints:
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
POST /api/auth/refresh-token
POST /api/auth/forgot-password
POST /api/auth/reset-password
POST /api/auth/verify-email
POST /api/auth/verify-sms
POST /api/auth/social-login
GET /api/auth/me
```

### 2. User Management

#### Features:
- User profile management
- Role assignment
- Staff management
- Customer information tracking
- Address management
- Preference settings

#### Endpoints:
```
GET /api/users/profile
PUT /api/users/profile
GET /api/users/{id}
PUT /api/users/{id}
DELETE /api/users/{id}
GET /api/users
POST /api/users/addresses
GET /api/users/addresses
PUT /api/users/addresses/{id}
DELETE /api/users/addresses/{id}
```

### 3. Product Management

#### Features:
- Product catalog management
- Category management
- Product variants (size, flavor, etc.)
- Product images
- Nutritional information
- Allergen information
- Seasonal products
- Product availability status

#### Endpoints:
```
GET /api/products
GET /api/products/{id}
POST /api/products
PUT /api/products/{id}
DELETE /api/products/{id}
GET /api/products/categories
POST /api/products/categories
PUT /api/products/categories/{id}
DELETE /api/products/categories/{id}
POST /api/products/{id}/images
DELETE /api/products/{id}/images/{imageId}
GET /api/products/search
GET /api/products/featured
```

#### Product Entity Fields:
- ID, name, description
- Category, subcategory
- Price, cost price
- SKU, barcode
- Images
- Nutritional info
- Allergens
- Ingredients
- Shelf life
- Storage requirements
- Active status

### 4. Inventory Management

#### Features:
- Stock level tracking
- Automatic reorder points
- Supplier management
- Purchase orders
- Stock adjustments
- Expiry date tracking
- Waste management
- Inventory reports

#### Endpoints:
```
GET /api/inventory
GET /api/inventory/{productId}
PUT /api/inventory/{productId}/adjust
GET /api/inventory/low-stock
GET /api/inventory/expiring
POST /api/inventory/purchase-orders
GET /api/inventory/purchase-orders
PUT /api/inventory/purchase-orders/{id}
GET /api/suppliers
POST /api/suppliers
PUT /api/suppliers/{id}
```

### 5. Order Management

#### Features:
- Order creation and processing
- Order status tracking
- Order history
- Custom orders
- Bulk orders
- Order modifications
- Refunds and cancellations
- Order notifications

#### Endpoints:
```
POST /api/orders
GET /api/orders
GET /api/orders/{id}
PUT /api/orders/{id}/status
DELETE /api/orders/{id}
GET /api/orders/customer/{customerId}
POST /api/orders/{id}/refund
PUT /api/orders/{id}/cancel
GET /api/orders/pending
GET /api/orders/ready
```

#### Order Status Flow:
- PENDING → CONFIRMED → PREPARING → READY → COMPLETED
- CANCELLED, REFUNDED (terminal states)

### 6. Shopping Cart

#### Features:
- Add/remove items
- Update quantities
- Cart persistence
- Guest cart support
- Cart merging on login
- Cart expiration

#### Endpoints:
```
GET /api/cart
POST /api/cart/items
PUT /api/cart/items/{itemId}
DELETE /api/cart/items/{itemId}
DELETE /api/cart/clear
POST /api/cart/merge
```

### 7. Payment Processing

#### Features:
- Multiple payment methods
- Payment gateway integration
- Payment status tracking
- Refund processing
- Payment history
- Invoice generation

#### Endpoints:
```
POST /api/payments/process
GET /api/payments/{id}
POST /api/payments/{id}/refund
GET /api/payments/orders/{orderId}
GET /api/invoices/{orderId}
```

### 8. Customer Management

#### Features:
- Customer profiles
- Purchase history
- Loyalty points
- Customer preferences
- Communication preferences
- Customer segmentation

#### Endpoints:
```
GET /api/customers
GET /api/customers/{id}
PUT /api/customers/{id}
GET /api/customers/{id}/orders
GET /api/customers/{id}/loyalty-points
POST /api/customers/{id}/loyalty-points
GET /api/customers/segments
```

### 9. Loyalty Program

#### Features:
- Points accumulation
- Reward redemption
- Tier-based benefits
- Special offers
- Birthday rewards
- Referral bonuses

#### Endpoints:
```
GET /api/loyalty/points/{customerId}
POST /api/loyalty/earn
POST /api/loyalty/redeem
GET /api/loyalty/rewards
GET /api/loyalty/tiers
GET /api/loyalty/history/{customerId}
```

### 10. Promotions & Discounts

#### Features:
- Coupon management
- Discount codes
- Seasonal promotions
- Buy-one-get-one offers
- Percentage and fixed discounts
- Minimum order requirements

#### Endpoints:
```
GET /api/promotions
POST /api/promotions
PUT /api/promotions/{id}
DELETE /api/promotions/{id}
POST /api/promotions/validate
GET /api/promotions/active
```

### 11. Scheduling & Production

#### Features:
- Production planning
- Staff scheduling
- Equipment scheduling
- Batch tracking
- Recipe management
- Production reports

#### Endpoints:
```
GET /api/production/schedule
POST /api/production/batches
GET /api/production/batches/{id}
PUT /api/production/batches/{id}/status
GET /api/recipes
POST /api/recipes
PUT /api/recipes/{id}
GET /api/staff/schedule
POST /api/staff/schedule
```

### 12. Reporting & Analytics

#### Features:
- Sales reports
- Inventory reports
- Customer analytics
- Product performance
- Financial reports
- Custom date ranges
- Export capabilities

#### Endpoints:
```
GET /api/reports/sales
GET /api/reports/inventory
GET /api/reports/customers
GET /api/reports/products
GET /api/reports/financial
GET /api/analytics/dashboard
GET /api/analytics/trends
```

### 13. Mobile-Specific Features

#### Push Notifications:
- Order status updates
- Promotional offers
- Loyalty point updates
- New product announcements
- Inventory alerts for staff
- Custom order reminders

#### Features:
- FCM token management
- Notification preferences
- Rich notifications with images
- Deep linking support
- Silent notifications for data sync
- Notification history

#### Endpoints:
```
POST /api/mobile/fcm-token
DELETE /api/mobile/fcm-token
POST /api/mobile/notifications/send
GET /api/mobile/notifications/history
PUT /api/mobile/notifications/preferences
GET /api/mobile/notifications/preferences
POST /api/mobile/notifications/test
```

### 14. Offline Support & Synchronization

#### Features:
- Offline cart management
- Data synchronization
- Conflict resolution
- Queue management for failed requests
- Incremental sync
- Last sync timestamp

#### Endpoints:
```
GET /api/sync/products?lastSync={timestamp}
GET /api/sync/orders?lastSync={timestamp}
GET /api/sync/customer-data?lastSync={timestamp}
POST /api/sync/offline-actions
GET /api/sync/status
```

### 15. Image & Media Management

#### Features:
- Multiple image sizes for different screen densities
- Progressive image loading
- Image caching headers
- WebP format support
- Thumbnail generation
- Image compression

#### Endpoints:
```
POST /api/media/upload
GET /api/media/{id}
GET /api/media/{id}/thumbnail
GET /api/media/{id}/sizes
DELETE /api/media/{id}
GET /api/media/product/{productId}
```

#### Image Response Format:
```json
{
  "id": "img_123",
  "originalUrl": "https://api.bakery.com/media/img_123/original",
  "sizes": {
    "thumbnail": "https://api.bakery.com/media/img_123/thumb",
    "small": "https://api.bakery.com/media/img_123/small",
    "medium": "https://api.bakery.com/media/img_123/medium",
    "large": "https://api.bakery.com/media/img_123/large"
  },
  "webp": {
    "thumbnail": "https://api.bakery.com/media/img_123/thumb.webp",
    "small": "https://api.bakery.com/media/img_123/small.webp"
  }
}
```

### 16. Location & Delivery Services

#### Features:
- Store locator
- GPS-based delivery zones
- Delivery time estimation
- Real-time delivery tracking
- Geofencing for pickup notifications
- Distance-based delivery charges

#### Endpoints:
```
GET /api/locations/stores
GET /api/locations/delivery-zones
POST /api/locations/validate-address
GET /api/locations/delivery-estimate
GET /api/orders/{id}/track
POST /api/locations/geofence-event
```

### 17. Flutter-Optimized API Responses

#### Paginated List Response:
```json
{
  "success": true,
  "data": {
    "items": [
      {
        "id": "prod_123",
        "name": "Chocolate Cake",
        "price": 25.99,
        "imageUrl": "https://api.bakery.com/media/prod_123/medium",
        "thumbnailUrl": "https://api.bakery.com/media/prod_123/thumb",
        "isAvailable": true,
        "category": {
          "id": "cat_1",
          "name": "Cakes"
        }
      }
    ],
    "pagination": {
      "page": 1,
      "limit": 20,
      "total": 150,
      "hasMore": true,
      "nextPage": 2
    }
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### Mobile-Optimized Product Response:
```json
{
  "success": true,
  "data": {
    "id": "prod_123",
    "name": "Chocolate Cake",
    "description": "Rich chocolate cake with cream frosting",
    "shortDescription": "Rich chocolate cake",
    "price": 25.99,
    "originalPrice": 29.99,
    "discount": 13,
    "images": [
      {
        "id": "img_1",
        "url": "https://api.bakery.com/media/img_1/medium",
        "thumbnailUrl": "https://api.bakery.com/media/img_1/thumb",
        "webpUrl": "https://api.bakery.com/media/img_1/medium.webp"
      }
    ],
    "category": {
      "id": "cat_1",
      "name": "Cakes",
      "icon": "🎂"
    },
    "isAvailable": true,
    "preparationTime": 30,
    "allergens": ["gluten", "dairy", "eggs"],
    "nutritionalInfo": {
      "calories": 350,
      "fat": 15,
      "carbs": 45,
      "protein": 8
    },
    "variants": [
      {
        "id": "var_1",
        "name": "Small (6 inch)",
        "price": 25.99,
        "isAvailable": true
      }
    ],
    "ratings": {
      "average": 4.5,
      "count": 128
    },
    "isFavorite": false
  }
}
```

## Database Schema

### Key Entities:

#### Users
- id, email, password, first_name, last_name
- role, phone, created_at, updated_at
- email_verified, active

#### Products
- id, name, description, category_id
- price, cost_price, sku, barcode
- images, nutritional_info, allergens
- active, created_at, updated_at

#### Orders
- id, customer_id, order_number
- status, total_amount, tax_amount
- order_date, delivery_date
- shipping_address, billing_address

#### Order_Items
- id, order_id, product_id
- quantity, unit_price, total_price
- special_instructions

#### Inventory
- id, product_id, quantity
- reorder_point, reorder_quantity
- last_updated, expiry_date

#### Categories
- id, name, description
- parent_id, sort_order, active

## Security Requirements

### Authentication:
- JWT tokens with refresh mechanism
- Password encryption (BCrypt)
- Rate limiting on auth endpoints
- Account lockout after failed attempts

### Authorization:
- Role-based access control
- Endpoint-level security
- Data filtering based on user role
- Audit logging

### Data Protection:
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CORS configuration

## API Standards

### Response Format:
```json
{
  "success": true,
  "data": {},
  "message": "Success message",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Error Response:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Validation failed",
    "details": []
  },
  "timestamp": "2024-01-01T10:00:00Z"
}
```

### Pagination:
```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5
}
```

## Configuration

### Application Properties:
- Database configuration
- JWT settings
- File storage settings
- Email configuration
- Payment gateway settings
- Cache configuration

### Environment Variables:
- Database credentials
- JWT secret
- API keys
- Storage credentials
- Email credentials

## Testing Strategy

### Unit Tests:
- Service layer testing
- Repository testing
- Utility function testing

### Integration Tests:
- API endpoint testing
- Database integration
- External service integration

### Performance Tests:
- Load testing
- Stress testing
- Database performance

## Deployment

### Requirements:
- Java 17+ runtime
- Database server
- Redis server (optional)
- File storage service
- Email service

### Docker Support:
- Dockerfile
- Docker Compose
- Multi-stage builds
- Health checks

## Monitoring & Logging

### Logging:
- Structured logging
- Log levels configuration
- Request/response logging
- Error tracking

### Monitoring:
- Health checks
- Metrics collection
- Performance monitoring
- Alerting

## Future Enhancements

1. **Mobile App Integration**
    - Push notifications
    - Mobile-specific endpoints
    - Offline capabilities

2. **Advanced Analytics**
    - Machine learning recommendations
    - Predictive analytics
    - Customer behavior analysis

3. **Multi-location Support**
    - Store management
    - Location-specific inventory
    - Transfer orders

4. **Integration Capabilities**
    - POS system integration
    - Accounting software integration
    - Third-party delivery services

5. **Advanced Features**
    - Real-time order tracking
    - Video streaming for cake decoration
    - AI-powered recipe suggestions

## Flutter Integration Considerations

### HTTP Client Configuration:
```dart
// Dio configuration example
final dio = Dio(BaseOptions(
  baseUrl: 'https://api.bakery.com/api',
  connectTimeout: 30000,
  receiveTimeout: 30000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
));

// Add JWT interceptor
dio.interceptors.add(InterceptorsWrapper(
  onRequest: (options, handler) {
    // Add JWT token
    final token = getStoredToken();
    if (token != null) {
      options.headers['Authorization'] = 'Bearer $token';
    }
    handler.next(options);
  },
  onError: (error, handler) {
    if (error.response?.statusCode == 401) {
      // Handle token refresh
      refreshToken();
    }
    handler.next(error);
  },
));
```

### State Management:
- Use Provider/Riverpod for global state
- Implement offline-first architecture
- Cache frequently accessed data
- Handle loading and error states consistently

### Data Models:
```dart
class Product {
  final String id;
  final String name;
  final double price;
  final String imageUrl;
  final bool isAvailable;
  final Category category;
  
  Product.fromJson(Map<String, dynamic> json)
    : id = json['id'],
      name = json['name'],
      price = json['price'].toDouble(),
      imageUrl = json['imageUrl'],
      isAvailable = json['isAvailable'],
      category = Category.fromJson(json['category']);
}
```

### Error Handling:
```dart
class ApiResponse<T> {
  final bool success;
  final T? data;
  final String? message;
  final ApiError? error;
  
  bool get hasError => !success && error != null;
  bool get hasData => success && data != null;
}
```

### Offline Storage:
- Use Hive for complex object storage
- SharedPreferences for simple key-value pairs
- Implement sync queues for offline actions
- Cache images using cached_network_image

### Push Notifications Setup:
1. Configure Firebase project
2. Add FCM dependencies
3. Handle notification permissions
4. Implement deep linking
5. Store FCM token on login

### Performance Optimizations:
- Implement lazy loading for lists
- Use pagination for large datasets
- Cache network images
- Implement pull-to-refresh
- Use efficient list builders (ListView.builder)

## Getting Started

### Backend Setup:
1. Clone the repository
2. Configure database connection
3. Set up environment variables
4. Run database migrations
5. Configure Firebase Admin SDK for push notifications
6. Set up file storage (AWS S3 or local)
7. Start the application
8. Access Swagger UI at `/swagger-ui.html`

### Flutter Integration:
1. Add required dependencies to pubspec.yaml
2. Configure Firebase for push notifications
3. Set up HTTP client with interceptors
4. Implement authentication flow
5. Create data models matching API responses
6. Set up offline storage and synchronization
7. Implement error handling and loading states

### Required Flutter Dependencies:
```yaml
dependencies:
  dio: ^5.3.2
  provider: ^6.0.5
  hive: ^2.2.3
  hive_flutter: ^1.1.0
  firebase_messaging: ^14.6.9
  firebase_core: ^2.15.1
  cached_network_image: ^3.3.0
  flutter_secure_storage: ^9.0.0
  connectivity_plus: ^5.0.1
  image_picker: ^1.0.4
  geolocator: ^10.1.0
  permission_handler: ^11.0.1

dev_dependencies:
  hive_generator: ^2.0.1
  build_runner: ^2.4.7
```

### API Integration Examples:

#### Authentication Service:
```dart
class AuthService {
  final Dio _dio;
  
  Future<ApiResponse<User>> login(String email, String password) async {
    try {
      final response = await _dio.post('/auth/login', data: {
        'email': email,
        'password': password,
      });
      
      final user = User.fromJson(response.data['data']);
      await _storeTokens(response.data['data']['tokens']);
      
      return ApiResponse.success(user);
    } catch (e) {
      return ApiResponse.error(e.toString());
    }
  }
}
```

#### Product Service:
```dart
class ProductService {
  Future<List<Product>> getProducts({
    int page = 1,
    int limit = 20,
    String? category,
    String? search,
  }) async {
    final response = await _dio.get('/products', queryParameters: {
      'page': page,
      'limit': limit,
      if (category != null) 'category': category,
      if (search != null) 'search': search,
    });
    
    return (response.data['data']['items'] as List)
        .map((json) => Product.fromJson(json))
        .toList();
  }
}
```

This specification provides a comprehensive foundation for building a robust bakery business backend API optimized for Flutter mobile applications, including mobile-specific features, offline support, and performance considerations.