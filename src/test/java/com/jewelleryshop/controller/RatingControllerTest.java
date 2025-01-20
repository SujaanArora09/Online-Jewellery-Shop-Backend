package com.jewelleryshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelleryshop.controller.RatingController;
import com.jewelleryshop.exception.ProductException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Rating;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.request.RatingRequest;
import com.jewelleryshop.service.RatingServices;
import com.jewelleryshop.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(RatingController.class)
public class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;  // Mock the UserService

    @MockBean
    private RatingServices ratingServices;  // Mock the RatingServices

    @InjectMocks
    private RatingController ratingController;

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 1L;
    private static final double RATING_VALUE = 4.5;
    private static final String JWT_TOKEN = "Bearer sample-jwt-token";

    @Test
    public void testCreateRating() throws Exception {
        // Create a mock user
        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("password");
        mockUser.setRole("USER");
        mockUser.setMobile("1234567890");

        // Create a mock Rating object
        Rating mockRating = new Rating();
        mockRating.setId(1L);
        mockRating.setUser(mockUser);
        mockRating.setRating(RATING_VALUE);
        mockRating.setCreatedAt(LocalDateTime.now());

        // Create the RatingRequest object
        RatingRequest ratingRequest = new RatingRequest();
        ratingRequest.setProductId(PRODUCT_ID);
        ratingRequest.setRating(RATING_VALUE);

        // Convert RatingRequest to JSON using ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        String ratingRequestJson = objectMapper.writeValueAsString(ratingRequest);

        // Mock the services
        when(userService.findUserProfileByJwt(JWT_TOKEN)).thenReturn(mockUser);
        when(ratingServices.createRating(any(), any(User.class))).thenReturn(mockRating);

        // Perform the POST request to /api/ratings/create
        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ratingRequestJson)  // Pass the dynamically created JSON
                .header("Authorization", JWT_TOKEN));

    }
    
    @Test
    public void testGetProductsReview() throws Exception {
        // Create a mock User object
        User mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("password");
        mockUser.setRole("USER");
        mockUser.setMobile("1234567890");

        // Create a mock Rating object for the product
        Rating mockRating = new Rating();
        mockRating.setId(1L);
        mockRating.setUser(mockUser);
        mockRating.setRating(RATING_VALUE);
        mockRating.setCreatedAt(LocalDateTime.now());

        // Mock the rating service to return a list of ratings for the product
        when(ratingServices.getProductsRating(PRODUCT_ID)).thenReturn(List.of(mockRating));

        // Simulate a GET request to /api/ratings/product/{productId}
        mockMvc.perform(get("/api/ratings/product/{productId}", PRODUCT_ID));
                
               

      }
    
    
    @Test
    public void testCreateRating_UserException() throws Exception {
        // Simulate an error where the user is not found
        when(userService.findUserProfileByJwt(JWT_TOKEN)).thenThrow(new UserException("User not found"));

        mockMvc.perform(post("/api/ratings/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productId\": " + PRODUCT_ID + ", \"rating\": " + RATING_VALUE + "}")
                .header("Authorization", JWT_TOKEN));
    }
}
    





   