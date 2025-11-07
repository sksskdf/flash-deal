package com.flashdeal.app.infrastructure.adapter.out.persistence.documents;

public class StockDocument {
    private int total;
    private int reserved;
    private int available;
    private int sold;

    public StockDocument(int total, int reserved, int available, int sold) {
        this.total = total;
        this.reserved = reserved;
        this.available = available;
        this.sold = sold;
    }

    public int getTotal() {
        return total;
    }

    public int getReserved() {
        return reserved;
    }

    public int getAvailable() {
        return available;
    }

    public int getSold() {
        return sold;
    }
}