package com.flashdeal.app.infrastructure.adapter.out.persistence.mapper;

import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.InventoryDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PolicyDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.RedisInfoDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.RestockPolicyDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.StockDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ThresholdsDocument;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;

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
                new ArrayList<>(),
                new ArrayList<>(),
                Instant.now(),
                Instant.now()
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
        if (document == null) {
            return new Stock(0, 0, 0, 0);
        }
        return new Stock(
            document.getTotal(),
            document.getReserved(),
            document.getAvailable(),
            document.getSold()
        );
    }

    private String calculateLevel(Stock stock) {
        if (stock.getTotal() == 0) {
            return "LOW";
        }
        double ratio = (double) stock.getAvailable() / stock.getTotal();
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
                "inventory:" + inventory.getProductId().getValue(),
                inventory.getStock().getAvailable(),
                Instant.now(),
                1L
        );
    }

    private PolicyDocument toPolicyDocument(Policy policy) {
        if (policy == null) {
            return null;
        }
        return new PolicyDocument(
            policy.getSafetyStock(),
            new RestockPolicyDocument(false, 0, 0, null),
            policy.getReservationTimeout(),
            policy.getMaxPurchasePerUser()
        );
    }

    private Policy toPolicy(PolicyDocument document) {
        if (document == null) {
            return new Policy(0, 0, 0);
        }
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