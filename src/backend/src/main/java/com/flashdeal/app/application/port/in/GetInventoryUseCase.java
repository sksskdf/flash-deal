package com.flashdeal.app.application.port.in;

import com.flashdeal.app.domain.inventory.Inventory;
import com.flashdeal.app.domain.inventory.InventoryId;
import com.flashdeal.app.domain.product.ProductId;
import reactor.core.publisher.Mono;

public interface GetInventoryUseCase {
    
    Mono<Inventory> getInventory(InventoryId inventoryId);
    
    Mono<Inventory> getInventoryByProductId(ProductId productId);
}
