package com.flashdeal.app.application.service;

import com.flashdeal.app.application.port.out.ProductRepository;
import com.flashdeal.app.domain.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductStatusUpdateService 테스트")
class ProductStatusUpdateServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductStatusUpdateService service;

    Product upcoming;
    Product active;

    @BeforeEach
    void setUp() {
        Price price = new Price(new BigDecimal("10000"), new BigDecimal("9000"), "KRW");
        Schedule schedule = new Schedule(ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1), "Asia/Seoul");
        Specs specs = new Specs(Map.of("imageUrl", "http://img", "category", "General"));

        upcoming = new Product(new ProductId("P-1"), "상품1", "설명", "카테고리", price, schedule, specs, DealStatus.UPCOMING);
        active = new Product(new ProductId("P-2"), "상품2", "설명", "카테고리", price, schedule, specs, DealStatus.ACTIVE);
    }

    @Test
    @DisplayName("상품 상태를 업데이트하면 UPCOMING을 ACTIVE로, ACTIVE를 ENDED로 전이한다")
    void updateProductStatuses_movesUpcomingToActive_andActiveToEnded() {
        given(productRepository.findByStatusAndScheduleStartAtBefore(eq(DealStatus.UPCOMING), any()))
            .willReturn(Flux.just(upcoming));
        given(productRepository.findByStatusAndScheduleEndAtBefore(eq(DealStatus.ACTIVE), any()))
            .willReturn(Flux.just(active));

        given(productRepository.save(any())).willAnswer(inv -> Mono.just(inv.getArgument(0)));

        service.updateProductStatuses();

        // 두 상태 전이에 대해 각각 저장 호출
        verify(productRepository).findByStatusAndScheduleStartAtBefore(eq(DealStatus.UPCOMING), any());
        verify(productRepository).findByStatusAndScheduleEndAtBefore(eq(DealStatus.ACTIVE), any());
        org.mockito.Mockito.verify(productRepository, org.mockito.Mockito.times(2)).save(any());
    }
}
