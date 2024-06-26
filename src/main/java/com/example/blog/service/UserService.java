package com.example.blog.service;

import com.example.blog.entity.User;
import com.example.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  public PasswordEncoder passwordEncoder;

  public User registerUser(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt();
    return userRepository.save(user);
  }
  public Map<String, Object> findAllUsers(int page) {
    PageRequest pageable = PageRequest.of(page, 15);
    Map<String, Object> usersData = new HashMap<>();
    Page<User> users = userRepository.findAllPaged(pageable);
    usersData.put("currentPage", users.getNumber());
    usersData.put("totalPages", users.getTotalPages());
    usersData.put("users", users.getContent());
    return usersData;
  }

  public User findUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByUsername(username);
    if (user == null) {
      throw new UsernameNotFoundException("User not found");
    }
    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword())
        .authorities("USER").build();
  }

  public void updateUser(User user) {
    userRepository.updateUser(user.getUsername(), user.getPassword(), user.getEmail(), user.getId());
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public void changePassword(String username, String password){
    User user = findUserByUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    updateUser(user);
  }

  public void changeInformation(String username, String email, String name, String newUsername){
    User user = findUserByUsername(username);
    user.setEmail(email);
    user.setUsername(newUsername);
    user.setName(name);
    updateUser(user);
  }
}
