package com.furniro.ProductService.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.furniro.ProductService.database.entity.Product;
import com.furniro.ProductService.database.entity.ProductImage;
import com.furniro.ProductService.database.entity.ProductVariant;
import com.furniro.ProductService.dto.res.ProductDetailRes;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.categoryName", target = "categoryName")
    // Ánh xạ từ ProductSpecification (1:1)
    @Mapping(source = "specification.width", target = "width")
    @Mapping(source = "specification.height", target = "height")
    @Mapping(source = "specification.depth", target = "depth")
    @Mapping(source = "specification.weight", target = "weight")
    @Mapping(source = "specification.material", target = "material")
    @Mapping(source = "specification.configuration", target = "configuration")

    // Ánh xạ từ Warranty (1:1)
    @Mapping(source = "warranty.type", target = "warrantyType")
    @Mapping(source = "warranty.duration", target = "warrantyDuration")
    @Mapping(source = "warranty.summary", target = "warrantySummary")

    // Các trường danh sách cần xử lý riêng
    @Mapping(source = "images", target = "images", qualifiedByName = "mapImages")

    @Mapping(source = "variants", target = "sizes", qualifiedByName = "mapSizes")
    @Mapping(source = "variants", target = "colors", qualifiedByName = "mapColors")
    @Mapping(source = "variants", target = "skus", qualifiedByName = "mapSkus")
    @Mapping(source = "variants", target = "productVariantID", qualifiedByName = "mapProductVariantID")



    ProductDetailRes toDetailRes(Product product);

    @Named("mapImages")
    default List<String> mapImages(List<ProductImage> images) {
        if (images == null)
            return null;
        return images.stream().map(ProductImage::getUrl).toList();
    }

    @Named("mapSizes")
    default List<String> mapSizes(List<ProductVariant> variants) {
        if (variants == null)
            return null;
        return variants.stream()
                .map(v -> v.getSize().getSizeName())
                .distinct()
                .toList();
    }

    @Named("mapColors")
    default List<String> mapColors(List<ProductVariant> variants) {
        if (variants == null)
            return null;
        return variants.stream()
                .map(v -> v.getColor().getColorName())
                .distinct()
                .toList();
    }

    @Named("mapSkus")
    default List<String> mapSkus(List<ProductVariant> variants) {
        if (variants == null)
            return null;
        return variants.stream()
                .map(ProductVariant::getSku)
                .distinct()
                .toList();
    }

    @Named("mapProductVariantID")
    default Integer mapProductVariantID(List<ProductVariant> variants) {
        if (variants == null)
            return null;
        return variants.stream()
                .findFirst()
                .map(ProductVariant::getVariantID)
                .orElse(null);
    }
}