package com.example.spring.controller;


import com.example.spring.domain.Menu;
import com.example.spring.service.MenuService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

  private final MenuService service;

  public MenuController(MenuService service) {
    this.service = service;
  }

  @GetMapping("/{id}")
  public Menu get(@PathVariable Long id) {
    return service.findById(id);
  }

  @GetMapping
  public List<Menu> search(@RequestParam String keyword) {
    return service.search(keyword);
  }
}

