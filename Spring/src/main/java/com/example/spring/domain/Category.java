package com.example.spring.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Getter
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 30)
  private String name;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "category")
  private List<Menu> menus = new ArrayList<>();

  protected Category() {}

  public Category(String name, LocalDateTime createdAt) {
    this.name = name;
    this.createdAt = createdAt;
  }
}
