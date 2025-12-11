# Blink Microservice Architecture Playground Platform

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen?style=for-the-badge&logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green?style=for-the-badge&logo=mongodb&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-red?style=for-the-badge&logo=redis&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Kafka-7.5.0-black?style=for-the-badge&logo=apachekafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-24-blue?style=for-the-badge&logo=docker&logoColor=white)
![GraphQL](https://img.shields.io/badge/GraphQL-E10098?style=for-the-badge&logo=graphql&logoColor=white)

</div>

---

## Features

### Core Features

-  **Microservices Architecture** - Independently deployable services
-  **Service Discovery** - Automatic service registration with Netflix Eureka
-  **API Gateway** - Centralized routing with Spring Cloud Gateway
-  **Event-Driven** - Asynchronous communication with Apache Kafka
-  **Caching** - High-performance caching with Redis
-  **GraphQL** - Flexible data querying with Netflix DGS
-  **JWT Authentication** - Secure user authentication
-  **Containerization** - Easy deployment with Docker

### Features

- 👤 User registration and authentication
- 📦 Product catalog and category management
- 🔍 Advanced product search and filtering
- 🛒 Shopping cart management (Redis-backed)
- 📋 Order creation and tracking
- 📧 Email notifications (Kafka-driven)
- 📍 Multiple address management
- ⭐ Featured products

---

## Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                                  CLIENTS                                      │
│                    (Web App, Mobile App, Third Party)                         │
└─────────────────────────────────────┬───────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                              API GATEWAY                                      │
│                        (Spring Cloud Gateway)                                 │
│                            Port:  8080                                         │
└─────────────────────────────────────┬───────────────────────────────────────┘
                                      │
                    ┌─────────────────┼─────────────────┐
                    │                 │                 │
                    ▼                 ▼                 ▼
┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────────┐
│   DISCOVERY SERVER   │ │      REST APIs       │ │      GraphQL         │
│      (Eureka)        │ │                      │ │    (Netflix DGS)     │
│    Port: 8761        │ │                      │ │                      │
└──────────────────────┘ └──────────────────────┘ └──────────────────────┘
                                      │
        ┌─────────────┬───────────────┼───────────────┬─────────────┐
        │             │               │               │             │
        ▼             ▼               ▼               ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
│    USER     │ │   PRODUCT   │ │    CART     │ │    ORDER    │ │NOTIFICATION │
│   SERVICE   │ │   SERVICE   │ │   SERVICE   │ │   SERVICE   │ │   SERVICE   │
│  Port: 8081 │ │  Port: 8082 │ │  Port:  8083 │ │  Port: 8084 │ │  Port: 8085 │
└──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘ └──────┬──────┘
       │               │               │               │               │
       ▼               ▼               ▼               │               │
┌─────────────┐ ┌─────────────┐ ┌─────────────┐       │               │
│   MongoDB   │ │   MongoDB   │ │    Redis    │       │               │
│    users    │ │  products   │ │    carts    │       │               │
└─────────────┘ └─────────────┘ └─────────────┘       │               │
                       │                               │               │
                       ▼                               ▼               │
                ┌─────────────┐                 ┌─────────────┐        │
                │    Redis    │                 │   MongoDB   │        │
                │   (Cache)   │                 │   orders    │        │
                └─────────────┘                 └──────┬──────┘        │
                                                       │               │
                                                       ▼               ▼
                                               ┌───────────────────────────┐
                                               │      Apache Kafka         │
                                               │   (Event Streaming)       │
                                               └───────────────┬───────────┘
                                                               │
                                                               ▼
                                                       ┌─────────────┐
                                                       │   MongoDB   │
                                                       │notifications│
                                                       └─────────────┘
```

### Event Flow (Kafka)

```
┌─────────────┐     order-created      ┌─────────────────┐
│   ORDER     │ ───────────────────▶   │                 │
│   SERVICE   │     order-updated      │  NOTIFICATION   │
│             │ ───────────────────▶   │     SERVICE     │
│             │    order-cancelled     │                 │
│             │ ───────────────────▶   │                 │
└─────────────┘                        └─────────────────┘
                                               │
                                               ▼
                                        ┌─────────────┐
                                        │   Email     │
                                        │   Service   │
                                        └─────────────┘
```

---

## Services

### 1. Discovery Server (Eureka)
| | |
|---|---|
| **Port** | 8761 |
| **Description** | Service discovery and registration center |
| **Dashboard** | http://localhost:8761 |

### 2. API Gateway
| | |
|---|---|
| **Port** | 8080 |
| **Description** | Centralized routing and load balancing |
| **Technology** | Spring Cloud Gateway |

### 3. User Service
| | |
|---|---|
| **Port** | 8081 |
| **Database** | MongoDB (blink_users) |
| **Description** | User management and JWT authentication |

**Features:**
- User registration and login
- JWT token management
- Profile management
- Multiple address management

### 4. Product Service
| | |
|---|---|
| **Port** | 8082 |
| **Database** | MongoDB (blink_products) |
| **Cache** | Redis |
| **Description** | Product and category management |

**Features:**
- Product CRUD operations
- Category hierarchy
- Advanced search and filtering
- Redis caching
- Stock management

### 5. Cart Service
| | |
|---|---|
| **Port** | 8083 |
| **Database** | Redis |
| **Description** | Shopping cart management |

**Features:**
- Fast cart operations (Redis-backed)
- Automatic cart expiration (TTL)
- Stock validation
- Product info synchronization

### 6. Order Service
| | |
|---|---|
| **Port** | 8084 |
| **Database** | MongoDB (blink_orders) |
| **Message Broker** | Apache Kafka |
| **Description** | Order management |

**Features:**
- Order creation
- Order status tracking
- Automatic stock deduction
- Kafka event publishing
- Order cancellation and refunds

### 7. Notification Service
| | |
|---|---|
| **Port** | 8085 |
| **Database** | MongoDB (blink_notifications) |
| **Message Broker** | Apache Kafka (Consumer) |
| **Description** | Notification management |

**Features:**
- Kafka event consumption
- Email notifications (Thymeleaf templates)
- Notification history
- Read/unread tracking

---

## 🛠 Technologies

### Backend
| Technology | Version | Description |
|-----------|----------|----------|
| Java | 21 | Primary programming language |
| Spring Boot | 3.3.5 | Application framework |
| Spring Cloud | 2023.0.3 | Microservices infrastructure |
| Spring Security | 6.x | Security framework |
| Netflix DGS | 9.1.3 | GraphQL framework |

### Databases & Cache
| Technology | Version | Usage |
|-----------|----------|----------|
| MongoDB | 7.0 | Primary database |
| Redis | 7.x | Cache & Session store |

### Messaging
| Technology | Version | Usage |
|-----------|----------|----------|
| Apache Kafka | 7.5.0 | Event streaming |
| Zookeeper | 7.5.0 | Kafka coordination |

### DevOps
| Technology | Version | Usage |
|-----------|----------|----------|
| Docker | 24.x | Containerization |
| Docker Compose | 2.x | Container orchestration |

### Other Libraries
| Technology | Usage |
|-----------|----------|
| Lombok | Boilerplate code reduction |
| MapStruct | Object mapping |
| Thymeleaf | Email templates |
| JWT (jjwt) | Token management |

---

## Prerequisites

### Software Requirements

- **Docker:** 24.x or higher
- **Docker Compose:** 2.x or higher
- **Java:** 21 (for development only)
- **Maven:** 3.9.x (for development only)
- **Git:** 2.x

### Port Requirements

Ensure the following ports are available: 

```
8080, 8081, 8082, 8083, 8084, 8085  (Application services)
8761                                 (Eureka)
8088, 8089, 8090                     (Management UIs)
27017                                (MongoDB)
6379                                 (Redis)
9092, 29092                          (Kafka)
2181                                 (Zookeeper)
```

---

## Installation

### Quick Start (Docker)

```bash
# 1. Clone the repository
git clone https://github.com/tarkanbatar/blink.git
cd blink

# 2. Start all services
docker-compose up --build -d

# 3. Wait for services to start (2-3 minutes)
docker-compose ps

# 4. Check Eureka Dashboard
open http://localhost:8761
```

### Step-by-Step Installation

#### 1. Clone the Repository

```bash
git clone https://github.com/tarkanbatar/blink.git
cd blink
```

#### 2. Configure Environment Variables (Optional)

```bash
# Create .env file
cat > .env << EOF
MONGO_ROOT_USERNAME=root
MONGO_ROOT_PASSWORD=rootpassword
REDIS_PASSWORD=redispassword
JWT_SECRET=mySecretKeyForJWTTokenGenerationMinimum256BitsLong123456789
EOF
```

#### 3. Start Services

```bash
# Build and start all services
docker-compose up --build -d

# Follow logs
docker-compose logs -f
```

#### 4. Verify Services Status

```bash
# Check container status
docker-compose ps

# Health check
curl http://localhost:8761/actuator/health
curl http://localhost:8080/actuator/health
```

### Development Mode

To run only infrastructure services in Docker and application services from IDE:

```bash
# Start only infrastructure (MongoDB, Redis, Kafka)
docker-compose -f docker-compose.dev.yml up -d

# Start services from IDE or manually: 
cd user-service && ./mvnw spring-boot:run &
cd product-service && ./mvnw spring-boot:run &
cd cart-service && ./mvnw spring-boot:run &
cd order-service && ./mvnw spring-boot:run &
cd notification-service && ./mvnw spring-boot:run &
```

### Stopping Services

```bash
# Stop services
docker-compose stop

# Stop and remove containers
docker-compose down

# Remove everything including volumes (WARNING: Data will be lost!)
docker-compose down -v --rmi all
```

---

## API Documentation

### Base URL

```
http://localhost:8080/api
```

### Authentication

JWT Bearer token is used for authentication.  Obtain a token via `/api/auth/login` endpoint.

```bash
# Header format
Authorization: Bearer <your_jwt_token>
```

---

### Auth Endpoints

#### Register

```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.. .",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": "6571a2b3c4d5e6f7a8b9c0d1",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password":  "Password123!"
}
```

---

### User Endpoints

#### Get Current User

```http
GET /api/users/me
Authorization: Bearer <token>
```

#### Update Profile

```http
PATCH /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "+0987654321"
}
```

#### Add Address

```http
POST /api/users/me/addresses
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Home",
  "addressLine1": "123 Main Street",
  "addressLine2":  "Apt 4B",
  "city": "New York",
  "state": "NY",
  "country": "USA",
  "zipCode": "10001",
  "isDefault": true
}
```

#### Delete Address

```http
DELETE /api/users/me/addresses/{addressId}
Authorization: Bearer <token>
```

#### Set Default Address

```http
PATCH /api/users/me/addresses/{addressId}/default
Authorization: Bearer <token>
```

---

### Product Endpoints

#### List Products

```http
GET /api/products? page=0&size=20
```

**Response:**
```json
{
  "content": [
    {
      "id": "6571a2b3c4d5e6f7a8b9c0d1",
      "name": "iPhone 15 Pro",
      "price":  999.99,
      "discountedPrice": 899.99,
      "discountPercentage": 10,
      "inStock": true,
      "brand": "Apple"
    }
  ],
  "currentPage": 0,
  "totalPages": 10,
  "totalElements": 200,
  "size": 20
}
```

#### Get Product by ID

```http
GET /api/products/{id}
```

#### Get Product by SKU

```http
GET /api/products/sku/{sku}
```

#### Search Products

```http
POST /api/products/search
Content-Type: application/json

