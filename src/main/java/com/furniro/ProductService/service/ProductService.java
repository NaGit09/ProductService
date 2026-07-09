package com.furniro.ProductService.service;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.Wishlist;
import com.furniro.ProductService.database.entity.ProductSpecification;
import com.furniro.ProductService.database.entity.Warranty;
import com.furniro.ProductService.database.repository.ProductRepository;
import com.furniro.ProductService.database.repository.WishlistRepository;
import com.furniro.ProductService.database.repository.CategoryRepository;
import com.furniro.ProductService.database.entity.Category;
import com.furniro.ProductService.dto.API.AType;
import com.furniro.ProductService.dto.API.ApiType;
import com.furniro.ProductService.dto.mapper.ProductMapper;
import com.furniro.ProductService.dto.res.ProductCompareRes;
import com.furniro.ProductService.dto.res.ProductDetailRes;
import com.furniro.ProductService.dto.res.ProductListRes;
import com.furniro.ProductService.dto.req.ProductUpdateReq;
import com.furniro.ProductService.dto.req.ProductCreateReq;
import com.furniro.ProductService.utils.ProductStatus;
import com.furniro.ProductService.dto.API.ErrorType;
import com.furniro.ProductService.exception.CustomException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final WishlistRepository wishlistRepository;
    private final ProductCacheService productCacheService;
    private final CategoryRepository categoryRepository;

    // Product Management
    public ResponseEntity<AType> getTotalProduct() {
        Long total = productRepository.count();
        return ResponseEntity.ok(ApiType.success(total));
    }

    public ResponseEntity<AType> getProducts(Integer page, Integer size) {
        // 1. validate page and size
        if (page == null || size == null) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        // 2. create pageable
        Pageable pageable = PageRequest.of(page, size);

        // 3. find products
        Page<ProductListRes> products = productRepository.getProductList(pageable);

        // 4. response
        return ResponseEntity.ok(ApiType.success(products));
    }

    public ResponseEntity<AType> getProductDetail(Integer id) {
        // 1. validate id
        if (id == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        // 2. retrieve cached detail
        ProductDetailRes productDetailRes = productCacheService.getProductDetail(id);

        // 3. response
        return ResponseEntity.ok(ApiType.success(productDetailRes));
    }

    public ResponseEntity<AType> getProductsByCategory(
            Integer page,
            Integer size,
            Integer categoryID) {

        // 1. validate page and size
        if (page == null || size == null) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        // 2. validate categoryID
        if (categoryID == null) {
            throw new CustomException(ErrorType.notFound("Category not found"));
        }

        // 3. create pageable
        Pageable pageable = PageRequest.of(page, size);

        // 4. find products
        Page<ProductListRes> products = productRepository.getProductListByCategoryID(pageable, categoryID);

        // 5. response
        return ResponseEntity.ok(ApiType.success(products));
    }

    public ResponseEntity<AType> compareProducts(List<Integer> ids) {
        // 1. validate ids
        if (ids == null || ids.isEmpty()) {
            throw new CustomException(ErrorType.badRequest("Product ids cannot be empty"));
        }

        // 2. check duplicate
        if (ids.size() != new HashSet<>(ids).size()) {
            throw new CustomException(ErrorType.badRequest("Duplicate products detected"));
        }

        // 3. check maximum comparison
        if (ids.size() > 3) {
            throw new CustomException(ErrorType.badRequest("Maximum 3 products can be compared at once"));
        }

        // 4. find products
        List<ProductCompareRes> result = productRepository.compareProducts(ids);

        // 5. response
        return ResponseEntity.ok(ApiType.success(result));
    }

    // Wishlist Management
    public ResponseEntity<AType> getWishlistProducts(Integer userId, Integer page, Integer size) {
        if (userId == null) {
            throw new CustomException(ErrorType.notFound("User not found"));
        }

        if (page == null || size == null || page < 0 || size <= 0) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductListRes> products = wishlistRepository.findWishlistProductsByUserId(userId, pageable)
                .map(productMapper::toListRes);

        return ResponseEntity.ok(ApiType.success(products));

    }

    public ResponseEntity<AType> addToWishlist(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found")));

        boolean existed = wishlistRepository.existsByUserIdAndProduct_ProductID(userId, productId);

        if (existed) {
            throw new CustomException(ErrorType.conflict("Product already in wishlist"));
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .product(product)
                .build();

        wishlistRepository.save(wishlist);

        return ResponseEntity.ok(ApiType.success("Added product to wishlist successfully"));
    }

    public ResponseEntity<AType> removeFromWishlist(Integer userId, Integer productId) {
        if (userId == null) {
            throw new CustomException(ErrorType.notFound("User not found"));
        }

        if (productId == null) {
            throw new CustomException(ErrorType.notFound("Product not found"));
        }

        Wishlist wishlist = wishlistRepository
                .findByUserIdAndProduct_ProductID(userId, productId)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found in wishlist")));

        wishlistRepository.delete(wishlist);

        return ResponseEntity.ok(ApiType.success("Removed product from wishlist successfully"));
    }

    // Search Management
    public ResponseEntity<AType> searchProducts(
            String query, Integer categoryID, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice,
            Integer colorID, Integer sizeID, String material, String sortBy, Integer page, Integer size) {

        if (page == null || size == null || page < 0 || size <= 0) {
            throw new CustomException(ErrorType.badRequest("Invalid page size"));
        }

        // Build sorting
        org.springframework.data.domain.Sort sort;
        if ("price_asc".equalsIgnoreCase(sortBy)) {
            sort = org.springframework.data.domain.Sort.by("basePrice").ascending();
        } else if ("price_desc".equalsIgnoreCase(sortBy)) {
            sort = org.springframework.data.domain.Sort.by("basePrice").descending();
        } else if ("rating_desc".equalsIgnoreCase(sortBy)) {
            sort = org.springframework.data.domain.Sort.by("averageRating").descending();
        } else {
            sort = org.springframework.data.domain.Sort.by("createdAt").descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // Build dynamic specification
        org.springframework.data.jpa.domain.Specification<Product> spec = org.springframework.data.jpa.domain.Specification.where(
                com.furniro.ProductService.database.specification.ProductSpecs.isActive()
        );

        if (query != null && !query.trim().isEmpty()) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasKeyword(query));
        }
        if (categoryID != null) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasCategory(categoryID));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasPriceBetween(minPrice, maxPrice));
        }
        if (colorID != null) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasColor(colorID));
        }
        if (sizeID != null) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasSize(sizeID));
        }
        if (material != null && !material.trim().isEmpty()) {
            spec = spec.and(com.furniro.ProductService.database.specification.ProductSpecs.hasMaterial(material));
        }

        Page<ProductListRes> products = productRepository.findAll(spec, pageable)
                .map(productMapper::toListRes);

        return ResponseEntity.ok(ApiType.success(products));
    }

    @Transactional
    @CacheEvict(value = "product:detail", key = "#id")
    public ResponseEntity<AType> updateProduct(Integer id, ProductUpdateReq req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Product not found")));

        if (req.getName() != null) product.setName(req.getName());
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if (req.getBasePrice() != null) product.setBasePrice(req.getBasePrice());
        if (req.getBrand() != null) product.setBrand(req.getBrand());
        if (req.getStatus() != null) {
            try {
                product.setStatus(ProductStatus.valueOf(req.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorType.badRequest("Invalid product status"));
            }
        }

        ProductSpecification spec = product.getSpecification();
        if (spec == null) {
            spec = new ProductSpecification();
            spec.setProduct(product);
            product.setSpecification(spec);
        }
        if (req.getWidth() != null) spec.setWidth(req.getWidth());
        if (req.getHeight() != null) spec.setHeight(req.getHeight());
        if (req.getDepth() != null) spec.setDepth(req.getDepth());
        if (req.getWeight() != null) spec.setWeight(req.getWeight());
        if (req.getMaterial() != null) spec.setMaterial(req.getMaterial());
        if (req.getConfiguration() != null) spec.setConfiguration(req.getConfiguration());

        Warranty warranty = product.getWarranty();
        if (warranty == null) {
            warranty = new Warranty();
            warranty.setProduct(product);
            product.setWarranty(warranty);
        }
        if (req.getWarrantyType() != null) warranty.setType(req.getWarrantyType());
        if (req.getWarrantyDuration() != null) warranty.setDuration(req.getWarrantyDuration());
        if (req.getWarrantySummary() != null) warranty.setSummary(req.getWarrantySummary());

        productRepository.save(product);

        ProductDetailRes productDetailRes = productMapper.toDetailRes(product);
        return ResponseEntity.ok(ApiType.success(productDetailRes));
    }

    @Transactional
    public ResponseEntity<AType> createProduct(ProductCreateReq req) {
        if (req.getName() == null || req.getBasePrice() == null || req.getCategoryID() == null) {
            throw new CustomException(ErrorType.badRequest("Missing required fields (name, basePrice, categoryID)"));
        }

        Category category = categoryRepository.findById(req.getCategoryID())
                .orElseThrow(() -> new CustomException(ErrorType.notFound("Category not found")));

        Product product = new Product();
        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setBasePrice(req.getBasePrice());
        product.setBrand(req.getBrand());
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        // Specifications
        ProductSpecification spec = new ProductSpecification();
        spec.setProduct(product);
        spec.setWidth(req.getWidth() != null ? req.getWidth() : 0);
        spec.setHeight(req.getHeight() != null ? req.getHeight() : 0);
        spec.setDepth(req.getDepth() != null ? req.getDepth() : 0);
        spec.setWeight(req.getWeight() != null ? req.getWeight() : 0);
        spec.setMaterial(req.getMaterial() != null ? req.getMaterial() : "N/A");
        spec.setConfiguration(req.getConfiguration() != null ? req.getConfiguration() : "N/A");
        product.setSpecification(spec);

        // Warranty
        Warranty warranty = new Warranty();
        warranty.setProduct(product);
        warranty.setType(req.getWarrantyType() != null ? req.getWarrantyType() : "N/A");
        warranty.setDuration(req.getWarrantyDuration() != null ? req.getWarrantyDuration() : "N/A");
        warranty.setSummary(req.getWarrantySummary() != null ? req.getWarrantySummary() : "N/A");
        product.setWarranty(warranty);

        productRepository.save(product);

        ProductDetailRes productDetailRes = productMapper.toDetailRes(product);
        return ResponseEntity.ok(ApiType.success(productDetailRes));
    }

    @Transactional
    public ResponseEntity<AType> importProductsFromCsv(org.springframework.web.multipart.MultipartFile file) {
        if (file.isEmpty()) {
            throw new CustomException(ErrorType.badRequest("File cannot be empty"));
        }

        int successCount = 0;
        int failCount = 0;
        List<String> errors = new java.util.ArrayList<>();

        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(file.getInputStream(), java.nio.charset.StandardCharsets.UTF_8))) {

            String line = reader.readLine(); // Header

            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if (line.trim().isEmpty()) continue;

                String[] cols = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                if (cols.length < 5) {
                    failCount++;
                    errors.add("Line " + lineNum + ": Missing required columns (Name,Description,BasePrice,Brand,CategoryID)");
                    continue;
                }

                try {
                    String name = cols[0].trim().replace("\"", "");
                    String description = cols[1].trim().replace("\"", "");
                    java.math.BigDecimal basePrice = new java.math.BigDecimal(cols[2].trim());
                    String brand = cols[3].trim().replace("\"", "");
                    Integer categoryId = Integer.valueOf(cols[4].trim());

                    Category category = categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + categoryId));

                    Product product = new Product();
                    product.setName(name);
                    product.setDescription(description);
                    product.setBasePrice(basePrice);
                    product.setBrand(brand);
                    product.setCategory(category);
                    product.setStatus(ProductStatus.ACTIVE);

                    // Specifications
                    ProductSpecification spec = new ProductSpecification();
                    spec.setProduct(product);
                    spec.setWidth(cols.length > 5 && !cols[5].trim().isEmpty() ? Integer.valueOf(cols[5].trim()) : 0);
                    spec.setHeight(cols.length > 6 && !cols[6].trim().isEmpty() ? Integer.valueOf(cols[6].trim()) : 0);
                    spec.setDepth(cols.length > 7 && !cols[7].trim().isEmpty() ? Integer.valueOf(cols[7].trim()) : 0);
                    spec.setWeight(cols.length > 8 && !cols[8].trim().isEmpty() ? Integer.valueOf(cols[8].trim()) : 0);
                    spec.setMaterial(cols.length > 9 && !cols[9].trim().isEmpty() ? cols[9].trim().replace("\"", "") : "N/A");
                    spec.setConfiguration(cols.length > 10 && !cols[10].trim().isEmpty() ? cols[10].trim().replace("\"", "") : "N/A");
                    product.setSpecification(spec);

                    // Warranty
                    Warranty warranty = new Warranty();
                    warranty.setProduct(product);
                    warranty.setType(cols.length > 11 && !cols[11].trim().isEmpty() ? cols[11].trim().replace("\"", "") : "N/A");
                    warranty.setDuration(cols.length > 12 && !cols[12].trim().isEmpty() ? cols[12].trim().replace("\"", "") : "N/A");
                    warranty.setSummary(cols.length > 13 && !cols[13].trim().isEmpty() ? cols[13].trim().replace("\"", "") : "N/A");
                    product.setWarranty(warranty);

                    productRepository.save(product);
                    successCount++;
                } catch (Exception ex) {
                    failCount++;
                    errors.add("Line " + lineNum + ": " + ex.getMessage());
                }
            }

            java.util.Map<String, Object> result = java.util.Map.of(
                    "successCount", successCount,
                    "failCount", failCount,
                    "errors", errors
            );
            return ResponseEntity.ok(ApiType.success(result));
        } catch (Exception e) {
            throw new CustomException(ErrorType.badRequest("Failed to parse CSV upload: " + e.getMessage()));
        }
    }
}