# Implementation Plan: Advanced Search, Filtering, and Product Review System

Introduce two key functional features to the **Furniro Product Service** to complete its core business capabilities:
1. **Advanced Searching, Dynamic Filtering & Sorting**: Enable buyers to find furniture via keyword and dynamic filters (category, price range, brand, color, size, material) with custom sorting and pagination.
2. **Product Review & Rating System**: Complete the review subsystem by adding repositories, services, DTOs, and REST controller endpoints to create, fetch, and aggregate ratings.

---

## User Review Required

> [!IMPORTANT]
> - **Soft Deletes vs. Hard Deletes**: For category and product operations, we propose soft deletes (updating `ProductStatus` to `INACTIVE` or `DELETED` instead of purging rows) to protect transactional history and foreign key dependencies.
> - **Rating Cache**: To optimize reading product lists, we propose adding `averageRating` and `reviewCount` fields directly to the `Product` entity, recalculating them asynchronously or when a new review is persisted, rather than executing database aggregations (`SELECT AVG(rating)...`) on every page load.

---

## Open Questions

> [!NOTE]
> 1. Do we need an authentication integration (e.g., Spring Security / JWT filter check) for `POST /products/{id}/reviews` to verify that `userID` matches the logged-in customer, or is this validation handled at an API Gateway level? (We will assume API Gateway validation/extraction of headers for now).
> 2. For product filtering, are color and size names strictly matched against the `ColorMaster` and `SizeMaster` tables, or should we filter by their plain string codes? (We will match by IDs/codes for precision).

---

## Proposed Changes

We will implement this in a logical order:
1. Repository & Specifications for Product.
2. Review Component (DTO, Mapper, Repository, Service, Controller).
3. Product Search and Sorting updates in Product Service.

---

### 1. Database Repositories & Specifications

We will introduce Spring Data specifications to dynamic query the database.

#### [NEW] [ProductSpecification.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/database/specification/ProductSpecification.java)
- Build a dynamic `org.springframework.data.jpa.domain.Specification<Product>` class.
- Support filters for:
  - Keyword (matches brand or name case-insensitively).
  - Category (ID matching).
  - Price Range (`basePrice` between min and max).
  - Color (filtering nested variants matching color ID/name).
  - Size (filtering nested variants matching size ID/name).

#### [NEW] [ReviewRepository.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/database/repository/ReviewRepository.java)
- Standard JPA Repository extending `JpaRepository<Review, Integer>` to handle persistence operations for product reviews.
- Include paginated query: `Page<Review> findByProduct_ProductID(Integer productId, Pageable pageable)`.
- Include average query: `@Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productID = :productId")`.

---

### 2. Data Transfer Objects (DTOs) & Mappers

#### [NEW] [ReviewReq.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/dto/req/ReviewReq.java)
- Request payload DTO containing:
  - `userID` (Integer, validated `@NotNull`)
  - `rating` (Integer, validated `@Min(1) @Max(5)`)
  - `comment` (String, validated `@Size(max = 1000)`)

#### [NEW] [ReviewRes.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/dto/res/ReviewRes.java)
- Response payload DTO returning formatted reviews, including `reviewID`, `userID`, `rating`, `comment`, and `createdAt`.

#### [NEW] [ReviewMapper.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/dto/mapper/ReviewMapper.java)
- MapStruct mapper to cleanly convert `ReviewReq` to `Review` entity and `Review` entity to `ReviewRes`.

---

### 3. Service Layer

#### [NEW] [ReviewService.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/service/ReviewService.java)
- Methods:
  - `createReview(Integer productId, ReviewReq reviewReq)`: Saves the review, updates average product rating cache.
  - `getProductReviews(Integer productId, int page, int size)`: Retrieves paginated reviews.

#### [MODIFY] [ProductService.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/service/ProductService.java)
- Add the `searchProducts` method:
  ```java
  public ResponseEntity<AType> searchProducts(
          String query, Integer categoryID, BigDecimal minPrice, BigDecimal maxPrice,
          Integer colorID, Integer sizeID, String sortBy, int page, int size)
  ```
- Build sort parameters based on requested string (e.g., `price_asc` -> `Sort.by("basePrice").ascending()`).

---

### 4. REST Controllers

#### [NEW] [ReviewController.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/controller/ReviewController.java)
- Endpoints:
  - `POST /products/{id}/reviews` (submit review)
  - `GET /products/{id}/reviews` (get reviews paginated)

#### [MODIFY] [ProductController.java](file:///Users/anh09/Downloads/project/Furniro/backend/ProductService/src/main/java/com/furniro/ProductService/controller/ProductController.java)
- Expose the new search endpoint:
  ```java
  @GetMapping("/search")
  public ResponseEntity<AType> search(
          @RequestParam(required = false) String query,
          @RequestParam(required = false) Integer categoryID,
          @RequestParam(required = false) BigDecimal minPrice,
          @RequestParam(required = false) BigDecimal maxPrice,
          @RequestParam(required = false) Integer colorID,
          @RequestParam(required = false) Integer sizeID,
          @RequestParam(defaultValue = "newest") String sortBy,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "12") int size)
  ```

---

## Verification Plan

### Automated Tests
- Run integration tests using Spring Boot Test profile to confirm database transactions work successfully:
  ```bash
  ./mvnw test
  ```
- Write unit tests for `ProductSpecification` construction to ensure correct query generation.

### Manual Verification
- Launch the application:
  ```bash
  ./mvnw spring-boot:run
  ```
- Validate endpoints via the integrated Swagger UI:
  - `http://localhost:8083/swagger-ui/index.html`
- Submit reviews for a product, query them, and verify that dynamic searches successfully filter products by price ranges, color/size variants, and category ID.