{
  "keyword": "iPhone",
  "categoryId": "electronics-id",
  "minPrice": 500,
  "maxPrice": 1500,
  "brands": ["Apple", "Samsung"],
  "inStock": true,
  "featured": false,
  "minRating": 4.0,
  "sortBy": "price",
  "sortDirection": "asc",
  "page": 0,
  "size": 20
}
```

#### Get Featured Products

```http
GET /api/products/featured
```

#### Get Products by Category

```http
GET /api/products/category/{categoryId}? page=0&size=20
```

#### Create Product (Admin)

```http
POST /api/products
Authorization: Bearer <admin_token>
Content-Type:  application/json

{
  "name": "iPhone 15 Pro",
  "description": "Apple iPhone 15 Pro 256GB",
  "sku": "IPHONE-15-PRO-256",
  "price": 999.99,
  "discountedPrice": 899.99,
  "categoryId": "6571a2b3c4d5e6f7a8b9c0d1",
  "stock": 100,
  "brand": "Apple",
  "images": [
    "https://example.com/iphone15-1.jpg",
    "https://example.com/iphone15-2.jpg"
  ],
  "attributes": {
    "color": "Titanium Black",
    "storage": "256GB",
    "ram": "8GB"
  },
  "featured": true
}
```

#### Update Product (Admin)

```http
PATCH /api/products/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "price": 949.99,
  "stock": 150,
  "featured": true
}
```

#### Update Stock (Admin)

```http
PATCH /api/products/{id}/stock? quantity=100
Authorization: Bearer <admin_token>
```

#### Delete Product (Admin)

```http
DELETE /api/products/{id}
Authorization: Bearer <admin_token>
```

---

### Category Endpoints

#### List All Categories

```http
GET /api/categories
```

#### Get Root Categories

```http
GET /api/categories/root
```

#### Get Category by ID

```http
GET /api/categories/{id}
```

#### Get Subcategories

```http
GET /api/categories/{id}/subcategories
```

#### Create Category (Admin)

```http
POST /api/categories
Authorization:  Bearer <admin_token>
Content-Type: application/json

