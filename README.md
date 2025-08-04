# My Bakery Management System

A comprehensive web application for managing all aspects of a bakery business, from inventory and product management to order processing and customer relationships.

## Overview

My Bakery Management System is designed to help bakery owners and staff efficiently manage their bakery operations. The system provides a user-friendly interface for managing products, inventory, orders, customers, and more.

## Features

- **User Authentication**: Secure login and registration with role-based access control
- **Dashboard**: Overview of key metrics and performance indicators
- **Product Management**: Create, update, and manage bakery products with image uploads
- **Image Management**: Cloudinary integration for product image storage and optimization
- **Inventory Management**: Track ingredients and supplies with low stock alerts
- **Order Processing**: Create and manage customer orders
- **Customer Management**: Store and manage customer information
- **Reporting**: Generate various reports for business analysis
- **Promotions**: Create and manage special offers and discounts
- **Loyalty Program**: Track customer loyalty points and rewards

## Technology Stack

- **Backend**: Spring Boot, Java
- **Database**: PostgreSQL
- **Security**: JWT (JSON Web Token) for authentication
- **Frontend**: HTML, CSS, JavaScript, Bootstrap
- **API**: RESTful API for integration with other systems

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- PostgreSQL

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/my-bakery.git
   ```

2. Navigate to the project directory:
   ```
   cd my-bakery
   ```

3. Configure the database connection in `src/main/resources/application.properties` or `src/main/resources/application-local.properties`

4. Build the project:
   ```
   ./mvnw clean install
   ```

5. Run the application:
   ```
   ./mvnw spring-boot:run
   ```

6. Access the application at `http://localhost:8080`

## Documentation

- [User Guide](docs/user-guide.md): Detailed instructions for using the system
- [API Documentation](docs/api-documentation-fixed.md): Information about the RESTful API endpoints
- [Cloudinary Setup Guide](CLOUDINARY_SETUP.md): Instructions for configuring image uploads

## Development

### Project Structure

- `src/main/java/com/aurelius/tech/mybakery`: Java source code
  - `controller`: REST controllers
  - `model`: Domain models
  - `repository`: Data access layer
  - `service`: Business logic
  - `security`: Authentication and authorization
  - `config`: Application configuration
  - `exception`: Exception handling
  - `util`: Utility classes
- `src/main/resources`: Configuration files and static resources
  - `static`: Frontend assets (HTML, CSS, JavaScript)
  - `templates`: Template files
  - `application.properties`: Application configuration

### Building for Production

To build the application for production:

```
./mvnw clean package -Pprod
```

This will create a JAR file in the `target` directory that can be deployed to a production server.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please contact support@mybakery.com or open an issue on GitHub.

## Acknowledgements

- Spring Boot
- Bootstrap
- Chart.js
- PostgreSQL
- And all other open-source libraries used in this project