package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.product.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

/**
 * Product Domain ↔ Document Mapper
 */
@Component
public class ProductMapper {

    /**
     * Domain Product → Document 변환
     */
    public ProductDocument toDocument(Product product) {
        return new ProductDocument(
            product.getProductId().getValue(),
            product.getProductId().getValue(), // dealType은 별도 처리 필요
            product.getTitle(),
            null, // subtitle은 Product에 없음
            product.getDescription(),
            null, // category는 Product에 없음
            null, // images는 Product에 없음
            toPriceDocument(product.getPrice()),
            toScheduleDocument(product.getSchedule()),
            product.getStatus(),
            product.getSpecs().getFields(),
            null, // metadata는 Product에 없음
            Instant.now(), // createdAt
            Instant.now()  // updatedAt
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
            price,
            schedule,
            specs
        );

        // 상태 설정
        if (document.getStatus() != null) {
            product.transitionTo(document.getStatus());
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





