package com.flashdeal.app.domain.inventory;

import org.junit.jupiter.api.Test;

import com.flashdeal.app.domain.product.ProductId;

import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Inventory Entity 테스트")
class InventoryTest {

    @Test
    @DisplayName("유효한 값으로 Inventory를 생성할 수 있다")
    void createInventoryWithValidValues() {
        // given
        InventoryId inventoryId = InventoryId.generate();
        ProductId productId = ProductId.generate();
        Stock stock = Stock.initial(100);
        Policy policy = Policy.defaultPolicy();
        
        // when
        Inventory inventory = new Inventory(inventoryId, productId, stock, policy);
        
        // then
        assertNotNull(inventory);
        assertEquals(inventoryId, inventory.getInventoryId());
        assertEquals(productId, inventory.getProductId());
        assertEquals(stock, inventory.getStock());
        assertEquals(policy, inventory.getPolicy());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        ProductId productId = ProductId.generate();
        Stock stock = Stock.initial(100);
        Policy policy = Policy.defaultPolicy();
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> new Inventory(null, productId, stock, policy));
        assertThrows(IllegalArgumentException.class,
            () -> new Inventory(InventoryId.generate(), null, stock, policy));
        assertThrows(IllegalArgumentException.class,
            () -> new Inventory(InventoryId.generate(), productId, null, policy));
        assertThrows(IllegalArgumentException.class,
            () -> new Inventory(InventoryId.generate(), productId, stock, null));
    }

    @Test
    @DisplayName("재고를 예약할 수 있다")
    void canReserveStock() {
        // given
        Inventory inventory = createInventory(100);
        
        // when
        inventory.reserve(30);
        
        // then
        assertEquals(30, inventory.getStock().getReserved());
        assertEquals(70, inventory.getStock().getAvailable());
    }

    @Test
    @DisplayName("가용 재고보다 많이 예약하면 예외가 발생한다")
    void throwsExceptionWhenReserveMoreThanAvailable() {
        // given
        Inventory inventory = createInventory(100);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> inventory.reserve(101));
    }

    @Test
    @DisplayName("예약을 확정할 수 있다")
    void canConfirmReservation() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(30);
        
        // when
        inventory.confirm(20);
        
        // then
        assertEquals(10, inventory.getStock().getReserved());
        assertEquals(70, inventory.getStock().getAvailable());
        assertEquals(20, inventory.getStock().getSold());
    }

    @Test
    @DisplayName("예약량보다 많이 확정하면 예외가 발생한다")
    void throwsExceptionWhenConfirmMoreThanReserved() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(30);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> inventory.confirm(31));
    }

    @Test
    @DisplayName("예약을 해제할 수 있다")
    void canReleaseReservation() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(30);
        
        // when
        inventory.release(10);
        
        // then
        assertEquals(20, inventory.getStock().getReserved());
        assertEquals(80, inventory.getStock().getAvailable());
    }

    @Test
    @DisplayName("예약량보다 많이 해제하면 예외가 발생한다")
    void throwsExceptionWhenReleaseMoreThanReserved() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(30);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> inventory.release(31));
    }

    @Test
    @DisplayName("재고가 0이면 품절 상태다")
    void isOutOfStockWhenAvailableIsZero() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(100);
        
        // when & then
        assertTrue(inventory.isOutOfStock());
    }

    @Test
    @DisplayName("재고가 안전 재고량보다 적으면 품절 임박 상태다")
    void isLowStockWhenBelowSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
            InventoryId.generate(),
            ProductId.generate(),
            Stock.initial(100),
            policy
        );
        inventory.reserve(51);  // available: 49
        
        // when & then
        assertTrue(inventory.isLowStock());
    }

    @Test
    @DisplayName("재고가 안전 재고량 이상이면 품절 임박이 아니다")
    void isNotLowStockWhenAboveSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
            InventoryId.generate(),
            ProductId.generate(),
            Stock.initial(100),
            policy
        );
        inventory.reserve(50);  // available: 50
        
        // when & then
        assertFalse(inventory.isLowStock());
    }

    @Test
    @DisplayName("구매 가능 수량인지 확인할 수 있다")
    void canValidatePurchaseQuantity() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
            InventoryId.generate(),
            ProductId.generate(),
            Stock.initial(100),
            policy
        );
        
        // when & then
        assertTrue(inventory.isValidPurchaseQuantity(5));
        assertFalse(inventory.isValidPurchaseQuantity(6));
        assertFalse(inventory.isValidPurchaseQuantity(0));
    }

    @Test
    @DisplayName("정책을 변경할 수 있다")
    void canUpdatePolicy() {
        // given
        Inventory inventory = createInventory(100);
        Policy newPolicy = new Policy(30, 900, 10);
        
        // when
        inventory.updatePolicy(newPolicy);
        
        // then
        assertEquals(newPolicy, inventory.getPolicy());
    }

    @Test
    @DisplayName("재고를 증가시킬 수 있다")
    void canIncreaseStock() {
        // given
        Inventory inventory = createInventory(100);
        
        // when
        inventory.increaseStock(50);
        
        // then
        assertEquals(150, inventory.getStock().getTotal());
        assertEquals(150, inventory.getStock().getAvailable());
    }

    @Test
    @DisplayName("음수로 재고를 증가시키면 예외가 발생한다")
    void throwsExceptionWhenIncreaseNegative() {
        // given
        Inventory inventory = createInventory(100);
        
        // when & then
        assertThrows(IllegalArgumentException.class,
            () -> inventory.increaseStock(-10));
    }

    private Inventory createInventory(int totalStock) {
        return new Inventory(
            InventoryId.generate(),
            ProductId.generate(),
            Stock.initial(totalStock),
            Policy.defaultPolicy()
        );
    }
}

