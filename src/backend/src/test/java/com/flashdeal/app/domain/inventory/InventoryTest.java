package com.flashdeal.app.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.flashdeal.app.domain.product.ProductId;

@DisplayName("Inventory Entity 테스트")
class InventoryTest {

    @Test
    @DisplayName("유효한 값으로 Inventory를 생성할 수 있다")
    void createInventoryWithValidValues() {
        // given
        InventoryId inventoryId = InventoryId.generate();
        ProductId productId = ProductId.generate();
        Stock stock = Stock.initial(new Quantity(100));
        Policy policy = Policy.defaultPolicy();

        // when
        Inventory inventory = new Inventory(inventoryId, productId, stock, policy);

        // then
        assertNotNull(inventory);
        assertEquals(inventoryId, inventory.inventoryId());
        assertEquals(productId, inventory.productId());
        assertEquals(stock, inventory.stock());
        assertEquals(policy, inventory.policy());
    }

    @Test
    @DisplayName("필수 필드가 null이면 예외가 발생한다")
    void throwsExceptionWhenRequiredFieldsAreNull() {
        // given
        ProductId productId = ProductId.generate();
        Stock stock = Stock.initial(new Quantity(100));
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
        Inventory reservedInventory = inventory.reserve(new Quantity(30));

        // then
        assertEquals(30, reservedInventory.stock().reserved().value());
        assertEquals(70, reservedInventory.stock().available().value());
    }

    @Test
    @DisplayName("가용 재고보다 많이 예약하면 예외가 발생한다")
    void throwsExceptionWhenReserveMoreThanAvailable() {
        // given
        Inventory inventory = createInventory(100);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> inventory.reserve(new Quantity(101)));
    }

    @Test
    @DisplayName("예약을 확정할 수 있다")
    void canConfirmReservation() {
        // given
        Inventory inventory = createInventory(100);
        Inventory reservedInventory = inventory.reserve(new Quantity(30));

        // when
        Inventory confirmedInventory = reservedInventory.confirm(new Quantity(20));

        // then
        assertEquals(10, confirmedInventory.stock().reserved().value());
        assertEquals(70, confirmedInventory.stock().available().value());
        assertEquals(20, confirmedInventory.stock().sold().value());
    }

    @Test
    @DisplayName("예약량보다 많이 확정하면 예외가 발생한다")
    void throwsExceptionWhenConfirmMoreThanReserved() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(new Quantity(30));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> inventory.confirm(new Quantity(31)));
    }

    @Test
    @DisplayName("예약을 해제할 수 있다")
    void canReleaseReservation() {
        // given
        Inventory inventory = createInventory(100);
        Inventory reservedInventory = inventory.reserve(new Quantity(30));

        // when
        Inventory releasedInventory = reservedInventory.release(new Quantity(10));

        // then
        assertEquals(20, releasedInventory.stock().reserved().value());
        assertEquals(80, releasedInventory.stock().available().value());
    }

    @Test
    @DisplayName("예약량보다 많이 해제하면 예외가 발생한다")
    void throwsExceptionWhenReleaseMoreThanReserved() {
        // given
        Inventory inventory = createInventory(100);
        inventory.reserve(new Quantity(30));

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> inventory.release(new Quantity(31)));
    }

    @Test
    @DisplayName("재고가 0이면 품절 상태다")
    void isOutOfStockWhenAvailableIsZero() {
        // given
        Inventory inventory = createInventory(100);
        Inventory reservedInventory = inventory.reserve(new Quantity(100));

        // when & then
        assertTrue(reservedInventory.stock().outOfStock());
    }

    @Test
    @DisplayName("재고가 안전 재고량보다 적으면 품절 임박 상태다")
    void isLowStockWhenBelowSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
                InventoryId.generate(),
                ProductId.generate(),
                Stock.initial(new Quantity(100)),
                policy);
        Inventory reservedInventory = inventory.reserve(new Quantity(51)); // available: 49

        // when & then
        assertTrue(reservedInventory.lowStock());
    }

    @Test
    @DisplayName("재고가 안전 재고량 이상이면 품절 임박이 아니다")
    void isNotLowStockWhenAboveSafetyStock() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
                InventoryId.generate(),
                ProductId.generate(),
                Stock.initial(new Quantity(100)),
                policy);
        Inventory reservedInventory = inventory.reserve(new Quantity(50)); // available: 50

        // when & then
        assertFalse(reservedInventory.lowStock());
    }

    @Test
    @DisplayName("구매 가능 수량인지 확인할 수 있다")
    void canValidatePurchaseQuantity() {
        // given
        Policy policy = new Policy(50, 600, 5);
        Inventory inventory = new Inventory(
                InventoryId.generate(),
                ProductId.generate(),
                Stock.initial(new Quantity(100)),
                policy);

        // when & then
        assertTrue(inventory.policy().isValidPurchaseQuantity(new Quantity(5)));
        assertFalse(inventory.policy().isValidPurchaseQuantity(new Quantity(6)));
        assertFalse(inventory.policy().isValidPurchaseQuantity(new Quantity(0)));
    }

    @Test
    @DisplayName("정책을 변경할 수 있다")
    void canUpdatePolicy() {
        // given
        Inventory inventory = createInventory(100);
        Policy newPolicy = new Policy(30, 900, 10);

        // when
        Inventory updatedInventory = inventory.updatePolicy(newPolicy);

        // then
        assertEquals(newPolicy, updatedInventory.policy());
    }

    @Test
    @DisplayName("재고를 증가시킬 수 있다")
    void canIncreaseStock() {
        // given
        Inventory inventory = createInventory(100);

        // when
        Inventory increasedInventory = inventory.increaseStock(new Quantity(50));

        // then
        assertEquals(150, increasedInventory.stock().total().value());
        assertEquals(150, increasedInventory.stock().available().value());
    }

    @Test
    @DisplayName("음수로 재고를 증가시키면 예외가 발생한다")
    void throwsExceptionWhenIncreaseNegative() {
        // given
        Inventory inventory = createInventory(100);

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> inventory.increaseStock(new Quantity(-10)));
    }

    private Inventory createInventory(int totalStock) {
        return new Inventory(
                InventoryId.generate(),
                ProductId.generate(),
                Stock.initial(new Quantity(totalStock)),
                Policy.defaultPolicy());
    }
}
