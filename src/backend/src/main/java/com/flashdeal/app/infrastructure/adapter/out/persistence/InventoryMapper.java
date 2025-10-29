package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Inventory Domain ↔ Document Mapper
 */
@Component
public class InventoryMapper {

    /**
     * Domain Inventory → Document 변환
     */
    public InventoryDocument toDocument(Inventory inventory) {
        return new InventoryDocument(
            inventory.getInventoryId().getValue(),
            inventory.getProductId().getValue(),
            toStockDocument(inventory.getStock()),
            calculateLevel(inventory.getStock()),
            toRedisInfoDocument(inventory),
            toPolicyDocument(inventory.getPolicy()),
            toThresholdsDocument(),
            new ArrayList<>(), // events는 별도 처리
            new ArrayList<>(), // adjustments는 별도 처리
                Instant.now(), // createdAt
                Instant.now() // updatedAt
        );
    }

    /**
     * Document → Domain Inventory 변환
     */
    public Inventory toDomain(InventoryDocument document) {
        InventoryId inventoryId = new InventoryId(document.getId());
        ProductId productId = new ProductId(document.getProductId());
        Stock stock = toStock(document.getStock());
        Policy policy = toPolicy(document.getPolicy());

        return new Inventory(inventoryId, productId, stock, policy);
    }

    private StockDocument toStockDocument(Stock stock) {
        return new StockDocument(
            stock.getTotal(),
            stock.getReserved(),
            stock.getAvailable(),
            stock.getSold()
        );
    }

    private Stock toStock(StockDocument document) {
        return new Stock(
            document.getTotal(),
            document.getReserved(),
            document.getAvailable(),
            document.getSold()
        );
    }

    private String calculateLevel(Stock stock) {
        double ratio = (double) stock.getAvailable() / stock.getTotal();
        if (ratio >= 0.5) return "HIGH";
        if (ratio >= 0.2) return "MID";
        return "LOW";
    }

    private RedisInfoDocument toRedisInfoDocument(Inventory inventory) {
        return new RedisInfoDocument(
            "inventory:" + inventory.getProductId().getValue(),
            inventory.getStock().getAvailable(),
                Instant.now(),
                    1
        );
    }

    private PolicyDocument toPolicyDocument(Policy policy) {
        return new PolicyDocument(
            policy.getSafetyStock(),
            new RestockPolicyDocument(true, 10, 100, "Supplier"),
            policy.getReservationTimeout(),
            policy.getMaxPurchasePerUser()
        );
    }

    private Policy toPolicy(PolicyDocument document) {
        return new Policy(
            document.getSafetyStock(),
            document.getReservationTimeout(),
            document.getMaxPurchasePerUser()
        );
    }

    private ThresholdsDocument toThresholdsDocument() {
        return new ThresholdsDocument(0.5, 0.2, 0.0);
    }
}





