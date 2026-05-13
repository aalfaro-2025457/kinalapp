package com.angelalfaro.kinalapp.service.product;

import com.angelalfaro.kinalapp.entity.Product;
import com.angelalfaro.kinalapp.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService{

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> listAllByState(int state) {
        return productRepository.findByStateProduct(state, Pageable.unpaged()).toList();
    }

    @Override
    @Transactional
    public Product saveProduct(Product product) {

        validateProduct(product);
        if (product.getStateProduct() == 0) 
            product.setStateProduct(1);
        

        return productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByCodeProduct(Long codeProduct) {
        return productRepository.findById(codeProduct);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(Long codeProduct, Product product) {

        if (!productRepository.existsById(codeProduct)) {
            throw new RuntimeException("el producto no fue encontrado");
        }

        //This instance will used to avoid have empty attributes
        //if you don't send new attributes
        Optional<Product> p = productRepository.findById(codeProduct);

        product.setCodeProduct(codeProduct);

        //Set the last price if you don't send a new price
        product.setPriceProduct(product.getPriceProduct() == null ||
            product.getPriceProduct().signum() <= 0
            ? product.getPriceProduct() : p.get().getPriceProduct() );

        //Set the last stock if you don't send a new stock
        product.setStockProduct(product.getStockProduct() <= 0
            ? product.getStockProduct() : p.get().getStockProduct() );

        //Set the last state if you don't send a new state
        product.setStateProduct(product.getStateProduct() <= 0
            ? product.getStateProduct() : p.get().getStateProduct() );

        validateProduct(product);

        return null;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long codeProduct) {
        
        if (!productRepository.existsById(codeProduct)) {
            throw new RuntimeException("El producto no fue encontrado");
        }

        productRepository.deleteById(codeProduct);

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeProduct(Long codeProduct) {
        return productRepository.existsById(codeProduct);
    }

    public void validateProduct(Product product){

        if (product.getNameProduct() == null || product.getNameProduct().trim().isEmpty()){
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (product.getPriceProduct() == null || product.getPriceProduct().signum() <= 0){
            throw new IllegalArgumentException("El precio es obligatorio, tiene que ser mayor a 0");
        }
        if (product.getStockProduct() < 0){
            throw new IllegalArgumentException("El stock es obligatorio, tiene que ser igual o mayor a 0");
        }

    }
}