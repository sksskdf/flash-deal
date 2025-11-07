package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

import com.flashdeal.app.domain.order.PaymentStatus;

public class PaymentDocument {
    private String method;
    private PaymentStatus status;

    public PaymentDocument(String method, PaymentStatus status) {
        this.method = method;
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}