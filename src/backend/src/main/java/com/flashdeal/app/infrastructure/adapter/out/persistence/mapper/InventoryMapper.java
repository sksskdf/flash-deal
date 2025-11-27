package com.flashdeal.app.infrastructure.adapter.out.persistence.mapper;

import java.time.Instant;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.inventory.Policy;
import com.flashdeal.app.domain.inventory.Quantity;
import com.flashdeal.app.domain.inventory.Stock;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.InventoryDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PolicyDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.RedisInfoDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.RestockPolicyDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.StockDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ThresholdsDocument;

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
                inventory.inventoryId().value(),
                inventory.productId().value(),
                toStockDocument(inventory.stock()),
                calculateLevel(inventory.stock()),
                toRedisInfoDocument(inventory),
                toPolicyDocument(inventory.policy()),
                toThresholdsDocument(),
                new ArrayList<>(),
                new ArrayList<>(),
                Instant.now(),
                Instant.now());
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
                stock.total().value(),
                stock.reserved().value(),
                stock.available().value(),
                stock.sold().value());
    }

    private Stock toStock(StockDocument document) {
        if (document == null) {
            return new Stock(new Quantity(0), new Quantity(0), new Quantity(0), new Quantity(0));
        }
        return new Stock(
                new Quantity(document.getTotal()),
                new Quantity(document.getReserved()),
                new Quantity(document.getAvailable()),
                new Quantity(document.getSold()));
    }

    private String calculateLevel(Stock stock) {
        if (stock.total().value() == 0) {
            return "LOW";
        }
        double ratio = (double) stock.available().value() / stock.total().value();
        if (ratio >= 0.5) {
            return "HIGH";
        }
        if (ratio >= 0.2) {
            return "MID";
        }
        return "LOW";
    }

    private RedisInfoDocument toRedisInfoDocument(Inventory inventory) {
        return new RedisInfoDocument(
                "inventory:" + inventory.productId().value(),
                inventory.stock().available().value(),
                Instant.now(),
                1L);
    }

    private PolicyDocument toPolicyDocument(Policy policy) {
        if (policy == null) {
            return null;
        }
        return new PolicyDocument(
                policy.safetyStock(),
                new RestockPolicyDocument(false, 0, 0, null),
                policy.reservationTimeout(),
                policy.maxPurchasePerUser());
    }

    private Policy toPolicy(PolicyDocument document) {
        if (document == null) {
            return new Policy(0, 0, 0);
        }
        return new Policy(
                document.getSafetyStock(),
                document.getReservationTimeout(),
                document.getMaxPurchasePerUser());
    }

    private ThresholdsDocument toThresholdsDocument() {
        return new ThresholdsDocument(0.5, 0.2, 0.0);
    }
}