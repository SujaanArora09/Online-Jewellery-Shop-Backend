package com.jewelleryshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Address;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.OrderService;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OrderController orderController;

    private User user;
    private Address address;
    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();

        // Setting up user, address, and order for tests
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setRole("USER");

        address = new Address();
        address.setId(1L);
        address.setFirstName("John");
        address.setLastName("Doe");
        address.setStreetAddress("123 Test St");
        address.setCity("Test City");
        address.setState("Test State");
        address.setZipCode("12345");
        address.setMobile("1234567890");
        address.setUser(user);

        user.setAddresses(Collections.singletonList(address));

        order = new Order();
        order.setId(1L);
        order.setOrderId("ORD123");
        order.setUser(user);
        order.setShippingAddress(address);
        order.setTotalPrice(100.00);
        order.setOrderStatus(null); // Set to appropriate status if needed
    }

    @Test
    public void testCreateOrder() throws Exception {
        String jwtToken = "mockJwtToken";
        // Stubbing userService and orderService
        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.createOrder(eq(user), eq(address))).thenReturn(order);
        
        // Create the request body based on Address
        String requestBody = "{"
                + "\"firstName\": \"" + address.getFirstName() + "\","
                + "\"lastName\": \"" + address.getLastName() + "\","
                + "\"streetAddress\": \"" + address.getStreetAddress() + "\","
                + "\"city\": \"" + address.getCity() + "\","
                + "\"state\": \"" + address.getState() + "\","
                + "\"zipCode\": \"" + address.getZipCode() + "\","
                + "\"mobile\": \"" + address.getMobile() + "\""
                + "}";

        // Perform the mock MVC call
        mockMvc.perform(post("/api/orders/")
                .header("Authorization", jwtToken)
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk());  
                }
                
                

    @Test
    public void testGetUserOrderHistory() throws Exception {
        String jwtToken = "mockJwtToken";
        
        // Stubbing userService and orderService
        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.usersOrderHistory(user.getId())).thenReturn(Collections.singletonList(order));

        mockMvc.perform(get("/api/orders/user")
                .header("Authorization", jwtToken))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$[0].orderId").value("ORD123"))
                .andExpect(jsonPath("$[0].shippingAddress.city").value("Test City"));
    }

    @Test
    public void testFindOrderById() throws Exception {
        String jwtToken = "mockJwtToken";
        
        // Stubbing userService and orderService
        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.findOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/{orderId}", 1L)
                .header("Authorization", jwtToken))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.orderId").value("ORD123"))
                .andExpect(jsonPath("$.shippingAddress.city").value("Test City"));
    }

   /* @Test
    public void testCreateOrder_UserException() throws Exception {
        String jwtToken = "mockJwtToken";
        
        // Simulate user exception when the service is called
        when(userService.findUserProfileByJwt(jwtToken)).thenThrow(new UserException("User not found"));

        // Create a dynamic address object to be used in the request
        Address testAddress = new Address();
        testAddress.setFirstName("John");
        testAddress.setLastName("Doe");
        testAddress.setStreetAddress("123 Test St");
        testAddress.setCity("Test City");
        testAddress.setState("Test State");
        testAddress.setZipCode("12345");
        testAddress.setMobile("1234567890");

        // Convert the Address object to a JSON string for the request body
        String requestBody = new ObjectMapper().writeValueAsString(testAddress);

        MockHttpServletRequestBuilder request = post("/api/orders/")
                .header("Authorization", jwtToken)
                .contentType("application/json")
                .content(requestBody);

        mockMvc.perform(request)  
        .andDo(print())  // This will print the response body to the console
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("User not found"));

    }




    @Test
    public void testGetUserOrderHistory_OrderException() throws Exception {
        String jwtToken = "mockJwtToken";
        
        // Simulate order exception
        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(user);
        when(orderService.usersOrderHistory(user.getId())).thenThrow(new OrderException("No orders found"));

        mockMvc.perform(get("/api/orders/user")
                .header("Authorization", jwtToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No orders found"));
    } */
}
