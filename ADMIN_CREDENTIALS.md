# Admin Credentials and Implementation Details

## Admin Credentials

The application now has a default admin user that is automatically created on startup if it doesn't already exist:

- **Username:** admin@mybakery.com
- **Password:** Admin@123

These credentials can be modified by changing the following properties in `application.properties`:

```properties
admin.username=admin@mybakery.com
admin.password=Admin@123
```

## Implementation Details

### 1. Admin User Creation

A `DataInitializer` component has been added to create an admin user on application startup. This component:

- Checks if an admin user with the configured email already exists
- If not, creates a new admin user with the configured credentials
- Securely encodes the password using Spring Security's password encoder
- Sets the user role to ADMIN

### 2. Product Management Feature

The product management feature has been fully implemented in the web application:

#### Backend
- The existing ProductController and ProductService are used to handle product operations
- RESTful API endpoints for CRUD operations on products

#### Frontend
- Complete product management UI with:
  - Product listing with search functionality
  - Product creation form
  - Product editing capabilities
  - Product details view
  - Product deletion with confirmation

#### Features
- View all products in a table format
- Search products by name
- Add new products with detailed information
- Edit existing products
- View detailed product information
- Delete products
- Toggle product active/inactive status
- Mark products as featured

## How to Use

1. **Login as Admin:**
   - Navigate to the application URL
   - Enter the admin credentials (admin@mybakery.com / Admin@123)

2. **Access Product Management:**
   - Click on "Products" in the sidebar navigation
   - The products page will display all existing products

3. **Add a New Product:**
   - Click the "Add New Product" button
   - Fill in the product details in the form
   - Click "Save Product"

4. **Edit a Product:**
   - Find the product in the table
   - Click the edit (pencil) icon
   - Update the product details
   - Click "Save Product"

5. **View Product Details:**
   - Click the view (eye) icon next to a product
   - A modal will display detailed information about the product

6. **Delete a Product:**
   - Click the delete (trash) icon next to a product
   - Confirm the deletion when prompted

## Security Considerations

- The admin password is stored in an encoded format using BCrypt
- JWT authentication is used for securing API endpoints
- Role-based access control ensures only authorized users can access admin features