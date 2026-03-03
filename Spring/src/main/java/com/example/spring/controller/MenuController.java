package com.example.spring.controller;


import com.example.spring.controller.dto.MenuResponse;
import com.example.spring.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menus")
@RequiredArgsConstructor
public class MenuController {

  private final MenuService service;

//  public MenuController(MenuService service) {
//    this.service = service;
//  }

  @PostMapping("/tx/propagation")
  public String propagationTest(
      @RequestParam Long menuId,
      @RequestParam int newPrice
  ) {
    service.changePriceWithAuditAndFail(menuId, newPrice);
    return "ok";
  }

  @GetMapping("/{id}")
  public MenuResponse get(@PathVariable Long id) {
    return service.findById(id);
  }

  @GetMapping("/page-search")
  public Page<MenuResponse> pageSearch(
      @RequestParam int minPrice,
      @RequestParam(required = false) String categoryName,
      @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC)
      Pageable pageable
  ) {
    return service.searchMenus(minPrice, categoryName, pageable);
  }

  @GetMapping
  public List<MenuResponse> search(@RequestParam String keyword) {
    return service.search(keyword);
  }

  @GetMapping("/by-category/{categoryId}")
  public List<MenuResponse> byCategory(@PathVariable Long categoryId) {
    return service.findByCategory(categoryId);
  }

  @GetMapping("/entity-graph")
  public List<MenuResponse> searchEntityGraph(@RequestParam String keyword) {
    return service.searchEntityGraph(keyword);
  }

  @GetMapping("/filter")
  public List<MenuResponse> filter(
      @RequestParam String categoryName,
      @RequestParam int minPrice
  ) {
    return service.findExpensiveMenusInCategory(categoryName, minPrice);
  }

//  @GetMapping("/filter2")
//  public List<MenuResponse> filter2(
//      @RequestParam String categoryName,
//      @RequestParam int minPrice
//  ) {
//    return service.findExpensiveMenusInCategory2(categoryName, minPrice);
//  }

  @GetMapping("/pages")
  public Page<MenuResponse> pages(
      @RequestParam String categoryName,
      @RequestParam int minPrice,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "price") String sortBy,
      @RequestParam(defaultValue = "desc") String direction
  ) {
    return service.findMenusPageByCategoryAndMinPrice(categoryName, minPrice, page, size, sortBy,
        direction);
  }


  @GetMapping("/pages2")
  public Page<MenuResponse> pages2(
      @RequestParam String categoryName,
      @RequestParam int minPrice,
      @PageableDefault(size = 10, sort = "price", direction = Sort.Direction.DESC) Pageable pageable
  ) {
    return service.findMenusPageByCategoryAndMinPrice(categoryName, minPrice, pageable);
  }


  @GetMapping("/slice")
  public Slice<MenuResponse> slice(
      @RequestParam String categoryName,
      @RequestParam int minPrice,
      @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
  ) {
    return service.findMenusSliceByCategoryAndMinPrice(categoryName, minPrice, pageable);
  }

  @PostMapping("/tx/increase")
  public void txIncrease(
      @RequestParam String categoryName,
      @RequestParam int delta
  ) {
    service.txIncrease(categoryName, delta);
  }

  @PostMapping("/tx/rollback")
  public void txRollback(
      @RequestParam String categoryName,
      @RequestParam String newMenuName,
      @RequestParam int newMenuPrice,
      @RequestParam int delta
  ) {
    service.txCreateAndIncreaseWithRollback(categoryName, newMenuName, newMenuPrice, delta);
  }
}

