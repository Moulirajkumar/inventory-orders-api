package com.example.inventory.service;

import com.example.inventory.dto.CreateOrderRequest;
import com.example.inventory.dto.OrderItemRequest;
import com.example.inventory.dto.OrderResponse;
import com.example.inventory.model.Customer;
import com.example.inventory.model.Order;
import com.example.inventory.model.OrderStatus;
import com.example.inventory.model.Product;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.OrderRepository;
import com.example.inventory.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer(1L, "Alice Johnson", "alice@example.com");
        testProduct = new Product(10L, "SKU-KEY-01", "Mechanical Keyboard", "Electronics", new BigDecimal("120.00"), 10);
    }

    @Test
    void createOrder_Success_DecrementsStockAndCalculatesTotal() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(10L, 2)));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(new BigDecimal("240.00"), response.totalAmount());
        assertEquals(8, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);
    }

    @Test
    void createOrder_InsufficientStock_ThrowsIllegalStateException() {
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new OrderItemRequest(10L, 15)));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(testProduct));

        assertThrows(IllegalStateException.class, () -> orderService.createOrder(request));
        assertEquals(10, testProduct.getStockQuantity());
        verify(orderRepository, never()).save(any());
    }
}
