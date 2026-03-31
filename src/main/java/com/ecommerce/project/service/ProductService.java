package com.ecommerce.project.service;

import com.ecommerce.project.dtos.CartDto;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.dtos.ProductDto;
import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.service.interfaces.ICartService;
import com.ecommerce.project.service.interfaces.IFileService;
import com.ecommerce.project.service.interfaces.IProductService;
import com.ecommerce.project.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ICartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IFileService fileService;

    @Autowired
    AuthUtil authUtil;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductNotPresent = true;

        List<Product> products = category.getProducts();
        for (Product product : products) {
            if (product.getName().equals(productDto.getName())) {
                isProductNotPresent = false;
                break;
            }
        }

        if (isProductNotPresent) {
            Product product = modelMapper.map(productDto, Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice();
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct, ProductDto.class);
        } else {
            throw new APIException("Product already exists");
        }
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        Product product = modelMapper.map(productDto, Product.class);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setPrice(product.getPrice());
        double specialPrice = product.getPrice() - (product.getDiscount() * 0.01) * product.getPrice();
        existingProduct.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(existingProduct);
        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDto> cartDTOs = carts.stream().map(cart -> {
            CartDto cartDTO = modelMapper.map(cart, CartDto.class);

            List<ProductDto> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).toList();

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getId(), productId));
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductDto deleteProduct(Long productId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getId(), productId));

        productRepository.delete(existingProduct);
        return modelMapper.map(existingProduct, ProductDto.class);
    }

    @Override
    public ProductDto updateImage(Long productId, MultipartFile image) throws IOException {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        String fileName = fileService.uploadImage(path, image);
        existingProduct.setImage(fileName);
        Product updatedProduct = productRepository.save(existingProduct);
        return modelMapper.map(updatedProduct, ProductDto.class);
    }

    @Override
    public PaginationResponseDto<ProductDto, Product> getAllProductsForAdmin(HttpServletRequest request, Pageable pageable) {
        Page<Product> productPage = productRepository.findAll(pageable);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products created till now");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);
                    productDto.setImage(constructImageUrl(product.getImage()));
                    return productDto;
                })
                .toList();
        return new PaginationResponseDto<>(request, productDtos, productPage);
    }

    @Override
    public PaginationResponseDto<ProductDto, Product> getAllProductsForSeller(HttpServletRequest request, Pageable pageable) {
        User user = authUtil.loggedInUser();
        Page<Product> productPage = productRepository.findByUser(user, pageable);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products created till now");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);
                    productDto.setImage(constructImageUrl(product.getImage()));
                    return productDto;
                }).toList();
        return new PaginationResponseDto<>(request, productDtos, productPage);
    }

    @Override
    public PaginationResponseDto<ProductDto, Product> getAllProducts(HttpServletRequest request, Pageable pageable, String keyword, String category) {
        Specification<Product> spec = Specification.where((Specification<Product>) null);
        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
        }

        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")), "%" + category.toLowerCase() + "%"));
        }
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products created till now");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> {
                    ProductDto productDto = modelMapper.map(product, ProductDto.class);
                    productDto.setImage(constructImageUrl(product.getImage()));
                    return productDto;
                })
                .toList();
        return new PaginationResponseDto<>(request, productDtos, productPage);
    }

    @Override
    public PaginationResponseDto<ProductDto, Product> searchByCategory(HttpServletRequest request, Pageable pageable, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(pageable, category);
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No products created till now");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        return new PaginationResponseDto<>(request, productDtos, productPage);
    }

    @Override
    public PaginationResponseDto<ProductDto, Product> searchByKeyword(HttpServletRequest request, Pageable pageable, String keyword) {
        Page<Product> productPage = productRepository.findByNameLikeIgnoreCase(pageable, '%' + keyword + '%');
        List<Product> products = productPage.getContent();
        if (products.isEmpty()) {
            throw new APIException("No product created till now");
        }
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        return new PaginationResponseDto<>(request, productDtos, productPage);
    }

    private String constructImageUrl(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl + imageName : imageBaseUrl + "/" + imageName;
    }
}