{
  "name": "Electronics",
  "description": "Electronic devices and accessories",
  "parentId": null,
  "imageUrl": "https://example.com/electronics.jpg",
  "displayOrder": 1
}
```

#### Update Category (Admin)

```http
PUT /api/categories/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "name": "Electronics & Gadgets",
  "description": "Updated description",
  "displayOrder": 2
}
```

#### Delete Category (Admin)

```http
DELETE /api/categories/{id}
Authorization: Bearer <admin_token>
```

---

### Cart Endpoints

#### Get Cart

```http
GET /api/cart
X-User-Id: user123
```

**Response:**
```json
{
  "userId": "user123",
  "items": [
    {
      "productId": "6571a2b3c4d5e6f7a8b9c0d1",
      "productName": "iPhone 15 Pro",
      "productImage": "https://example.com/iphone15.jpg",
      "unitPrice": 899.99,
      "quantity":  2,
      "totalPrice": 1799.98
    }
  ],
  "totalItems": 2,
  "totalPrice": 1799.98,
  "empty": false,
  "createdAt": "2024-12-05T10:30:00",
  "updatedAt": "2024-12-05T14:45:00"
}
```

#### Add to Cart

```http
POST /api/cart/items
X-User-Id: user123
Content-Type: application/json

{
  "productId": "6571a2b3c4d5e6f7a8b9c0d1",
  "quantity": 2
}
```

#### Update Item Quantity

```http
PATCH /api/cart/items/{productId}
X-User-Id: user123
Content-Type:  application/json

