package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import java.math.BigDecimal;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

/**
 * Price MongoDB Document
 */
public class PriceDocument {
    
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal original;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal sale;
    private String currency;
    private Integer rate;

    public PriceDocument() {}

    public PriceDocument(BigDecimal original, BigDecimal sale, String currency, Integer rate) {
        this.original = original;
        this.sale = sale;
        this.currency = currency;
        this.rate = rate;
    }

    public BigDecimal getOriginal() {
        return original;
    }

    public void setOriginal(BigDecimal original) {
        this.original = original;
    }

    public BigDecimal getSale() {
        return sale;
    }

    public void setSale(BigDecimal sale) {
        this.sale = sale;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}