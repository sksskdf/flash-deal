package com.flashdeal.app.infrastructure.adapter.out.persistence;

/**
 * Stock MongoDB Document
 */
public class StockDocument {
    
    private int total;
    private int reserved;
    private int available;
    private int sold;

    public StockDocument() {}

    public StockDocument(int total, int reserved, int available, int sold) {
        this.total = total;
        this.reserved = reserved;
        this.available = available;
        this.sold = sold;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }
}





