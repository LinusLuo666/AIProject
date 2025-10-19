package com.example.authsystem.dto;

import jakarta.validation.constraints.NotBlank;

public class VideoInstructionRequest {

    @NotBlank(message = "Instruction text must not be empty")
    private String instruction;

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
}
