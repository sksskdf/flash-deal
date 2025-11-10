package com.flashdeal.app.infrastructure.adapter.in.graphql;

import com.flashdeal.app.application.port.in.*;
import com.flashdeal.app.domain.inventory.*;
import com.flashdeal.app.domain.product.ProductId;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

/**
 * Inventory GraphQL Resolver
 */
@Controller
public class InventoryResolver {

    private final CreateInventoryUseCase createInventoryUseCase;
    private final GetInventoryUseCase getInventoryUseCase;
    private final ReserveInventoryUseCase reserveInventoryUseCase;
    private final ConfirmInventoryUseCase confirmInventoryUseCase;
    private final ReleaseInventoryUseCase releaseInventoryUseCase;

    public InventoryResolver(
            CreateInventoryUseCase createInventoryUseCase,
            GetInventoryUseCase getInventoryUseCase,
            ReserveInventoryUseCase reserveInventoryUseCase,
            ConfirmInventoryUseCase confirmInventoryUseCase,
            ReleaseInventoryUseCase releaseInventoryUseCase) {
        this.createInventoryUseCase = createInventoryUseCase;
        this.getInventoryUseCase = getInventoryUseCase;
        this.reserveInventoryUseCase = reserveInventoryUseCase;
        this.confirmInventoryUseCase = confirmInventoryUseCase;
        this.releaseInventoryUseCase = releaseInventoryUseCase;
    }

    @QueryMapping
    public Mono<Inventory> inventory(@Argument String id) {
        InventoryId inventoryId = new InventoryId(id);
        return getInventoryUseCase.getInventory(inventoryId);
    }

    @QueryMapping
    public Mono<Inventory> inventoryByProduct(@Argument String productId) {
        ProductId id = new ProductId(productId);
        return getInventoryUseCase.getInventoryByProductId(id);
    }

    @MutationMapping
    public Mono<Inventory> createInventory(@Argument CreateInventoryInput input) {
        CreateInventoryUseCase.CreateInventoryCommand command = new CreateInventoryUseCase.CreateInventoryCommand(
            new ProductId(input.productId()),
            input.totalQuantity(),
            input.lowStockThreshold(),
            input.maxPurchaseQuantity(),
            input.reservationTimeout()
        );
        return createInventoryUseCase.createInventory(command);
    }

    @MutationMapping
    public Mono<Boolean> reserveInventory(@Argument ReserveInventoryInput input) {
        ReserveInventoryUseCase.ReserveInventoryCommand command = new ReserveInventoryUseCase.ReserveInventoryCommand(
            new ProductId(input.productId()),
            input.quantity()
        );
        return reserveInventoryUseCase.reserve(command)
            .thenReturn(true)
            .onErrorReturn(false);
    }

    @MutationMapping
    public Mono<Boolean> confirmInventory(@Argument ReserveInventoryInput input) {
        ConfirmInventoryUseCase.ConfirmInventoryCommand command = new ConfirmInventoryUseCase.ConfirmInventoryCommand(
            new ProductId(input.productId()),
            input.quantity()
        );
        return confirmInventoryUseCase.confirm(command)
            .thenReturn(true)
            .onErrorReturn(false);
    }

    @MutationMapping
    public Mono<Boolean> releaseInventory(@Argument ReserveInventoryInput input) {
        ReleaseInventoryUseCase.ReleaseInventoryCommand command = new ReleaseInventoryUseCase.ReleaseInventoryCommand(
            new ProductId(input.productId()),
            input.quantity()
        );
        return releaseInventoryUseCase.release(command)
            .thenReturn(true)
            .onErrorReturn(false);
    }

    @SchemaMapping(typeName = "Inventory", field = "inventoryId")
    public String inventoryId(Inventory inventory) {
        return inventory.inventoryId().value();
    }

    @SchemaMapping(typeName = "Inventory", field = "productId")
    public String productId(Inventory inventory) {
        return inventory.getProductId().value();
    }

    // Input DTOs
    public record CreateInventoryInput(
        String productId,
        int totalQuantity,
        int lowStockThreshold,
        int maxPurchaseQuantity,
        int reservationTimeout
    ) {}

    public record ReserveInventoryInput(
        String productId,
        int quantity
    ) {}
}

