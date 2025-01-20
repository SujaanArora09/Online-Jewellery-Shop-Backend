package com.jewelleryshop.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Cart;
import com.jewelleryshop.modal.CartItem;
import com.jewelleryshop.modal.Product;
import com.jewelleryshop.modal.User;
import com.jewelleryshop.service.CartService;
import com.jewelleryshop.service.UserService;

@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CartController cartController;

    @Autowired
    private MockMvc mockMvc;

    private String jwtToken;

    private Cart mockCart;

    @BeforeEach
    public void setUp() throws UserException {
        jwtToken = "Bearer mock-jwt-token"; // Mock JWT token

        // Mock user service to return a user when finding by JWT
        when(userService.findUserProfileByJwt(jwtToken)).thenReturn(createMockUser());

        // Create a mock Cart
        mockCart = createMockCart();

        // Mock CartService to return the mock cart
        when(cartService.findUserCart(1L)).thenReturn(mockCart);

        // Initialize MockMvc with standaloneSetup
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    public void testFindUserCartHandler_Success() throws Exception {
        // Perform GET request to fetch the user's cart
        mockMvc.perform(get("/api/cart/")
                        .header("Authorization", jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.id").value(mockCart.getUser().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user.email").value(mockCart.getUser().getEmail()));
    }

	/*
	 * @Test public void testAddItemToCart_Success() throws Exception { // Simulate
	 * fetching a valid product Product mockProduct = createMockProduct();
	 * 
	 * // Create an AddItemRequest AddItemRequest addItemRequest = new
	 * AddItemRequest(); addItemRequest.setProductId(mockProduct.getId());
	 * addItemRequest.setQuantity(2); addItemRequest.setSize("M");
	 * 
	 * // Mock the behavior of the cart service to add a cart item using argument
	 * matcher CartItem mockCartItem = createMockCartItem(mockProduct);
	 * when(cartService.addCartItem(eq(1L),
	 * any(AddItemRequest.class))).thenReturn(mockCartItem);
	 * 
	 * // Perform PUT request to add an item to the cart
	 * mockMvc.perform(put("/api/cart/add") .header("Authorization", jwtToken)
	 * .contentType(MediaType.APPLICATION_JSON)
	 * .content("{\"productId\": 1, \"quantity\": 2, \"size\": \"M\"}"))
	 * .andExpect(MockMvcResultMatchers.status().isAccepted())
	 * .andExpect(MockMvcResultMatchers.jsonPath("$.product.id").value(mockProduct.
	 * getId())) .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(2))
	 * .andExpect(MockMvcResultMatchers.jsonPath("$.size").value("M")); }
	 */

    // Helper methods to create mock entities
    private Cart createMockCart() {
        Cart cart = new Cart();
        cart.setId(1L); // Mock cart ID
        cart.setUser(createMockUser()); // Mock the user for the cart
        return cart;
    }

    private CartItem createMockCartItem(Product product) {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L); // Set ID for the cart item
        cartItem.setProduct(product);
        cartItem.setSize("M");
        cartItem.setQuantity(2);
        cartItem.setPrice(100);
        cartItem.setDiscountedPrice(90); // Mock discounted price
        cartItem.setUserId(1L); // Mock user ID
        return cartItem;
    }

    private Product createMockProduct() {
        // Create a mock product
        Product product = new Product();
        product.setId(1L); // Set a valid product ID
        product.setTitle("Mock Product");
        product.setPrice(100);
        product.setDiscountedPrice(90);
        product.setDiscountPersent(10);
        product.setQuantity(50);
        product.setBrand("Brand");
        product.setColor("Red");
        return product;
    }

    private User createMockUser() {
        User user = new User();
        user.setId(1L); // Set mock user ID
        user.setEmail("testuser@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        user.setRole("USER");
        return user;
    }
}
