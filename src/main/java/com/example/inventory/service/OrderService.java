package com.example.inventory.service;

import com.example.inventory.dto.CreateOrderRequest;
import com.example.inventory.dto.OrderItemRequest;
import com.example.inventory.dto.OrderResponse;
import com.example.inventory.model.*;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final InventoryValidationService inventoryValidationService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        InventoryValidationService inventoryValidationService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.inventoryValidationService = inventoryValidationService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + request.customerId()));

        if (!inventoryValidationService.isInventoryAvailable(request.items())) {
            throw new IllegalStateException("One or more items do not have sufficient stock available.");
        }

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(orderNumber, customer, OrderStatus.CONFIRMED);
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem(product, itemRequest.quantity(), product.getPrice());
            order.addItem(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        try {
            syncInventoryAuditTrail(savedOrder);
        } catch (Exception ex) {
            // Non-critical audit trail failure: proceed with order completion
        }

        return mapToResponse(savedOrder);
    }

    private void syncInventoryAuditTrail(Order order) {
        List<Order> customerOrders = orderRepository.findAll().stream()
                .filter(o -> o.getCustomer().getId().equals(order.getCustomer().getId()))
                .toList();

        for (Order o : customerOrders) {
            for (OrderItem item : o.getItems()) {
                String sku = item.getProduct().getSku();
                int qty = item.getQuantity();
            }
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {
        int itemCount = order.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemCount
        );
    }
}