{
  "quantity": 3
}
```

#### Remove Item from Cart

```http
DELETE /api/cart/items/{productId}
X-User-Id: user123
```

#### Clear Cart

```http
DELETE /api/cart
X-User-Id: user123
```

---

### Order Endpoints

#### Create Order

```http
POST /api/orders
X-User-Id: user123
Content-Type: application/json

{
  "shippingAddress": {
    "fullName": "John Doe",
    "phone": "+1234567890",
    "addressLine1": "123 Main Street",
    "addressLine2": "Apt 4B",
    "city": "New York",
    "state":  "NY",
    "country":  "USA",
    "zipCode": "10001"
  },
  "paymentMethod": "CREDIT_CARD",
  "notes": "Please leave at the door"
}
```

**Response:**
```json
{
  "id": "6571a2b3c4d5e6f7a8b9c0d1",
  "orderNumber": "ORD-20241205-A1B2C",
  "userId": "user123",
  "items": [... ],
  "totalItems": 2,
  "status": "PENDING",
  "statusDescription": "Pending",
  "subtotal": 1799.98,
  "shippingCost": 9.99,
  "tax":  162.00,
  "discount": 0,
  "totalAmount": 1971.97,
  "paymentMethod": "CREDIT_CARD",
  "cancellable": true,
  "createdAt": "2024-12-05T14:30:00"
}
```

#### Get Order by ID

```http
GET /api/orders/{orderId}
```

#### Get Order by Order Number

```http
GET /api/orders/number/{orderNumber}
```

#### Get My Orders

```http
GET /api/orders/my-orders? page=0&size=10
X-User-Id: user123
```

#### Get Orders by Status (Admin)

```http
GET /api/orders/status/{status}? page=0&size=10
Authorization: Bearer <admin_token>
```

**Available Statuses:**
- `PENDING` - Order created, awaiting payment
- `CONFIRMED` - Payment received, order confirmed
- `PROCESSING` - Order is being prepared
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `CANCELLED` - Order cancelled
- `REFUNDED` - Order refunded

#### Update Order Status (Admin)

```http
PATCH /api/orders/{orderId}/status
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "status": "SHIPPED",
  "trackingNumber": "TR123456789",
  "note": "Shipped via FedEx"
}
```

#### Cancel Order

```http
POST /api/orders/{orderId}/cancel? reason=Changed my mind
X-User-Id: user123
```

---

### Notification Endpoints

#### Get My Notifications

```http
GET /api/notifications? page=0&size=20
X-User-Id: user123
```

**Response:**
```json
[
  {
    "id":  "6571a2b3c4d5e6f7a8b9c0d1",
    "userId": "user123",
    "type": "ORDER_CREATED",
    "typeDescription": "Order Created",
    "channel": "EMAIL",
    "status": "SENT",
    "title": "Order Created - ORD-20241205-A1B2C",
    "content": "Your order has been created successfully.",
    "orderId": "order123",
    "orderNumber": "ORD-20241205-A1B2C",
    "sentAt": "2024-12-05T14:30:00",
    "readAt": null,
    "createdAt": "2024-12-05T14:30:00"
  }
]
```

#### Get Unread Count

```http
GET /api/notifications/unread-count
X-User-Id: user123
```

**Response:**
```json
{
  "unreadCount": 5
}
```

#### Get Notification by ID

```http
GET /api/notifications/{id}
```

#### Mark as Read

```http
PATCH /api/notifications/{id}/read
```

#### Mark All as Read

```http
PATCH /api/notifications/read-all
X-User-Id: user123
```

---

## GraphQL

### Endpoint

```
POST /graphql
```

### GraphQL Playground

```
http://localhost:8080/graphiql
```

### Sample Queries

#### List Products

```graphql
query GetProducts {
  products(page: 0, size: 10) {
    content {
      id
      name
      description
      price
      discountedPrice
      discountPercentage
      inStock
      brand
      categoryName
      images
      rating
      reviewCount
    }
    totalElements
    totalPages
    currentPage
    first
    last
  }
}
```

#### Get Product Details

```graphql
query GetProduct($id: ID!) {
  product(id: $id) {
    id
    name
    description
    sku
    price
    discountedPrice
    stock
    brand
    categoryId
    categoryName
    attributes {
      key
      value
    }
    images
    rating
    reviewCount
    active
    featured
    createdAt
  }
}
```

#### Search Products

```graphql
query SearchProducts {
  searchProducts(filter: {
    keyword: "iPhone"
    minPrice: 500
    maxPrice: 1500
    inStock: true
    sortBy: "price"
    sortDirection: "asc"
    page: 0
    size:  10
  }) {
    content {
      id
      name
      price
      brand
      inStock
    }
    totalElements
    totalPages
  }
}
```

#### Get Featured Products

```graphql
query GetFeaturedProducts {
  featuredProducts {
    id
    name
    price
    discountedPrice
    images
    brand
  }
}
```

#### Get Categories

```graphql
query GetCategories {
  categories {
    id
    name
    description
    parentId
    imageUrl
    displayOrder
    subCategories {
      id
      name
    }
  }
}
```

#### Get Root Categories with Subcategories

```graphql
query GetRootCategories {
  rootCategories {
    id
    name
    description
    subCategories {
      id
      name
      subCategories {
        id
        name
      }
    }
  }
}
```

#### Get Cart

```graphql
query GetCart($userId: String!) {
  cart(userId: $userId) {
    userId
    items {
      productId
      productName
      productImage
      unitPrice
      quantity
      totalPrice
    }
    totalItems
    totalPrice
    empty
    createdAt
    updatedAt
  }
}
```

#### Get My Orders

```graphql
query GetMyOrders($userId: String!) {
  myOrders(userId: $userId, page: 0, size: 10) {
    id
    orderNumber
    status
    statusDescription
    totalAmount
    totalItems
    trackingNumber
    estimatedDeliveryDate
    cancellable
    createdAt
    items {
      productName
      quantity
      unitPrice
      totalPrice
    }
    shippingAddress {
      fullName
      city
      country
    }
  }
}
```

#### Get Order Details

```graphql
query GetOrder($id: ID!) {
  order(id: $id) {
    id
    orderNumber
    userId
    userEmail
    status
    statusDescription
    items {
      productId
      productName
      productImage
      sku
      unitPrice
      quantity
      totalPrice
    }
    shippingAddress {
      fullName
      phone
      addressLine1
      addressLine2
      city
      state
      country
      zipCode
    }
    subtotal
    shippingCost
    tax
    discount
    totalAmount
    paymentMethod
    trackingNumber
    estimatedDeliveryDate
    cancellable
    createdAt
  }
}
```

### Sample Mutations

#### Add to Cart

```graphql
mutation AddToCart($userId: String!, $productId: String!, $quantity: Int!) {
  addToCart(userId: $userId, input: {
    productId: $productId
    quantity: $quantity
  }) {
    userId
    items {
      productId
      productName
      quantity
      totalPrice
    }
    totalItems
    totalPrice
  }
}
```

#### Update Cart Item Quantity

```graphql
mutation UpdateCartQuantity($userId: String!, $productId: String!, $quantity: Int!) {
  updateCartItemQuantity(
    userId: $userId
    productId: $productId
    quantity: $quantity
  ) {
    userId
    items {
      productId
      quantity
      totalPrice
    }
    totalPrice
  }
}
```

#### Remove from Cart

```graphql
mutation RemoveFromCart($userId: String!, $productId: String!) {
  removeFromCart(userId: $userId, productId:  $productId) {
    userId
    items {
      productId
      productName
    }
    totalItems
    totalPrice
  }
}
```

#### Clear Cart

```graphql
mutation ClearCart($userId: String!) {
  clearCart(userId: $userId)
}
```

#### Create Order

```graphql
mutation CreateOrder($userId: String!) {
  createOrder(
    userId: $userId
    input: {
      shippingAddress: {
        fullName: "John Doe"
        phone:  "+1234567890"
        addressLine1: "123 Main Street"
        city: "New York"
        state: "NY"
        country:  "USA"
        zipCode:  "10001"
      }
      paymentMethod: "CREDIT_CARD"
      notes:  "Please leave at the door"
    }
  ) {
    id
    orderNumber
    status
    totalAmount
    items {
      productName
      quantity
      unitPrice
    }
  }
}
```

#### Update Order Status (Admin)

```graphql
mutation UpdateOrderStatus($orderId: ID!) {
  updateOrderStatus(
    orderId: $orderId
    input: {
      status:  SHIPPED
      trackingNumber: "TR123456789"
    }
  ) {
    id
    orderNumber
    status
    statusDescription
    trackingNumber
  }
}
```

#### Cancel Order

```graphql
mutation CancelOrder($orderId: ID!) {
  cancelOrder(orderId: $orderId, reason: "Changed my mind") {
    id
    orderNumber
    status
    statusDescription
    cancellationReason
  }
}
```

#### Create Product (Admin)

```graphql
mutation CreateProduct {
  createProduct(input: {
    name: "MacBook Pro 16"
    description: "Apple MacBook Pro 16-inch M3 Pro"
    sku: "MACBOOK-PRO-16-M3"
    price: 2499.99
    categoryId: "category-id"
    stock: 50
    brand: "Apple"
    images: ["https://example.com/macbook. jpg"]
    attributes: [
      { key: "processor", value: "M3 Pro" }
      { key: "ram", value: "18GB" }
      { key:  "storage", value: "512GB" }
    ]
    featured: true
  }) {
    id
    name
    sku
    price
    stock
  }
}
```

#### Create Category (Admin)

```graphql
mutation CreateCategory {
  createCategory(input: {
    name: "Laptops"
    description: "Laptop computers"
    parentId: "electronics-category-id"
    displayOrder: 1
  }) {
    id
    name
    parentId
  }
}
```

---

### Health Check Script

```bash
#!/bin/bash

