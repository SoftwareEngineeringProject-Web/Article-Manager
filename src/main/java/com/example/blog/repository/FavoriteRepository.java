package com.example.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.blog.entity.Favorite;


@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  @Query("SELECT favorite FROM Favorite favorite WHERE favorite.userId = :userId")
  List<Favorite> findByUserId(Long userId);

  Optional<Favorite> findById(Long userId);

  void deleteById(Long id);
}
