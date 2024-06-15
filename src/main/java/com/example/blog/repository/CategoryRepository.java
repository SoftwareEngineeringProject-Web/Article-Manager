package com.example.blog.repository;

import com.example.blog.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  List<Category> findByParentId(Long parentId);

  List<Category> findByUserId(Long userId);

  void deleteById(Long id);
}
