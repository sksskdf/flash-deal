package com.flashdeal.app.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Stock Value Object 테스트")
class StockTest {

    @Test
    @DisplayName("유효한 재고로 Stock을 생성할 수 있다")
    void createStockWithValidValues() {
        // when
        Stock stock = new Stock(new Quantity(100), new Quantity(20), new Quantity(60), new Quantity(20));

        // then
        assertNotNull(stock);
        assertEquals(100, stock.total().value());
        assertEquals(20, stock.reserved().value());
        assertEquals(60, stock.available().value());
        assertEquals(20, stock.sold().value());
    }

    @Test
    @DisplayName("초기 재고를 생성할 수 있다")
    void createInitialStock() {
        // when
        Stock stock = Stock.initial(new Quantity(100));

        // then
        assertEquals(100, stock.total().value());
        assertEquals(0, stock.reserved().value());
        assertEquals(100, stock.available().value());
        assertEquals(0, stock.sold().value());
    }

    @Test
    @DisplayName("불변식을 위반하면 예외가 발생한다 - total != reserved + available + sold")
    void throwsExceptionWhenInvariantViolated() {
        // when & then - total이 100인데 합이 110
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(new Quantity(100), new Quantity(20), new Quantity(70), new Quantity(20)));
    }

    @Test
    @DisplayName("음수 값이 있으면 예외가 발생한다")
    void throwsExceptionWhenNegativeValues() {
        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(new Quantity(-100), new Quantity(0), new Quantity(0), new Quantity(0)));
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(new Quantity(100), new Quantity(-10), new Quantity(60), new Quantity(50)));
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(new Quantity(100), new Quantity(10), new Quantity(-60), new Quantity(50)));
        assertThrows(IllegalArgumentException.class,
                () -> new Stock(new Quantity(100), new Quantity(10), new Quantity(60), new Quantity(-50)));
    }

    @Test
    @DisplayName("재고를 감소시킬 수 있다 - available 감소, reserved 증가")
    void decreaseStock() {
        // given
        Stock stock = Stock.initial(new Quantity(100));

        // when
        Stock decreased = stock.reserve(new Quantity(30));

        // then
        assertEquals(100, decreased.total().value());
        assertEquals(30, decreased.reserved().value());
        assertEquals(70, decreased.available().value());
        assertEquals(0, decreased.sold().value());
    }

    @Test
    @DisplayName("가용 재고보다 많이 감소시키면 예외가 발생한다")
    void throwsExceptionWhenDecreaseMoreThanAvailable() {
        // given
        Stock stock = Stock.initial(new Quantity(100));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(new Quantity(101)));
    }

    @Test
    @DisplayName("음수로 감소시키면 예외가 발생한다")
    void throwsExceptionWhenDecreaseNegative() {
        // given
        Stock stock = Stock.initial(new Quantity(100));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(new Quantity(-10)));
    }

    @Test
    @DisplayName("예약을 확정할 수 있다 - reserved 감소, sold 증가")
    void confirmReservation() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(30));

        // when
        Stock confirmed = stock.confirm(new Quantity(20));

        // then
        assertEquals(100, confirmed.total().value());
        assertEquals(10, confirmed.reserved().value());
        assertEquals(70, confirmed.available().value());
        assertEquals(20, confirmed.sold().value());
    }

    @Test
    @DisplayName("예약량보다 많이 확정하면 예외가 발생한다")
    void throwsExceptionWhenConfirmMoreThanReserved() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(30));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> stock.confirm(new Quantity(31)));
    }

    @Test
    @DisplayName("예약을 해제할 수 있다 - reserved 감소, available 증가")
    void releaseReservation() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(30));

        // when
        Stock released = stock.release(new Quantity(10));

        // then
        assertEquals(100, released.total().value());
        assertEquals(20, released.reserved().value());
        assertEquals(80, released.available().value());
        assertEquals(0, released.sold().value());
    }

    @Test
    @DisplayName("예약량보다 많이 해제하면 예외가 발생한다")
    void throwsExceptionWhenReleaseMoreThanReserved() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(30));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> stock.release(new Quantity(31)));
    }

    @Test
    @DisplayName("재고가 0개이면 품절 상태다")
    void isOutOfStockWhenAvailableIsZero() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(100));

        // when & then
        assertTrue(stock.outOfStock());
    }

    @Test
    @DisplayName("재고가 1개 이상이면 품절이 아니다")
    void isNotOutOfStockWhenAvailableIsPositive() {
        // given
        Stock stock = Stock.initial(new Quantity(100)).reserve(new Quantity(99));

        // when & then
        assertFalse(stock.outOfStock());
    }

    @Test
    @DisplayName("연속적인 재고 조작을 할 수 있다")
    void canChainStockOperations() {
        // given
        Stock stock = Stock.initial(new Quantity(100));

        // when
        Stock result = stock
                .reserve(new Quantity(30)) // available: 70, reserved: 30
                .confirm(new Quantity(20)) // available: 70, reserved: 10, sold: 20
                .release(new Quantity(5)) // available: 75, reserved: 5, sold: 20
                .reserve(new Quantity(25)); // available: 50, reserved: 30, sold: 20

        // then
        assertEquals(100, result.total().value());
        assertEquals(30, result.reserved().value());
        assertEquals(50, result.available().value());
        assertEquals(20, result.sold().value());
    }

    @Test
    @DisplayName("같은 재고 상태는 동일하다")
    void testEquality() {
        // given
        Stock stock1 = new Stock(new Quantity(100), new Quantity(20), new Quantity(60), new Quantity(20));
        Stock stock2 = new Stock(new Quantity(100), new Quantity(20), new Quantity(60), new Quantity(20));

        // then
        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());
    }
}
