package com.example.blog.repository;

import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Modifying
    @Query("SELECT DISTINCT c FROM Article a JOIN a.category c WHERE a.user.id = ?1")
    List<Category> findCategoriesByUserId(Long userId);

    List<Category> findByParentId(Long parentId);

    void deleteById(Long id);
}
