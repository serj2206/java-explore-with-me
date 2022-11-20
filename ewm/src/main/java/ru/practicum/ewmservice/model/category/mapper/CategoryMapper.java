package ru.practicum.ewmservice.model.category.mapper;

import ru.practicum.ewmservice.model.category.Category;
import ru.practicum.ewmservice.model.category.dto.CategoryDto;
import ru.practicum.ewmservice.model.category.dto.NewCategoryDto;

public class CategoryMapper {
    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(null, categoryDto.getName());
    }

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return new Category(null, newCategoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category);
    }
}
