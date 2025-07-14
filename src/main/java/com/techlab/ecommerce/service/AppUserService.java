package com.techlab.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.techlab.ecommerce.model.entity.AppUser;
import com.techlab.ecommerce.repository.AppUserRepository;
import com.techlab.ecommerce.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUser registerUser(AppUser user) {

        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setUsername(StringUtils.toTitleCase(user.getUsername()));

        if (user.getRoles() == null || user.getRoles().isBlank() || !user.getRoles().equalsIgnoreCase("ADMIN")) {
            user.setRoles("USER");
        }

        AppUser savedUser = userRepository.save(user);

        return savedUser;
    }

    public boolean verifyPassword(String username, String plainPassword) {
        Optional<AppUser> userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isPresent()) {
            AppUser user = userOpt.get();
            boolean matches = passwordEncoder.matches(plainPassword, user.getPassword());

            return matches;
        }
        return false;
    }

    public Optional<AppUser> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<AppUser> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<AppUser> findByUsername(String username) {
        return userRepository.findByUsernameContainingIgnoreCase(username);
    }

    public List<AppUser> listUsers() {
        return userRepository.findAll();
    }

    public Optional<AppUser> updateUser(Long id, AppUser updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setUsername(StringUtils.toTitleCase(updatedUser.getUsername()));
            user.setEmail(updatedUser.getEmail());
            // Solo actualiza password si no está vacío o nulo
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }
            user.setRoles(updatedUser.getRoles());
            return userRepository.save(user);
        });
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}