# ProductService

The `ProductService` manages products, categories, and product images for the Furniro e-commerce platform. It communicates with other services via Kafka for image management.

## API Endpoints

### Category Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/categories` | Get all categories |
| `POST` | `/categories` | Create a new category |
| `PUT` | `/categories/{id}` | Update an existing category |
| `DELETE` | `/categories/{id}` | Delete a category |

### Product Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/products` | Get a paginated list of products |
| `GET` | `/products/{id}` | Get detailed information for a specific product |
| `POST` | `/products/compare` | Compare products by a list of IDs |
| `GET` | `/products/category/{categoryID}` | Get a paginated list of products in a specific category |

### Product Image Management
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/product-images/{id}` | Get product image details by ID |
| `GET` | `/product-images/product/{productID}` | Get all images for a specific product |
| `POST` | `/product-images` | Add a new product image |
| `PUT` | `/product-images` | Update an existing product image |
| `DELETE` | `/product-images/{id}` | Delete a product image |

## Kafka Events

### Producer
The service emits events to the following topics to notify the `UploadService` or other interested services:

| Topic | Event Trigger | Payload |
| :--- | :--- | :--- |
| `upload.active` | Product image added or updated | `{"fileID": Integer}` |
| `upload.delete` | Product image deleted | `{"fileID": Integer}` |

## Technology Stack
- **Framework:** Spring Boot
- **Database:** MySQL
- **Messaging:** Apache Kafka
- **Cache:** Redis
