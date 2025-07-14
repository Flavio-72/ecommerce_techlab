package com.techlab.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String roles;
    private boolean enabled;
    private boolean emailVerified;
}
