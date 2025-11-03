package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.product.DealStatus;
import com.flashdeal.app.domain.product.Product;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Service
public class ProductStatusUpdateService {

    private final ProductRepository productRepository;

    public ProductStatusUpdateService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void updateProductStatuses() {
        updateUpcomingToActive().subscribe();
        updateActiveToEnded().subscribe();
    }

    private Flux<Product> updateUpcomingToActive() {
        return productRepository.findByStatusAndScheduleStartAtBefore(DealStatus.UPCOMING, Instant.now())
                .flatMap(product -> {
                    product.updateStatus(DealStatus.ACTIVE);
                    return productRepository.save(product);
                });
    }

    private Flux<Product> updateActiveToEnded() {
        return productRepository.findByStatusAndScheduleEndAtBefore(DealStatus.ACTIVE, Instant.now())
                .flatMap(product -> {
                    product.updateStatus(DealStatus.ENDED);
                    return productRepository.save(product);
                });
    }
}
