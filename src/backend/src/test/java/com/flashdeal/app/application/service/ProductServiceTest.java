package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.in.CreateProductUseCase.CreateProductCommand;
import com.flashdeal.app.application.port.in.UpdateProductUseCase.UpdateProductCommand;
import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    Product baseProduct;

    @BeforeEach
    void setUp() {
        ProductId productId = new ProductId("P-1");
        Price price = new Price(new BigDecimal("10000"), new BigDecimal("9000"), "KRW");
        Schedule schedule = new Schedule(ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), "Asia/Seoul");
        Specs specs = new Specs(Map.of("imageUrl", "http://img", "category", "General"));
        baseProduct = new Product(productId, "타이틀", "설명", price, schedule, specs);
    }

    @Test
    void createProduct_success() {
        CreateProductCommand cmd = new CreateProductCommand(
            "타이틀",
            "설명",
            new BigDecimal("10000"),
            new BigDecimal("9000"),
            "KRW",
            ZonedDateTime.now(),
            ZonedDateTime.now().plusDays(1),
            "General",
            "http://img"
        );
        given(productRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(productService.createProduct(cmd))
            .assertNext(p -> assertThat(p.getTitle()).isEqualTo("타이틀"))
            .verifyComplete();
    }

    @Test
    void getProduct_found() {
        given(productRepository.findById(baseProduct.getProductId())).willReturn(Mono.just(baseProduct));
        StepVerifier.create(productService.getProduct(baseProduct.getProductId()))
            .expectNext(baseProduct)
            .verifyComplete();
    }

    @Test
    void queries_delegateToRepository() {
        given(productRepository.findByStatus(DealStatus.ACTIVE)).willReturn(Flux.just(baseProduct));
        given(productRepository.findByCategory("General")).willReturn(Flux.just(baseProduct));
        given(productRepository.findActiveProducts()).willReturn(Flux.just(baseProduct));

        StepVerifier.create(productService.getProductsByStatus(DealStatus.ACTIVE)).expectNextCount(1).verifyComplete();
        StepVerifier.create(productService.getProductsByCategory("General")).expectNextCount(1).verifyComplete();
        StepVerifier.create(productService.getActiveProducts()).expectNextCount(1).verifyComplete();
    }

    @Test
    void updateProduct_updatesTitlePriceSchedule() {
        given(productRepository.findById(baseProduct.getProductId())).willReturn(Mono.just(baseProduct));
        given(productRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        UpdateProductCommand cmd = new UpdateProductCommand(
            baseProduct.getProductId(),
            "새 타이틀",
            null,
            new BigDecimal("12000"), new BigDecimal("10000"),
            ZonedDateTime.now(), ZonedDateTime.now().plusDays(2)
        );

        StepVerifier.create(productService.updateProduct(cmd))
            .assertNext(p -> assertThat(p.getTitle()).isEqualTo("새 타이틀"))
            .verifyComplete();
    }
}


