# SB-Ecom — Spring Boot E-Commerce REST API

A production-style **e-commerce backend** built with **Java 17** and **Spring Boot 3.2**. It exposes a
secure REST API covering the full commerce lifecycle: user authentication with JWT, role-based
authorization (customer / seller / admin), product catalog management, shopping carts, addresses,
order placement, and payments.

The project follows a clean, layered architecture (Controller → Service → Repository → Entity) with
DTO-based request/response payloads, centralized exception handling, pagination & sorting, and
stateless JWT security.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Domain Model](#domain-model)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Security](#security)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Roadmap](#roadmap)

---

## Features

- **Authentication & Authorization** — Sign-up / sign-in with JWT issued as an HTTP cookie, BCrypt
  password hashing, and three roles: `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN`.
- **Product Catalog** — Create, update, delete, search (by keyword and by category), and browse
  products with pagination and sorting. Supports per-product image upload.
- **Categories** — Public browsing and admin-managed CRUD.
- **Shopping Cart** — Add/remove products, increment/decrement quantities, and per-user cart
  retrieval with automatic special-price and total recalculation.
- **Addresses** — Full CRUD for user shipping addresses.
- **Orders & Payments** — Place orders from a cart against a selected payment method.
- **Cross-cutting** — Global exception handling, bean validation on all inputs, DTO mapping via
  ModelMapper, and configurable pagination defaults.

---

## Tech Stack

| Layer            | Technology                                            |
|------------------|-------------------------------------------------------|
| Language         | Java 17                                               |
| Framework        | Spring Boot 3.2.5 (Web, Data JPA, Security, Validation)|
| Security         | Spring Security + JJWT 0.12.6 (JWT)                    |
| Persistence      | Spring Data JPA / Hibernate, MySQL                     |
| Mapping          | ModelMapper 3.0                                        |
| Boilerplate      | Lombok                                                 |
| Build            | Maven (wrapper included)                               |
| CI               | GitHub Actions                                         |
| Testing          | JUnit 5 / Spring Boot Test                             |

---

## Architecture

The application uses a conventional **layered (n-tier) architecture**:

```
        HTTP (JSON)
            │
     ┌──────▼───────┐   @RestController — request handling, validation, DTO in/out
     │  Controllers │
     └──────┬───────┘
            │
     ┌──────▼───────┐   @Service — business logic, transactions, mapping
     │   Services   │
     └──────┬───────┘
            │
     ┌──────▼───────┐   Spring Data JPA — persistence
     │ Repositories │
     └──────┬───────┘
            │
     ┌──────▼───────┐   JPA Entities ↔ MySQL
     │   Database   │
     └──────────────┘
```

**Cross-cutting concerns**

- **Security filter chain** — A stateless `SecurityFilterChain` with a custom `AuthTokenFilter`
  validates the JWT on each request before it reaches the controllers. `/api/auth/**`,
  `/images/**`, and Swagger routes are public; everything else requires authentication.
- **DTO / payload layer** — Requests and responses use dedicated payload classes rather than exposing
  JPA entities directly, keeping the API contract decoupled from the persistence model.
- **Global exception handling** — `MyGlobalExceptionHandler` (`@RestControllerAdvice`) translates
  domain exceptions (`APIException`, `ResourceNotFoundException`) and validation errors into
  consistent JSON responses.

---

## Domain Model

Core entities and their relationships:

- **User** ⇄ **Role** — many-to-many (`user_role` join table).
- **User** → **Address** — one-to-many.
- **User** → **Cart** — one-to-one.
- **Category** → **Product** — one-to-many.
- **Product** → **CartItem** — one-to-many; **Cart** → **CartItem** — one-to-many.
- **Order** → **OrderItem** — one-to-many; **Order** → **Payment** — one-to-one; **Order** → **Address**.

---

## API Reference

All routes are prefixed with `/api`. Routes under `/public/**` are open for browsing, `/admin/**`
require an admin/seller role, and `/auth/**` handle authentication.

### Auth — `/api/auth`
| Method | Endpoint     | Description                        |
|--------|--------------|------------------------------------|
| POST   | `/signup`    | Register a new user               |
| POST   | `/signin`    | Authenticate and receive a JWT    |
| GET    | `/userName`  | Current authenticated username    |
| GET    | `/user`      | Current user details              |

### Categories — `/api`
| Method | Endpoint                          | Description             |
|--------|-----------------------------------|-------------------------|
| GET    | `/public/categories`              | List categories (paged) |
| POST   | `/public/categories`              | Create a category       |
| PUT    | `/public/categories/{categoryId}` | Update a category       |
| DELETE | `/admin/categories/{categoryId}`  | Delete a category       |

### Products — `/api`
| Method | Endpoint                                       | Description                  |
|--------|------------------------------------------------|------------------------------|
| GET    | `/public/products`                             | List products (paged)        |
| GET    | `/public/categories/{categoryId}/products`     | Products by category         |
| GET    | `/public/products/keyword/{keyword}`           | Search products by keyword   |
| POST   | `/admin/categories/{categoryId}/product`       | Add a product to a category  |
| PUT    | `/admin/products/{productId}`                  | Update a product             |
| DELETE | `/admin/products/{productId}`                  | Delete a product             |
| PUT    | `/products/{productId}/image`                  | Upload a product image       |

### Cart — `/api`
| Method | Endpoint                                                | Description                   |
|--------|---------------------------------------------------------|-------------------------------|
| POST   | `/carts/products/{productId}/quantity/{quantity}`       | Add product to cart           |
| GET    | `/carts`                                                | List all carts (admin)        |
| GET    | `/carts/users/cart`                                     | Get current user's cart       |
| PUT    | `/cart/products/{productId}/quantity/{operation}`       | Increment/decrement quantity  |
| DELETE | `/carts/{cartId}/product/{productId}`                   | Remove product from cart      |

### Addresses — `/api`
| Method | Endpoint                    | Description                    |
|--------|-----------------------------|--------------------------------|
| POST   | `/addresses`                | Create an address              |
| GET    | `/addresses`                | List all addresses             |
| GET    | `/addresses/{addressId}`    | Get an address by id           |
| GET    | `/users/addresses`          | Current user's addresses       |
| PUT    | `/addresses/{addressId}`    | Update an address              |
| DELETE | `/addresses/{addressId}`    | Delete an address              |

### Orders — `/api`
| Method | Endpoint                                  | Description                          |
|--------|-------------------------------------------|--------------------------------------|
| POST   | `/order/users/payments/{paymentMethod}`   | Place an order with a payment method |

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the bundled `./mvnw` wrapper)
- MySQL 8.x running locally (or update the datasource URL)

