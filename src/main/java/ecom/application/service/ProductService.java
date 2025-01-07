package ecom.application.service;


import ecom.application.payload.ProductRequest;
import ecom.application.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    ProductRequest addProduct(Long categoryId, ProductRequest product);

    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductRequest updateProduct(Long productId, ProductRequest productRequest);

    ProductRequest deleteProduct(Long productId);

    ProductRequest updateImage(Long productId, MultipartFile image) throws IOException;
}