echo "Checking service health..."
echo ""

services=(
  "8761: Discovery Server"
  "8080:API Gateway"
  "8081:User Service"
  "8082:Product Service"
  "8083:Cart Service"
  "8084:Order Service"
  "8085:Notification Service"
)

for service in "${services[@]}"; do
  port="${service%%:*}"
  name="${service##*:}"
  
  status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health)
  
  if [ "$status" == "200" ]; then
    echo "$name (port $port): Healthy"
  else
    echo "$name (port $port): Unhealthy (HTTP $status)"
  fi
done
```

### Running Unit Tests

```bash
# Run tests for all services
./mvnw test

# Run tests for specific service
cd user-service && ./mvnw test
cd product-service && ./mvnw test
```

---

## 📈 Monitoring

### Access URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Eureka Dashboard | http://localhost:8761 | - |
| Mongo Express | http://localhost:8088 | admin / admin123 |
| Redis Commander | http://localhost:8089 | - |
| Kafka UI | http://localhost:8090 | - |

### Viewing Logs

```bash
# All services logs
docker-compose logs -f

# Specific service logs
docker-compose logs -f user-service
docker-compose logs -f order-service
docker-compose logs -f notification-service

# Last 100 lines
docker-compose logs --tail=100 -f
```

### Container Metrics

```bash
# CPU and Memory usage
docker stats

