package com.restaurants.service;

import com.restaurants.dtoconversion.DtoConversion;
import com.restaurants.entities.FoodCategory;
import com.restaurants.exception.AlreadyExistsException;
import com.restaurants.exception.ResourceNotFoundException;
import com.restaurants.dto.FoodCategoryRequest;
import com.restaurants.dto.FoodCategoryResponse;
import com.restaurants.repository.FoodCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class FoodCategoryServiceTest {

    @InjectMocks
    private FoodCategoryService service;

    @Mock
    private FoodCategoryRepository repo;

    @Mock
    private DtoConversion dtoConv;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addCategoryTest() {
        FoodCategoryRequest req = new FoodCategoryRequest();
        req.setRestaurantId(1L);
        req.setCategoryName("Sample Category");

        FoodCategory category = new FoodCategory();
        category.setCategoryId(1L);
        category.setRestaurantId(1L);
        category.setCategoryName("Sample Category");

        when(dtoConv.convertToFoodCategoryEntity(req)).thenReturn(category);
        when(repo.save(any(FoodCategory.class))).thenReturn(category);
        when(dtoConv.convertToFoodCategoryResponse(category)).thenReturn(new FoodCategoryResponse());

        FoodCategoryResponse res = service.addFoodCategory(req);

        assertNotNull(res);
        verify(dtoConv, times(1)).convertToFoodCategoryEntity(req);
        verify(repo, times(1)).save(category);
        verify(dtoConv, times(1)).convertToFoodCategoryResponse(category);
    }

    @Test
    void deleteCategoryNotFoundTest() {
        when(repo.existsById(anyLong())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteFoodCategory(1L));

        verify(repo, times(1)).existsById(1L);
    }

    @Test
    void deleteCategoryTest() throws ResourceNotFoundException {
        when(repo.existsById(anyLong())).thenReturn(true);

        service.deleteFoodCategory(1L);

        verify(repo, times(1)).existsById(1L);
        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void getCategoriesByRestaurantIdTest() {
        FoodCategory category = new FoodCategory();
        category.setCategoryId(1L);
        category.setRestaurantId(1L);
        category.setCategoryName("Sample Category");

        when(repo.findByRestaurantId(anyLong())).thenReturn(Arrays.asList(category));
        when(dtoConv.convertToFoodCategoryResponse(any(FoodCategory.class))).thenReturn(new FoodCategoryResponse());

        List<FoodCategoryResponse> resList = service.getAllCategoriesByRestaurantId(1L);

        assertNotNull(resList);
        assertEquals(1, resList.size());
        verify(repo, times(1)).findByRestaurantId(1L);
    }

    @Test
    void updateCategoryNotFoundTest() {
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateCategoryName(1L, "Sample Category"));

        verify(repo, times(1)).findById(1L);
    }

    @Test
    void updateCategoryTest() throws ResourceNotFoundException {
        FoodCategory category = new FoodCategory();
        category.setCategoryId(1L);
        category.setCategoryName("Sample Category");

        when(repo.findById(anyLong())).thenReturn(Optional.of(category));
        when(repo.save(any(FoodCategory.class))).thenReturn(category);
        when(dtoConv.convertToFoodCategoryResponse(any(FoodCategory.class))).thenReturn(new FoodCategoryResponse());

        FoodCategoryResponse res = service.updateCategoryName(1L, "Sample Category");

        assertNotNull(res);
        assertEquals("Sample Category", category.getCategoryName());
        verify(repo, times(1)).findById(1L);
        verify(repo, times(1)).save(category);
    }


    @Test
    void addCategoryAlreadyExistsTest() {
        FoodCategoryRequest req = new FoodCategoryRequest();
        req.setRestaurantId(1L);
        req.setCategoryName("Sample Category");

        when(repo.existsByRestaurantIdAndCategoryNameIgnoreCase(anyLong(), any(String.class))).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> service.addFoodCategory(req));

        verify(repo, times(1)).existsByRestaurantIdAndCategoryNameIgnoreCase(anyLong(), any(String.class));
    }

    @Test
    void updateCategoryNameExceptionHandlingTest() {
        when(repo.findById(anyLong())).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> service.updateCategoryName(1L, "Sample Category"));

        verify(repo, times(1)).findById(1L);
    }

    @Test
    void testGetFoodCategoryById_Success() throws ResourceNotFoundException {
        // Arrange
        Long categoryId = 1L;
        FoodCategory mockCategory = new FoodCategory();
        mockCategory.setCategoryId(categoryId);
        mockCategory.setCategoryName("Sample Category");
        mockCategory.setRestaurantId(2L);

        when(repo.findById(categoryId)).thenReturn(Optional.of(mockCategory));

        // Act
        FoodCategoryResponse response = service.getFoodCategoryById(categoryId);

        // Assert
        assertNotNull(response);
        assertEquals(categoryId, response.getCategoryId());
        assertEquals("Sample Category", response.getCategoryName());
        assertEquals(2L, response.getRestaurantId());
        verify(repo, times(1)).findById(categoryId);
    }

    @Test
    void testGetFoodCategoryById_NotFound() {
        // Arrange
        Long categoryId = 1L;
        when(repo.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.getFoodCategoryById(categoryId));

        assertEquals("Food Category not found with id " + categoryId, exception.getMessage());
        verify(repo, times(1)).findById(categoryId);
    }

}

