package com.example.spring.service;


import com.example.spring.domain.Menu;
import com.example.spring.repository.MenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuService {

  private final MenuRepository repository;

  public MenuService(MenuRepository repository) {
    this.repository = repository;
  }

  @Transactional(readOnly = true)
  public Menu findById(Long id) {
    return repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("메뉴 없음"));
  }

  @Transactional(readOnly = true)
  public List<Menu> search(String keyword) {
    return repository.findByNameContaining(keyword);
  }
}

