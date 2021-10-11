package com.example.registersystembackend.presentation.layer.configuration;

import javax.validation.constraints.NotBlank;

class GlobalExceptionResponse {

    @NotBlank
    private final String message;

    /**
     * This is used for internal exceptions
     */
    GlobalExceptionResponse() {
        this("An internal error has occurred");
    }

    GlobalExceptionResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
