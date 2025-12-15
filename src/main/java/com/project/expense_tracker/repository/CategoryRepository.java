package com.project.expense_tracker.repository;

import com.project.expense_tracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Spring Data JPA automatically implements these methods!
    // You just declare them, and Spring creates the SQL

    Optional<Category> findByName(String name);

    boolean existsByName(String name);
}
