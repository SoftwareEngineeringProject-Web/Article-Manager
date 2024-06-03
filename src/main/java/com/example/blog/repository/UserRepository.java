package com.example.blog.repository;

import com.example.blog.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = ?1, u.password = ?2, u.email = ?3 WHERE u.id = ?4")
    void updateUser(String username, String password, String email, Long id);}
