# 📦 Furniro Product Service

The **Product Service** is the core catalog management component of the Furniro e-commerce platform. It handles products, categories, and product image metadata, ensuring a seamless shopping experience. It integrates with the **Upload Service** via Kafka to maintain synchronization between database records and cloud storage.

---

## ✨ Features

- 📂 **Category Management**: Create, update, and organize product categories.
- 🛒 **Product Catalog**: Paginated retrieval of products with detailed information.
- 🖼️ **Image Metadata Tracking**: Manage product images and their display order.
- ⚖️ **Product Comparison**: Compare multiple products by their unique identifiers.
- 📡 **Event-Driven Architecture**: Communicates with the Upload Service via Kafka for image lifecycle management.
- 📖 **OpenAPI Documentation**: Fully documented REST API for easy integration.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3
- **Language**: Java 17
- **Database**: MySQL (Product & Category persistence)
- **Messaging**: Apache Kafka (Event producer)
- **Documentation**: Springdoc OpenAPI (Swagger)

---

## ⚙️ Setup & Installation

### 1. Prerequisites
- **Java 17** or higher
- **Maven**
- **MySQL Instance**
- **Kafka Cluster**

### 2. Environment Configuration
Required environment variables:

```properties
# Server Configuration
SERVER_PORT=8083

# Database Configuration
DATABASE_URL=jdbc:mysql://localhost:3306/furniro_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

### 3. Run the Application
```bash
./mvnw clean spring-boot:run
```

---

## 📡 API Reference

### Category Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/categories` | Retrieve all categories. |
| `POST` | `/categories` | Create a new category. |
| `PUT` | `/categories/{id}` | Update an existing category. |
| `DELETE` | `/categories/{id}` | Delete a category. |

### Product Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/products` | Get a paginated list of products. |
| `GET` | `/products/{id}` | Get detailed information for a specific product. |
| `POST` | `/products/compare` | Compare products by a list of IDs. |
| `GET` | `/products/category/{categoryID}` | List products in a specific category (paginated). |

### Product Image Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/product-images/{id}` | Get product image details by ID. |
| `GET` | `/product-images/product/{productID}` | Get all images for a specific product. |
| `POST` | `/product-images` | Add a new product image (triggers `upload.active`). |
| `PUT` | `/product-images` | Update a product image (triggers `upload.active`). |
| `DELETE` | `/product-images/{id}` | Delete a product image (triggers `upload.delete`). |

---

## 🎡 Kafka Events

### Producer
The Product Service acts as a producer for the following topics:

| Topic | Event Trigger | Description | Payload Example |
| :--- | :--- | :--- | :--- |
| `upload.active` | Add/Update Image | Notifies UploadService to activate the file. | `{"fileID": 123}` |
| `upload.delete` | Delete Image | Notifies UploadService to remove the file from storage. | `{"fileID": 123}` |

---

## 📂 Project Structure

```text
src/main/java/com/furniro/ProductService/
├── config/         # Configuration classes (Kafka, OpenAPI, etc.)
├── controller/     # REST Controllers (Product, Category, Image)
├── database/       # JPA Entities & Repositories
├── dto/            # Request/Response DTOs
├── exception/      # Custom Exception Handlers
├── service/        # Core Business Logic
│   └── kafka/      # Kafka Event Producers
└── utils/          # Error Codes and Constants
```

---

## 📖 API Documentation
Access the interactive documentation at:
- `http://localhost:8083/swagger-ui/index.html` (Port depends on `SERVER_PORT`)
