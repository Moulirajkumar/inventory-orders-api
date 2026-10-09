# Inventory & Orders API

A backend service built with Spring Boot 3, Java 17, Spring Data JPA, and an H2 in-memory database.

## Architecture & Domain
- **Customer**: Manages customer profiles.
- **Product**: Manages product catalog, SKUs, and stock quantities.
- **Order & OrderItem**: Manages checkout lifecycles, stock deduction, and order status.

## Tech Stack
- Java 17
- Spring Boot 3.3.4
- Spring Data JPA / Hibernate
- H2 In-Memory Database
- JUnit 5 & Mockito

## Running the Application
```bash
./mvnw spring-boot:run
```

## Running Tests
```bash
./mvnw clean test
```
