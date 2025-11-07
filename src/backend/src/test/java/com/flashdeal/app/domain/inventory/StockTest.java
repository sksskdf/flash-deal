package com.flashdeal.app.domain.inventory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Stock Value Object 테스트")
class StockTest {

    @Test
    @DisplayName("유효한 재고로 Stock을 생성할 수 있다")
    void createStockWithValidValues() {
        // when
        Stock stock = new Stock(100, 20, 60, 20);
        
        // then
        assertNotNull(stock);
        assertEquals(100, stock.total());
        assertEquals(20, stock.reserved());
        assertEquals(60, stock.available());
        assertEquals(20, stock.sold());
    }

    @Test
    @DisplayName("초기 재고를 생성할 수 있다")
    void createInitialStock() {
        // when
        Stock stock = Stock.initial(100);
        
        // then
        assertEquals(100, stock.total());
        assertEquals(0, stock.reserved());
        assertEquals(100, stock.available());
        assertEquals(0, stock.sold());
    }

    @Test
    @DisplayName("불변식을 위반하면 예외가 발생한다 - total != reserved + available + sold")
    void throwsExceptionWhenInvariantViolated() {
        // when & then - total이 100인데 합이 110
        assertThrows(IllegalArgumentException.class, 
            () -> new Stock(100, 20, 70, 20));
    }

    @Test
    @DisplayName("음수 값이 있으면 예외가 발생한다")
    void throwsExceptionWhenNegativeValues() {
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> new Stock(-100, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, 
            () -> new Stock(100, -10, 60, 50));
        assertThrows(IllegalArgumentException.class, 
            () -> new Stock(100, 10, -60, 50));
        assertThrows(IllegalArgumentException.class, 
            () -> new Stock(100, 10, 60, -50));
    }

    @Test
    @DisplayName("재고를 감소시킬 수 있다 - available 감소, reserved 증가")
    void decreaseStock() {
        // given
        Stock stock = Stock.initial(100);
        
        // when
        Stock decreased = stock.decrease(30);
        
        // then
        assertEquals(100, decreased.total());
        assertEquals(30, decreased.reserved());
        assertEquals(70, decreased.available());
        assertEquals(0, decreased.sold());
    }

    @Test
    @DisplayName("가용 재고보다 많이 감소시키면 예외가 발생한다")
    void throwsExceptionWhenDecreaseMoreThanAvailable() {
        // given
        Stock stock = Stock.initial(100);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> stock.decrease(101));
    }

    @Test
    @DisplayName("음수로 감소시키면 예외가 발생한다")
    void throwsExceptionWhenDecreaseNegative() {
        // given
        Stock stock = Stock.initial(100);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> stock.decrease(-10));
    }

    @Test
    @DisplayName("예약을 확정할 수 있다 - reserved 감소, sold 증가")
    void confirmReservation() {
        // given
        Stock stock = Stock.initial(100).decrease(30);
        
        // when
        Stock confirmed = stock.confirm(20);
        
        // then
        assertEquals(100, confirmed.total());
        assertEquals(10, confirmed.reserved());
        assertEquals(70, confirmed.available());
        assertEquals(20, confirmed.sold());
    }

    @Test
    @DisplayName("예약량보다 많이 확정하면 예외가 발생한다")
    void throwsExceptionWhenConfirmMoreThanReserved() {
        // given
        Stock stock = Stock.initial(100).decrease(30);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> stock.confirm(31));
    }

    @Test
    @DisplayName("예약을 해제할 수 있다 - reserved 감소, available 증가")
    void releaseReservation() {
        // given
        Stock stock = Stock.initial(100).decrease(30);
        
        // when
        Stock released = stock.release(10);
        
        // then
        assertEquals(100, released.total());
        assertEquals(20, released.reserved());
        assertEquals(80, released.available());
        assertEquals(0, released.sold());
    }

    @Test
    @DisplayName("예약량보다 많이 해제하면 예외가 발생한다")
    void throwsExceptionWhenReleaseMoreThanReserved() {
        // given
        Stock stock = Stock.initial(100).decrease(30);
        
        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> stock.release(31));
    }

    @Test
    @DisplayName("재고가 0개이면 품절 상태다")
    void isOutOfStockWhenAvailableIsZero() {
        // given
        Stock stock = Stock.initial(100).decrease(100);
        
        // when & then
        assertTrue(stock.isOutOfStock());
    }

    @Test
    @DisplayName("재고가 1개 이상이면 품절이 아니다")
    void isNotOutOfStockWhenAvailableIsPositive() {
        // given
        Stock stock = Stock.initial(100).decrease(99);
        
        // when & then
        assertFalse(stock.isOutOfStock());
    }

    @Test
    @DisplayName("연속적인 재고 조작을 할 수 있다")
    void canChainStockOperations() {
        // given
        Stock stock = Stock.initial(100);
        
        // when
        Stock result = stock
            .decrease(30)    // available: 70, reserved: 30
            .confirm(20)     // available: 70, reserved: 10, sold: 20
            .release(5)      // available: 75, reserved: 5, sold: 20
            .decrease(25);   // available: 50, reserved: 30, sold: 20
        
        // then
        assertEquals(100, result.total());
        assertEquals(30, result.reserved());
        assertEquals(50, result.available());
        assertEquals(20, result.sold());
    }

    @Test
    @DisplayName("같은 재고 상태는 동일하다")
    void testEquality() {
        // given
        Stock stock1 = new Stock(100, 20, 60, 20);
        Stock stock2 = new Stock(100, 20, 60, 20);
        
        // then
        assertEquals(stock1, stock2);
        assertEquals(stock1.hashCode(), stock2.hashCode());
    }
}

