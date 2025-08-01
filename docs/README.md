# My Bakery Application - Sequence Diagrams

This directory contains sequence diagrams that illustrate the main flows in the My Bakery application.

## Authentication Flow

The `authentication_flow.puml` diagram shows the authentication process in the application, including:

- User Registration
- User Login
- Token Validation
- Token Refresh
- Logout

To view this diagram, you can use a PlantUML viewer or online service like [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/).

## Order Processing Flow

The `order_processing_flow.puml` diagram illustrates the order management process, including:

- Order Creation
- Order Retrieval
- Order Status Updates
- Order Cancellation
- Order Filtering (by customer, status, date range)

## Complete Application Flow

The `complete_application_flow.puml` diagram combines both authentication and order processing to show the complete application flow, particularly focusing on how protected API endpoints work with the JWT authentication.

## How to View These Diagrams

1. Install a PlantUML plugin for your IDE (IntelliJ, VS Code, etc.)
2. Or use an online PlantUML viewer like [PlantUML Web Server](http://www.plantuml.com/plantuml/uml/)
3. Open the `.puml` files with your PlantUML viewer

## Diagram Generation

These diagrams were generated based on the analysis of the following key components:

- `AuthController` - Handles authentication requests
- `JwtTokenProvider` - Manages JWT token generation and validation
- `JwtAuthenticationFilter` - Intercepts requests to validate JWT tokens
- `OrderController` - Manages order-related endpoints
- `OrderService` - Contains business logic for order processing

The sequence diagrams accurately represent the flow of data and control in the application based on the current implementation.