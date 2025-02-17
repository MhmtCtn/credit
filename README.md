# Bank Loan API

A Spring Boot REST API for managing bank loans and installments.

## Features

- Create loans with installments
- List loans by customer
- List installments by loan
- Pay loan installments
- Role-based access control (Admin and Customer roles)
- Basic authentication
- OpenAPI/Swagger documentation
- In-memory H2 database

## Technology Stack

- Java 21
- Spring Boot 3.2.2
- Spring Security with Basic Auth
- Spring Data JPA
- H2 Database
- Lombok
- OpenAPI 3.0 (Swagger)
- Maven

## Requirements

- Java 21
- Maven 3.6 or higher (The project already includes Maven Wrapper, you can use it)

## Building the Project

Using Maven:
```bash
mvn clean install
```

Using Maven Wrapper:
```bash
# On Windows
./mvnw.cmd clean install

# On Unix-based systems (Linux/MacOS)
./mvnw clean install
```

## Running the Application

Using Maven:
```bash
mvn spring-boot:run
```

Using Maven Wrapper:
```bash
# On Windows
./mvnw.cmd spring-boot:run

# On Unix-based systems (Linux/MacOS)
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080/credit`

## API Documentation

The API documentation is available via Swagger UI:
- Swagger UI: `http://localhost:8080/credit/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/credit/v3/api-docs`

## Authentication

The API uses Basic Authentication. Default credentials:
### Admin User
- Username: `admin`
- Password: `admin123`
- Role: ADMIN
- Can access all endpoints and manage any customer's loans

### Regular Customers
- Sample customer credentials:
    - Username: `mcetin`
    - Password: `pass123`
    - Role: CUSTOMER
    - Can only manage their own loans

## API Endpoints

### Customer Management
```http
POST /credit/customers/register
Content-Type: application/json

{
    "name": "Mehmet",
    "surname": "Çetin",
    "username": "mcetin",
    "password": "pass123",
    "email": "mehmet.cetin@example.com"
}
```

### Loan Management
```http
# Create Loan
POST /credit/loans/create
Content-Type: application/json
Authorization: Basic base64(username:password)

{
    "customerId": 1,
    "amount": 1000.00,
    "interestRate": 0.1,
    "numberOfInstallments": 6
}

# List Loans by Customer
GET /credit/loans/customer/{customerId}
Authorization: Basic base64(username:password)

# List Installments by Loan
GET /credit/loans/{loanId}/installments
Authorization: Basic base64(username:password)

# Pay Loan Installments
POST /credit/loans/pay
Content-Type: application/json
Authorization: Basic base64(username:password)

{
    "loanId": 1,
    "amount": 183.33
}
```

## Security Rules

1. Admin can access and manage all loans and customers
2. Customers can only:
    - Create loans for themselves
    - View their own loans
    - Pay installments for their own loans
3. All endpoints except registration require authentication
4. Passwords are encrypted using BCrypt

## Database Access

H2 Console is available at `http://localhost:8080/credit/h2-console` with the following credentials:
- JDBC URL: `jdbc:h2:mem:loandb`
- Username: `admin`
- Password: `admin123`

## Testing

Using Maven:
```bash
mvn test
```

Using Maven Wrapper:
```bash
# On Windows
./mvnw.cmd test

# On Unix-based systems (Linux/MacOS)
./mvnw test
```

## Response Format

All API responses follow this format:
```json
{
    "success": true,
    "message": "SUCCESS",
    "data": {
        // Response data here
    },
    "errors": null,
    "errorCode": 0,
    "timestamp_unix": 1234567890,
    "timestamp_iso8601": "2024-01-01T12:00:00.000Z",
    "path": "/api/endpoint/path"
}
```