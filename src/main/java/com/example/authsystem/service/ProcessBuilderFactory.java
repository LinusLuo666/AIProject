package com.example.authsystem.service;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessBuilderFactory {

    public ProcessBuilder create(List<String> command) {
        return new ProcessBuilder(command);
    }
}
