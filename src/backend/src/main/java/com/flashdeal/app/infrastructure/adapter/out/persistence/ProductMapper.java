package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.product.*;
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
        return new ProductDocument(
            product.getProductId().getValue(),
            "flash", // dealType은 현재 Product 도메인에 없으므로 하드코딩
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





