package com.flashdeal.app.infrastructure.adapter.out.persistence.mapper;

import com.flashdeal.app.domain.product.*;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.PriceDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ProductDocument;
import com.flashdeal.app.infrastructure.adapter.out.persistence.documents.ScheduleDocument;

import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Product Domain ↔ Document Mapper
 */
@Component
public class ProductMapper {

    /**
     * Domain Product → Document 변환
     */
    public ProductDocument toDocument(Product product) {
        Instant now = Instant.now();
        return new ProductDocument(
            product.getProductId().getValue(),
            "flash",
            product.getTitle(),
            null,
            product.getDescription(),
            product.getCategory(),
            null,
            toPriceDocument(product.getPrice()),
            toScheduleDocument(product.getSchedule()),
            product.getStatus(),
            product.getSpecs().getFields(),
            null,
            now,
            now
        );
    }

    /**
     * Document → Domain Product 변환
     */
    public Product toDomain(ProductDocument document) {
        ProductId productId = new ProductId(document.getId());
        Price price = toPrice(document.getPrice());
        Schedule schedule = toSchedule(document.getSchedule());
        Specs specs = new Specs(document.getSpecs());

        Product product = new Product(
            productId,
            document.getTitle(),
            document.getDescription(),
            document.getCategory(),
            price,
            schedule,
            specs
        );

        // 상태 설정 (DB에서 읽어온 값이므로 전이 검증 없이 직접 설정)
        if (document.getStatus() != null) {
            product.updateStatus(document.getStatus());
        }

        return product;
    }

    private PriceDocument toPriceDocument(Price price) {
        return new PriceDocument(
            price.getOriginal(),
            price.getSale(),
            price.getCurrency(),
            price.discountRate()
        );
    }

    private Price toPrice(PriceDocument document) {
        return new Price(
            document.getOriginal(),
            document.getSale(),
            document.getCurrency()
        );
    }

    private ScheduleDocument toScheduleDocument(Schedule schedule) {
        return new ScheduleDocument(
            schedule.getStartsAt().toInstant(),
            schedule.getEndsAt().toInstant(),
            schedule.getTimezone()
        );
    }

    private Schedule toSchedule(ScheduleDocument document) {
        return new Schedule(
            document.getStartsAt().atZone(java.time.ZoneId.of(document.getTimezone())),
            document.getEndsAt().atZone(java.time.ZoneId.of(document.getTimezone())),
            document.getTimezone()
        );
    }
}