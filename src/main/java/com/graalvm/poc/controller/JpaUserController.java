package com.graalvm.poc.controller;

import com.graalvm.poc.model.JpaUser;
import com.graalvm.poc.repository.JpaUserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JpaUserController {

    private final JpaUserRepository repository;

    public JpaUserController(JpaUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users-jpa")
    public List<JpaUser> getUsers() {
        return repository.findAll();
    }
}