package com.restaurants.controller;

import com.restaurants.dto.FoodCategoryRequest;
import com.restaurants.dto.FoodCategoryResponse;
import com.restaurants.dto.SuccessResponse;

import com.restaurants.service.FoodCategoryService;
import com.restaurants.exception.ResourceNotFoundException;
import com.restaurants.constant.ConstantMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing food categories.
 * Provides endpoints to add, delete, retrieve, and update food categories.
 */
@RestController
@RequestMapping("/foodCategories")
public final class FoodCategoryController {

    private static final Logger logger = LogManager.getLogger(FoodCategoryController.class);

    @Autowired
    private FoodCategoryService foodCategoryService;

    /**
     * Adds a new food category.
     *
     * @param foodCategoryRequest the request object containing the food category details
     * @return a response entity with the created food category
     */

    @PostMapping("/add")
    public ResponseEntity<SuccessResponse> addFoodCategory(@Valid @RequestBody FoodCategoryRequest foodCategoryRequest) {
        foodCategoryService.addFoodCategory(foodCategoryRequest);
        logger.info("Food category added successfully");
        return ResponseEntity.ok(new SuccessResponse(ConstantMessage.CATEGORY_ADD_SUCCESS));
    }

    /**
     * Deletes a food category by its ID.
     *
     * @param categoryId the ID of the food category to delete
     * @return a response entity with a success message or error message if not found
     */
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteFoodCategory(@PathVariable final Long categoryId) {
        logger.info("Request to delete food category with ID: {}", categoryId);
        foodCategoryService.deleteFoodCategory(categoryId);
        logger.info("Food category with ID: {} deleted successfully", categoryId);
        return ResponseEntity.ok("Food category deleted successfully.");
    }

    /**
     * Retrieves all food categories for a specific restaurant.
     *
     * @param restaurantId the ID of the restaurant
     * @return a response entity with a list of food categories
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<FoodCategoryResponse>> getAllCategoriesByRestaurantId(
            @PathVariable final Long restaurantId) {
        logger.info("Request to retrieve food categories for restaurant with ID: {}", restaurantId);

        List<FoodCategoryResponse> categories = foodCategoryService.getAllCategoriesByRestaurantId(restaurantId);
        logger.info("Retrieved {} food categories for restaurant with ID: {}", categories.size(), restaurantId);

        return ResponseEntity.ok(categories);
    }

    /**
     * Updates the name of a food category.
     *
     * @param categoryId      the ID of the food category to update
     * @param newCategoryName the new name for the food category
     * @return a response entity with the updated food category
     * @throws ResourceNotFoundException if the food category is not found
     */
    @PutMapping("/{categoryId}/name")
    public ResponseEntity<FoodCategoryResponse> updateCategoryName(
            @PathVariable final Long categoryId,
            @RequestBody final String newCategoryName) throws ResourceNotFoundException {
        logger.info("Request to update food category name for ID: {} to {}", categoryId, newCategoryName);

        FoodCategoryResponse updatedCategory = foodCategoryService.updateCategoryName(categoryId, newCategoryName);
        logger.info("Food category with ID: {} updated successfully with new name: {}", categoryId, newCategoryName);

        return ResponseEntity.ok(updatedCategory);
    }
}
