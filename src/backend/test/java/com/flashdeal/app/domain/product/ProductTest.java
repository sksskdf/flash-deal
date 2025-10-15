package com.flashdeal.app.domain.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Product Entity 테스트")
class ProductTest {

    @Test
    @DisplayName("유효한 값으로 Product를 생성할 수 있다")
    void createProductWithValidValues() {
        // given
        ProductId productId = ProductId.generate();
        String title = "AirPods Pro";
        String description = "Active Noise Cancellation";
        Price price = new Price(
            new BigDecimal("329000"),
            new BigDecimal("249000"),
            "KRW"
        );
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        Schedule schedule = new Schedule(
            startsAt,
            startsAt.plusHours(24),
            "Asia/Seoul"
        );
        Map<String, Object> specsFields = new HashMap<>();
        specsFields.put("battery", "30h");
        Specs specs = new Specs(specsFields);
        
        // when
        Product product = new Product(productId, title, description, price, schedule, specs);
        
        // then
        assertNotNull(product);
        assertEquals(productId, product.getProductId());
        assertEquals(title, product.getTitle());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertEquals(schedule, product.getSchedule());
        assertEquals(specs, product.getSpecs());
        assertEquals(DealStatus.UPCOMING, product.getStatus());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        Price price = new Price(new BigDecimal("100"), new BigDecimal("80"), "KRW");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        Schedule schedule = new Schedule(now, now.plusHours(1), "Asia/Seoul");
        Specs specs = new Specs(new HashMap<>());
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Product(null, "title", "desc", price, schedule, specs));
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), null, "desc", price, schedule, specs));
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), "title", "desc", null, schedule, specs));
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), "title", "desc", price, null, specs));
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), "title", "desc", price, schedule, null));
    }

    @Test
    @DisplayName("제목이 빈 문자열이면 예외가 발생한다")
    void throwsExceptionWhenTitleIsEmpty() {
        // given
        Price price = new Price(new BigDecimal("100"), new BigDecimal("80"), "KRW");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        Schedule schedule = new Schedule(now, now.plusHours(1), "Asia/Seoul");
        Specs specs = new Specs(new HashMap<>());
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), "", "desc", price, schedule, specs));
        assertThrows(IllegalArgumentException.class,
            () -> new Product(ProductId.generate(), "   ", "desc", price, schedule, specs));
    }

    @Test
    @DisplayName("현재 시각이 시작 전이면 UPCOMING 상태를 반환한다")
    void statusIsUpcomingWhenBeforeStart() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DealStatus status = product.calculateStatus(now);
        
        // then
        assertEquals(DealStatus.UPCOMING, status);
    }

    @Test
    @DisplayName("현재 시각이 진행 중이면 ACTIVE 상태를 반환한다")
    void statusIsActiveWhenInProgress() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DealStatus status = product.calculateStatus(now);
        
        // then
        assertEquals(DealStatus.ACTIVE, status);
    }

    @Test
    @DisplayName("현재 시각이 종료 후면 ENDED 상태를 반환한다")
    void statusIsEndedWhenAfterEnd() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(25);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        
        // when
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DealStatus status = product.calculateStatus(now);
        
        // then
        assertEquals(DealStatus.ENDED, status);
    }

    @Test
    @DisplayName("딜 상태를 UPCOMING에서 ACTIVE로 전이할 수 있다")
    void transitionFromUpcomingToActive() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        assertEquals(DealStatus.UPCOMING, product.getStatus());
        
        // when
        product.transitionTo(DealStatus.ACTIVE);
        
        // then
        assertEquals(DealStatus.ACTIVE, product.getStatus());
    }

    @Test
    @DisplayName("딜 상태를 ACTIVE에서 SOLDOUT으로 전이할 수 있다")
    void transitionFromActiveToSoldout() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        product.transitionTo(DealStatus.ACTIVE);
        
        // when
        product.transitionTo(DealStatus.SOLDOUT);
        
        // then
        assertEquals(DealStatus.SOLDOUT, product.getStatus());
    }

    @Test
    @DisplayName("딜 상태를 ACTIVE에서 ENDED로 전이할 수 있다")
    void transitionFromActiveToEnded() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).minusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        product.transitionTo(DealStatus.ACTIVE);
        
        // when
        product.transitionTo(DealStatus.ENDED);
        
        // then
        assertEquals(DealStatus.ENDED, product.getStatus());
    }

    @Test
    @DisplayName("불가능한 상태 전이를 시도하면 예외가 발생한다")
    void throwsExceptionWhenInvalidTransition() {
        // given
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        Schedule schedule = new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul");
        Product product = createProduct(schedule);
        
        // when & then
        assertThrows(IllegalStateException.class,
            () -> product.transitionTo(DealStatus.SOLDOUT));
        assertThrows(IllegalStateException.class,
            () -> product.transitionTo(DealStatus.ENDED));
    }

    @Test
    @DisplayName("가격을 변경할 수 있다")
    void canUpdatePrice() {
        // given
        Product product = createProduct();
        Price newPrice = new Price(
            new BigDecimal("350000"),
            new BigDecimal("280000"),
            "KRW"
        );
        
        // when
        product.updatePrice(newPrice);
        
        // then
        assertEquals(newPrice, product.getPrice());
    }

    @Test
    @DisplayName("일정을 변경할 수 있다")
    void canUpdateSchedule() {
        // given
        Product product = createProduct();
        ZonedDateTime newStartsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(2);
        Schedule newSchedule = new Schedule(
            newStartsAt,
            newStartsAt.plusHours(48),
            "Asia/Seoul"
        );
        
        // when
        product.updateSchedule(newSchedule);
        
        // then
        assertEquals(newSchedule, product.getSchedule());
    }

    private Product createProduct() {
        ZonedDateTime startsAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).plusHours(1);
        return createProduct(new Schedule(startsAt, startsAt.plusHours(24), "Asia/Seoul"));
    }

    private Product createProduct(Schedule schedule) {
        return new Product(
            ProductId.generate(),
            "Test Product",
            "Test Description",
            new Price(new BigDecimal("100000"), new BigDecimal("80000"), "KRW"),
            schedule,
            new Specs(new HashMap<>())
        );
    }
}

