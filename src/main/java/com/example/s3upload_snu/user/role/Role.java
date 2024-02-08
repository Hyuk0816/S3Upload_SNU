package com.example.s3upload_snu.user.role;

import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

public enum Role {

    ADMIN("ADMIN");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
