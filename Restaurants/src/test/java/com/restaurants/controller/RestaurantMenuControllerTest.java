package com.restaurants.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurants.constant.ConstantMessage;
import com.restaurants.dto.RestaurantMenuRequest;
import com.restaurants.dto.RestaurantMenuResponse;
import com.restaurants.dto.SuccessResponse;
import com.restaurants.exception.ResourceNotFoundException;
import com.restaurants.service.RestaurantMenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RestaurantMenuControllerTest {

    @Mock
    private RestaurantMenuService restaurantMenuService;

    @InjectMocks
    private RestaurantMenuController restaurantMenuController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(restaurantMenuController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddFoodItem() {
        // Arrange
        RestaurantMenuRequest request = new RestaurantMenuRequest();
        MultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "dummy-image".getBytes());


        // Act
        ResponseEntity<SuccessResponse> response = restaurantMenuController.addFoodItem(request, image);

        // Assert
        assertEquals(201, response.getStatusCodeValue());

    }

    @Test
    void getFoodItemsByRestaurantIdTest() throws Exception {
        Long restaurantId = 1L;
        List<RestaurantMenuResponse> responses = new ArrayList<>();
        RestaurantMenuResponse response = new RestaurantMenuResponse();
        response.setItemName("Pizza");
        responses.add(response);

        when(restaurantMenuService.getFoodItemsByRestaurantId(anyLong())).thenReturn(responses);

        mockMvc.perform(get("/foodItems/getFoodItems/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemName", is("Pizza")));

        verify(restaurantMenuService, times(1)).getFoodItemsByRestaurantId(restaurantId);
    }


    @Test
    void updateRestaurantMenuTest() throws Exception {
        Long restaurantId = 1L;
        RestaurantMenuRequest request = new RestaurantMenuRequest();
        request.setItemName("Updated Pizza");
        request.setPrice(15.99);
        request.setDescription("Updated description");
        request.setVegNonVeg(false);
        request.setCategoryId(2L);
        request.setRestaurantId(1L);

        RestaurantMenuResponse response = new RestaurantMenuResponse();
        response.setItemId(1L);
        response.setItemName("Updated Pizza");
        response.setPrice(15.99);
        response.setDescription("Updated description");
        response.setVegNonVeg(false);
        response.setCategoryId(2L);
        response.setRestaurantId(1L);

        when(restaurantMenuService.updateRestaurantMenu(anyLong(), any(RestaurantMenuRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/foodItems/update/{restaurantId}", restaurantId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemName", is("Updated Pizza")))
                .andExpect(jsonPath("$.price", is(15.99)))
                .andExpect(jsonPath("$.description", is("Updated description")))
                .andExpect(jsonPath("$.vegNonVeg", is(false)))
                .andExpect(jsonPath("$.categoryId", is(2)))
                .andExpect(jsonPath("$.restaurantId", is(1)));

        verify(restaurantMenuService, times(1)).updateRestaurantMenu(restaurantId, request);
    }

    @Test
    void getFoodItemImageTest() throws Exception {
        Long foodItemId = 1L;
        byte[] imageData = new byte[]{1, 2, 3};

        when(restaurantMenuService.getFoodItemImage(anyLong())).thenReturn(imageData);

        mockMvc.perform(get("/foodItems/{foodItemId}/image", foodItemId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageData));

        verify(restaurantMenuService, times(1)).getFoodItemImage(foodItemId);
    }


    @Test
    void testGetFoodItemsByCategoryId() throws ResourceNotFoundException {
        // Arrange
        Long categoryId = 1L;
        List<RestaurantMenuResponse> mockResponseList = new ArrayList<>();
        mockResponseList.add(new RestaurantMenuResponse());

        when(restaurantMenuService.getFoodItemsByCategoryId(categoryId)).thenReturn(mockResponseList);

        // Act
        ResponseEntity<List<RestaurantMenuResponse>> response = restaurantMenuController.getFoodItemsByCategoryId(categoryId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetFoodItemsByCategoryId_ResourceNotFound() throws ResourceNotFoundException {
        // Arrange
        Long categoryId = 999L;

        when(restaurantMenuService.getFoodItemsByCategoryId(categoryId)).thenThrow(new ResourceNotFoundException("Category not found"));

        // Act & Assert
        try {
            restaurantMenuController.getFoodItemsByCategoryId(categoryId);
        } catch (ResourceNotFoundException ex) {
            assertEquals("Category not found", ex.getMessage());
        }
    }



}