# Disk usage
docker system df

# View specific container resources
docker stats blink-user-service
```

### Useful Commands

```bash
# Restart a specific service
docker-compose restart user-service

# Rebuild and restart a service
docker-compose up --build -d user-service

# View container processes
docker-compose top

# Execute command in container
docker-compose exec user-service sh
```

---

## 📁 Project Structure

```
blink-microservices/
│
├── 📁 discovery-server/          # Eureka Server
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   │           ├── application.yml
│   │           └── application-docker.yml
│   ├── Dockerfile
│   ├── . dockerignore
│   └── pom.xml
│
├── 📁 api-gateway/               # Spring Cloud Gateway
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📁 user-service/              # User Management
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/blink/user/
│   │       │       ├── config/
│   │       │       │   ├── SecurityConfig. java
│   │       │       │   └── MongoConfig.java
│   │       │       ├── controller/
│   │       │       │   ├── AuthController.java
│   │       │       │   └── UserController.java
│   │       │       ├── datafetcher/
│   │       │       │   └── UserDataFetcher.java
│   │       │       ├── dto/
│   │       │       │   ├── request/
│   │       │       │   └── response/
│   │       │       ├── exception/
│   │       │       ├── model/
│   │       │       │   ├── User.java
│   │       │       │   └── Address.java
│   │       │       ├── repository/
│   │       │       ├── security/
│   │       │       │   ├── JwtService.java
│   │       │       │   └── JwtAuthenticationFilter.java
│   │       │       └── service/
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── application-docker.yml
│   │           └── schema/
│   │               └── schema.graphqls
│   ├── Dockerfile
│   └── pom.xml
│
├── 📁 product-service/           # Product Catalog
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📁 cart-service/              # Shopping Cart
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
│
├── 📁 order-service/             # Order Management
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── com/blink/order/
│   │               ├── client/
│   │               │   ├── CartServiceClient.java
│   │               │   ├── ProductServiceClient.java
│   │               │   └── UserServiceClient.java
│   │               ├── event/
│   │               │   ├── OrderEvent.java
│   │               │   └── OrderEventPublisher.java
│   │               └── ... 
│   ├── Dockerfile
│   └── pom. xml
│
├── 📁 notification-service/      # Notifications
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/blink/notification/
│   │       │       ├── consumer/
│   │       │       │   └── OrderEventConsumer.java
│   │       │       ├── template/
│   │       │       │   └── EmailTemplateService.java
│   │       │       └── ...
│   │       └── resources/
│   │           └── templates/
│   │               ├── order-created.html
│   │               ├── order-shipped.html
│   │               └── ... 
│   ├── Dockerfile
│   └── pom. xml
│
├── 📄 docker-compose.yml         # Production compose
├── 📄 docker-compose. dev.yml     # Development compose
├── 📄 . env. example               # Environment example
├── 📄 . gitignore
├── 📄 LICENSE
└── 📄 README.md
```

---

## 📜 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Tarkan Batar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 👨‍💻 Author

**Tarkan Batar**

- GitHub: [@tarkanbatar](https://github.com/tarkanbatar)
- LinkedIn: [Tarkan Batar](https://linkedin.com/in/tarkanbatar)

---


<div align="center">

---

Made by Tarkan Batar

</div>