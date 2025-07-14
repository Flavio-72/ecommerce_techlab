package com.techlab.ecommerce.service;

import com.techlab.ecommerce.model.entity.AppUser;
import com.techlab.ecommerce.repository.AppUserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AppUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Convierte roles String a String[]
        String[] rolesArray = user.getRoles() == null || user.getRoles().isBlank()
                ? new String[] { "USER" }
                : user.getRoles().split(",");

        System.out.println("Cargando usuario desde DB: " + user.getUsername() + " / roles: " + user.getRoles());

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(rolesArray)
                .disabled(!user.isEnabled())
                .build();
    }

}
