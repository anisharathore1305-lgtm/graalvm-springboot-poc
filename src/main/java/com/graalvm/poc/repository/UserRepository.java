package com.graalvm.poc.repository;

import com.graalvm.poc.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAll() {

        String sql = "SELECT id, name, email FROM users";

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                )
        );
    }
}