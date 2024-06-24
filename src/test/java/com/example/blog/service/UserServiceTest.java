package com.example.blog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setPassword("password");

        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.registerUser(user);

        assertEquals("encodedPassword", registeredUser.getPassword());
        assertNotNull(registeredUser.getCreatedAt());
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindAllUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());

        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 15), 2);
        when(userRepository.findAllPaged(any(PageRequest.class))).thenReturn(page);

        Map<String, Object> usersData = userService.findAllUsers(0);

        assertEquals(0, usersData.get("currentPage"));
        assertEquals(1, usersData.get("totalPages"));
        assertEquals(users, usersData.get("users"));
        verify(userRepository, times(1)).findAllPaged(any(PageRequest.class));
    }

    @Test
    void testFindUserByUsername() {
        User user = new User();
        when(userRepository.findByUsername("testUser")).thenReturn(user);

        User foundUser = userService.findUserByUsername("testUser");

        assertSame(user, foundUser);
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testLoadUserByUsername() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");

        when(userRepository.findByUsername("testUser")).thenReturn(user);

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertEquals("testUser", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals("USER")));
        verify(userRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsername("nonExistingUser")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonExistingUser"));
        verify(userRepository, times(1)).findByUsername("nonExistingUser");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("password");
        user.setEmail("test@example.com");

        userService.updateUser(user);

        verify(userRepository, times(1)).updateUser("testUser", "password", "test@example.com", 1L);
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testChangePassword() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("oldPassword");

        when(userService.findUserByUsername("testUser")).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        userService.changePassword("testUser", "newPassword");

        assertEquals("encodedNewPassword", user.getPassword());
        verify(userRepository, times(1)).findByUsername("testUser");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(userRepository, times(1)).updateUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getId());
    }

    @Test
    void testChangeInformation() {
        User user = new User();
        user.setUsername("oldUsername");
        user.setEmail("oldEmail");
        user.setName("oldName");

        when(userService.findUserByUsername("oldUsername")).thenReturn(user);

        userService.changeInformation("oldUsername", "newEmail", "newName", "newUsername");

        assertEquals("newEmail", user.getEmail());
        assertEquals("newName", user.getName());
        assertEquals("newUsername", user.getUsername());
        verify(userRepository, times(1)).findByUsername("oldUsername");
        verify(userRepository, times(1)).updateUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getId());
    }
}