### 1. Clone

```bash
git clone https://github.com/omonuj/e-commerce-.git
cd e-commerce-
```

### 2. Create the database

```sql
CREATE DATABASE ecommerce;
```

### 3. Configure environment variables

Configuration is externalized via environment variables (see [Configuration](#configuration)).
Copy the example file and fill in your own values:

```bash
cp .env.example .env
```

### 4. Run

```bash
./mvnw spring-boot:run
```

The API starts on the port defined by `SERVER_PORT` (e.g. `http://localhost:8080`).

On first startup a `CommandLineRunner` seeds the three roles and demo users
(`user1`, `seller1`, `admin`) — intended for local development only.

### 5. Build a jar

```bash
./mvnw clean package
java -jar target/sb-ecom-0.0.1-SNAPSHOT.jar
```

---

## Configuration

The application reads all settings from environment variables (injected into
`src/main/resources/application.properties`). Provide them via a `.env` file or your shell.

| Variable                                   | Description                                  |
|--------------------------------------------|----------------------------------------------|
| `SPRING_APPLICATION_NAME`                  | Application name                             |
| `SERVER_PORT`                              | HTTP port (e.g. `8080`)                      |
| `SPRING_DATASOURCE_URL`                    | JDBC URL, e.g. `jdbc:mysql://localhost:3306/ecommerce` |
| `SPRING_DATASOURCE_USERNAME`               | Database username                            |
| `SPRING_DATASOURCE_PASSWORD`               | Database password                            |
| `SPRING_JPA_HIBERNATE_DDL_AUTO`            | Hibernate DDL mode (`update`, `validate`, …) |
| `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT`  | Hibernate dialect                            |
| `PROJECT_IMAGE`                            | Directory for uploaded product images        |
| `SPRING_APP_JWTSECRET`                     | JWT signing secret (Base64, keep private)    |
| `SPRING_APP_JWTEXPIRATIONMS`               | JWT expiration in milliseconds               |
| `SPRING_ECOM_APP_JWTCOOKIENAME`            | Name of the JWT cookie                       |
| `LOGGING_LEVEL_*`                          | Per-package log levels                       |

A ready-to-copy [`.env.example`](.env.example) is included.

> ⚠️ **Security note:** never commit a real `.env`. Keep secrets out of version control and rotate
> the JWT secret and database credentials before any public deployment.

---

## Security

- **Stateless JWT** — no server-side session; the token is issued on sign-in and validated per
  request by `AuthTokenFilter`.
- **Password hashing** — BCrypt via `BCryptPasswordEncoder`.
- **Role-based access** — `ROLE_USER`, `ROLE_SELLER`, `ROLE_ADMIN` gate admin/seller operations.
- **Bean validation** — all request payloads are validated with Jakarta Bean Validation annotations.

---

## Project Structure

```
src/main/java/ecom/application
├── EcomApplication.java        # Spring Boot entry point
├── config/                     # App config, constants, ModelMapper bean
├── controller/                 # REST controllers (Auth, Product, Category, Cart, Address, Order)
├── service/                    # Business logic (interfaces + Impl)
├── repositories/               # Spring Data JPA repositories
├── model/                      # JPA entities
├── payload/                    # Request/response DTOs
├── exceptions/                 # Custom exceptions + global handler
├── security/                   # Security config, JWT filter/utils, user details
└── util/                       # Helpers (AuthUtil)

src/test/java/ecom/application   # Unit / context tests
```

---

## Testing

Run the test suite with:

```bash
./mvnw test
```

Continuous integration is configured under [`.github/workflows/build.yml`](.github/workflows/build.yml)
to build and test on push and pull request.

---

## Roadmap

Potential enhancements that would round out the platform:

- OpenAPI/Swagger UI documentation (dependency hooks already present)
- Method-level security (`@PreAuthorize`) for finer-grained authorization
- Payment gateway integration and order status workflow
- Docker Compose for one-command MySQL + app startup
- Expanded unit and integration test coverage

---

## Author

**Jonah Odoh** — [GitHub @omonuj](https://github.com/omonuj)
