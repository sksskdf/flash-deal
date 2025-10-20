package com.flashdeal.app.adapter.out.persistence;

import com.flashdeal.app.domain.order.PaymentStatus;
import java.time.ZonedDateTime;

/**
 * Payment MongoDB Document
 */
public class PaymentDocument {
    
    private String method;
    private CardDocument card;
    private PaymentStatus status;
    private PaymentGatewayDocument gateway;
    private RefundDocument refund;
    private InstallmentDocument installment;

    public PaymentDocument() {}

    public PaymentDocument(String method, CardDocument card, PaymentStatus status,
                          PaymentGatewayDocument gateway, RefundDocument refund,
                          InstallmentDocument installment) {
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

    public void setMethod(String method) {
        this.method = method;
    }

    public CardDocument getCard() {
        return card;
    }

    public void setCard(CardDocument card) {
        this.card = card;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public PaymentGatewayDocument getGateway() {
        return gateway;
    }

    public void setGateway(PaymentGatewayDocument gateway) {
        this.gateway = gateway;
    }

    public RefundDocument getRefund() {
        return refund;
    }

    public void setRefund(RefundDocument refund) {
        this.refund = refund;
    }

    public InstallmentDocument getInstallment() {
        return installment;
    }

    public void setInstallment(InstallmentDocument installment) {
        this.installment = installment;
    }
}





