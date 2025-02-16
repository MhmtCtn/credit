# Bank Loan API

A Spring Boot REST API for managing bank loans and installments.

## Features

- Create loans with installments
- List loans by customer
- List installments by loan
- Pay loan installments
- Basic authentication
- In-memory H2 database

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

The application will start on `http://localhost:8080/api`

## Authentication

The API uses Basic Authentication. Default credentials:
- Username: admin
- Password: admin123