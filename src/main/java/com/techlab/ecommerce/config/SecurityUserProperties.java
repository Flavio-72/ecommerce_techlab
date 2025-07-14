package com.techlab.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "security.user")
@Getter
@Setter
public class SecurityUserProperties {
    private String name;
    private String password;
    private String roles;
}
