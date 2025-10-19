package com.example.authsystem.dto;

public class CommandPreviewResponse {

    private String command;

    public CommandPreviewResponse() {
    }

    public CommandPreviewResponse(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
