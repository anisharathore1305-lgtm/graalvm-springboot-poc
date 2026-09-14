package com.graalvm.poc.repository;

import com.graalvm.poc.model.JpaUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<JpaUser, Integer> {
}