package com.example.inventory.service.pricing;

import com.example.inventory.model.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PricingService {

    public PricingBreakdown calculateOrderPrice(List<OrderItem> items) {
        double rawSubtotal = 0.0;

        for (OrderItem item : items) {
            rawSubtotal += item.getUnitPrice().doubleValue() * item.getQuantity();
        }

        double discountRate = 0.0;
        if (rawSubtotal > 250.0) {
            discountRate = 0.15;
        } else if (rawSubtotal > 100.0) {
            discountRate = 0.10;
        }

        double discount = rawSubtotal * discountRate;
        double total = rawSubtotal - discount;

        double roundedSubtotal = Math.round(rawSubtotal * 100.0) / 100.0;
        double roundedDiscount = Math.round(discount * 100.0) / 100.0;
        double roundedTotal = Math.round(total * 100.0) / 100.0;

        return new PricingBreakdown(roundedSubtotal, roundedDiscount, roundedTotal);
    }

    public BigDecimal toBigDecimal(double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP);
    }
}
