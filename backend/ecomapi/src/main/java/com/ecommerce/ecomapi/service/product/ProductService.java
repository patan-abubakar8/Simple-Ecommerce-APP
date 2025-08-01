package com.ecommerce.ecomapi.service.product;

import com.ecommerce.ecomapi.entity.Category;
import com.ecommerce.ecomapi.entity.Product;
import com.ecommerce.ecomapi.exceptions.AlreadyExistsException;
import com.ecommerce.ecomapi.exceptions.ResourceNotFoundException;
import com.ecommerce.ecomapi.repository.CategoryRepository;
import com.ecommerce.ecomapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.rmi.AlreadyBoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    public Product addProduct(Product product) {
        if (productRepository.existsByName(product.getName())) {
            throw new AlreadyExistsException("Product with name " + product.getName() + " already exists.");
        }

        // Fetch full category using ID
        Long categoryId = product.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        product.setCategory(category);

        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()->new ResourceAccessException("Product Not Found with Id : "+id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Product product, Long id) {
        Product existedProduct =getProductById(id);
        existedProduct.setName(product.getName());
        existedProduct.setDescription(product.getDescription());
        existedProduct.setPrice(product.getPrice());
        existedProduct.setCategory(product.getCategory());
        existedProduct.setStockQuantity(product.getStockQuantity());
        return productRepository.save(existedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product existedProduct =getProductById(id);
        productRepository.delete(existedProduct);
    }

    @Override
    public Page<Product> getAllProductsPage(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return productRepository.findAll(pageable);
    }

    @Override
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
}
