package com.restaurants.service;

import com.restaurants.constant.ConstantMessage;
import com.restaurants.dtoconversion.DtoConversion;
import com.restaurants.entities.FoodCategory;
import com.restaurants.exception.AlreadyExistsException;
import com.restaurants.exception.ResourceNotFoundException;
import com.restaurants.dto.FoodCategoryRequest;
import com.restaurants.dto.FoodCategoryResponse;
import com.restaurants.repository.FoodCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for handling operations related to food categories.
 */
@Service
@Slf4j
public class FoodCategoryService {


   
    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    @Autowired
    private DtoConversion dtoConversion;

    /**
     * Adds a new food category.
     *
     * @param foodCategoryRequest the request object containing category details
     * @return the response object containing details of the added food category
     */


    public FoodCategoryResponse addFoodCategory(final FoodCategoryRequest foodCategoryRequest) {
        log.info("Adding a new food category with details: {}", foodCategoryRequest);

        boolean exists = foodCategoryRepository.existsByRestaurantIdAndCategoryNameIgnoreCase(
                foodCategoryRequest.getRestaurantId(),
                foodCategoryRequest.getCategoryName()
        );

        if (exists) {
            log.error("Duplicate category: {} already exists for restaurant ID: {}",
                    foodCategoryRequest.getCategoryName(),
                    foodCategoryRequest.getRestaurantId());
           throw new AlreadyExistsException(ConstantMessage.CATEGORY_ALREADY_EXISTS);
        }

        FoodCategory foodCategory = dtoConversion.convertToFoodCategoryEntity(foodCategoryRequest);
        FoodCategory savedFoodCategory = foodCategoryRepository.save(foodCategory);

        return dtoConversion.convertToFoodCategoryResponse(savedFoodCategory);
    }

    /**
     * Deletes a food category by its ID.
     *
     * @param categoryId the ID of the category to delete
     * @throws ResourceNotFoundException if the category with the given ID is not found
     */
    public void deleteFoodCategory(final Long categoryId) throws ResourceNotFoundException {
        log.info("Attempting to delete food category with ID: {}", categoryId);
        if (!foodCategoryRepository.existsById(categoryId)) {
            log.error("Food category with ID: {} not found", categoryId);
            throw new ResourceNotFoundException(ConstantMessage.CATEGORY_NOT_FOUND);
        }
        foodCategoryRepository.deleteById(categoryId);
        log.info("Food category with ID: {} deleted successfully", categoryId);
    }

    /**
     * Retrieves all food categories for a given restaurant ID.
     *
     * @param restaurantId the ID of the restaurant to get categories for
     * @return a list of response objects containing food category details
     */
    public List<FoodCategoryResponse> getAllCategoriesByRestaurantId(final Long restaurantId) {
        log.info("Retrieving all food categories for restaurant ID: {}", restaurantId);
        List<FoodCategory> categories = foodCategoryRepository.findByRestaurantId(restaurantId);
        List<FoodCategoryResponse> responseList = new ArrayList<>();

        for (FoodCategory category : categories) {
            FoodCategoryResponse response = dtoConversion.convertToFoodCategoryResponse(category);
            responseList.add(response);
        }
        log.info("Retrieved {} food categories for restaurant ID: {}", responseList.size(), restaurantId);
        return responseList;
    }

    /**
     * Updates the name of an existing food category.
     *
     * @param categoryId the ID of the category to update
     * @param newCategoryName the new name to set for the category
     * @return the response object containing updated category details
     * @throws ResourceNotFoundException if the category with the given ID is not found
     */

    public FoodCategoryResponse updateCategoryName(final Long categoryId, final String newCategoryName) throws ResourceNotFoundException {
        log.info("Updating category name for category ID: {} to new name: {}", categoryId, newCategoryName);

        FoodCategory existingCategory = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> {
                    log.error("Food category with ID: {} not found", categoryId);
                    return new ResourceNotFoundException(ConstantMessage.CATEGORY_NOT_FOUND);
                });

        existingCategory.setCategoryName(newCategoryName);

        FoodCategory updatedCategory = foodCategoryRepository.save(existingCategory);
        log.info("Category name updated successfully for ID: {}", categoryId);

        return dtoConversion.convertToFoodCategoryResponse(updatedCategory);
    }

    /**
     * Retrieves a food category by its ID.
     *
     * @param categoryId the ID of the category to retrieve
     * @return the response object containing the food category details
     * @throws ResourceNotFoundException if the category with the given ID is not found
     */
    public FoodCategoryResponse getFoodCategoryById(final Long categoryId) throws ResourceNotFoundException {
        FoodCategory category = foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Food Category not found with id " + categoryId));


        FoodCategoryResponse response = new FoodCategoryResponse();
        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setRestaurantId(category.getRestaurantId());

        return response;
    }
}
