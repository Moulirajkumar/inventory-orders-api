package com.example.inventory.service.pricing;

import java.util.Objects;

public class PricingBreakdown {

    private final double subtotal;
    private final double discountAmount;
    private final double finalTotal;

    public PricingBreakdown(double subtotal, double discountAmount, double finalTotal) {
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalTotal = finalTotal;
    }

    public double getSubtotal() { return subtotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getFinalTotal() { return finalTotal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PricingBreakdown that)) return false;
        return Double.compare(that.subtotal, subtotal) == 0 &&
               Double.compare(that.discountAmount, discountAmount) == 0 &&
               Double.compare(that.finalTotal, finalTotal) == 0;
    }
}
