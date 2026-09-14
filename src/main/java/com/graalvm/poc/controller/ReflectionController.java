package com.graalvm.poc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;

@RestController
public class ReflectionController {

    @GetMapping("/reflection")
    public String reflectionTest(@RequestParam String className) throws Exception {

        Class<?> clazz = Class.forName(className);

        Constructor<?> constructor = clazz.getConstructor();

        Object object = constructor.newInstance();

        return "Reflection worked! Created: " + object.getClass().getName();
    }
}