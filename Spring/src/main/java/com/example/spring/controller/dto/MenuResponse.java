package com.example.spring.controller.dto;

public record MenuResponse(
    Long id,
    String name,
    int price,
    String categoryName
) {}
