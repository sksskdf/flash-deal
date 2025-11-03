package com.flashdeal.app.infrastructure.adapter.out.persistence;

import com.flashdeal.app.domain.order.PaymentStatus;

public class PaymentDocument {
    private String method;
    private CardDocument card;
    private PaymentStatus status;
    private PaymentGatewayDocument gateway;
    private RefundDocument refund;
    private InstallmentDocument installment;

    public PaymentDocument(String method, CardDocument card, PaymentStatus status, PaymentGatewayDocument gateway, RefundDocument refund, InstallmentDocument installment) {
        this.method = method;
        this.card = card;
        this.status = status;
        this.gateway = gateway;
        this.refund = refund;
        this.installment = installment;
    }

    public String getMethod() {
        return method;
    }

    public CardDocument getCard() {
        return card;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentGatewayDocument getGateway() {
        return gateway;
    }

    public RefundDocument getRefund() {
        return refund;
    }

    public InstallmentDocument getInstallment() {
        return installment;
    }
}