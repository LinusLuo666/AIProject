package com.example.authsystem.dto;

public class ApiKeyValidationResponse {

    private boolean valid;

    public ApiKeyValidationResponse(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
