package ecom.application.service;

import ecom.application.model.Category;
import ecom.application.model.Product;
import ecom.application.payload.ProductRequest;
import ecom.application.payload.ProductResponse;
import ecom.application.repositories.CategoryRepository;
import ecom.application.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class ProductServiceImplTest {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testAddProduct() {
        Category category = new Category();
        category.setCategoryName("Electronics");
        category = categoryRepository.save(category);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Laptop");
        productRequest.setPrice(1000.0);
        productRequest.setDiscount(10.0);
        productRequest.setQuantity(5);

        ProductRequest savedProduct = productService.addProduct(category.getCategoryId(), productRequest);

        assertNotNull(savedProduct);
        assertEquals("Laptop", savedProduct.getProductName());
        assertEquals(900.0, savedProduct.getSpecialPrice());
    }

    @Test
    public void testGetAllProducts() {
        Category category = new Category();
        category.setCategoryName("Books");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Book A");
        product.setPrice(50.0);
        product.setDiscount(5.0);
        product.setQuantity(10);
        product.setCategory(category);
        productRepository.save(product);

        ProductResponse response = productService.getAllProducts(0, 10, "productName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Book A", response.getContent().get(0).getProductName());
    }

    @Test
    public void testSearchByCategory() {
        Category category = new Category();
        category.setCategoryName("Furniture");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Chair");
        product.setPrice(100.0);
        product.setDiscount(10.0);
        product.setQuantity(15);
        product.setCategory(category);
        productRepository.save(product);

        ProductResponse response = productService.searchByCategory(category.getCategoryId(), 0, 10, "price", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Chair", response.getContent().get(0).getProductName());
    }

    @Test
    public void testSearchProductByKeyword() {
        Category category = new Category();
        category.setCategoryName("Appliances");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Blender");
        product.setPrice(200.0);
        product.setDiscount(5.0);
        product.setQuantity(8);
        product.setCategory(category);
        productRepository.save(product);

        ProductResponse response = productService.searchProductByKeyword("Blender", 0, 10, "productName", "asc");

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("Blender", response.getContent().get(0).getProductName());
    }

    @Test
    public void testUpdateProduct() {
        Category category = new Category();
        category.setCategoryName("Gadgets");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Smartphone");
        product.setPrice(500.0);
        product.setDiscount(10.0);
        product.setQuantity(20);
        product.setCategory(category);
        product = productRepository.save(product);

        ProductRequest productRequest = new ProductRequest();
        productRequest.setProductName("Smartphone Pro");
        productRequest.setPrice(700.0);
        productRequest.setDiscount(15.0);
        productRequest.setQuantity(25);

        ProductRequest updatedProduct = productService.updateProduct(product.getProductId(), productRequest);

        assertNotNull(updatedProduct);
        assertEquals("Smartphone Pro", updatedProduct.getProductName());
        assertEquals(700.0, updatedProduct.getPrice());
    }

    @Test
    public void testDeleteProduct() {
        Category category = new Category();
        category.setCategoryName("Toys");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("Toy Car");
        product.setPrice(30.0);
        product.setDiscount(5.0);
        product.setQuantity(50);
        product.setCategory(category);
        product = productRepository.save(product);

        ProductRequest deletedProduct = productService.deleteProduct(product.getProductId());

        assertNotNull(deletedProduct);
        assertEquals("Toy Car", deletedProduct.getProductName());
        assertTrue(productRepository.findById(product.getProductId()).isEmpty());
    }

    @Test
    public void testUpdateImage() throws IOException {
        Category category = new Category();
        category.setCategoryName("Cameras");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setProductName("DSLR Camera");
        product.setPrice(1000.0);
        product.setDiscount(10.0);
        product.setQuantity(5);
        product.setCategory(category);
        product = productRepository.save(product);

        MultipartFile imageFile = new MockMultipartFile(
                "image",
                "camera.jpg",
                "image/jpeg",
                "FakeImageContent".getBytes()
        );

        ProductRequest updatedProduct = productService.updateImage(product.getProductId(), imageFile);

        assertNotNull(updatedProduct);
        assertEquals("camera.jpg", updatedProduct.getImage());
    }
}


