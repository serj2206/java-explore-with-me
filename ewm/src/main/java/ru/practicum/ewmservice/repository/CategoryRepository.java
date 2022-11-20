package ru.practicum.ewmservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmservice.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);

    Page<Category> findCategoriesByIdIsNotNull(Pageable pageable);
}
