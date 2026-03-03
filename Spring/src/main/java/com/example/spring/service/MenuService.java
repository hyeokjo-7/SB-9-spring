package com.example.spring.service;


import com.example.spring.controller.dto.MenuResponse;
import com.example.spring.domain.Category;
import com.example.spring.domain.Menu;
import com.example.spring.repository.CategoryRepository;
import com.example.spring.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
//@RequiredArgsConstructor
public class MenuService {

  private final MenuRepository repository;
  private final CategoryRepository categoryRepository;

  public MenuService(MenuRepository repository, CategoryRepository categoryRepository) {
    this.repository = repository;
    this.categoryRepository = categoryRepository;
  }

  @Transactional(readOnly = true)
  public MenuResponse findById(Long id) {
    Menu menu = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("메뉴 없음"));

    return new MenuResponse(
        menu.getId(),
        menu.getName(),
        menu.getPrice(),
        menu.getCategory().getName()
    );
  }

  @Transactional(readOnly = true)
  public List<MenuResponse> search(String keyword) {
    return repository.findByNameContaining(keyword).stream()
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()))
        .toList();
  }

  @Transactional(readOnly = true)
  public List<MenuResponse> findByCategory(Long categoryId) {
    return repository.findByCategoryId(categoryId).stream()
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()))
        .toList();
  }

  @Transactional(readOnly = true)
  public List<MenuResponse> searchEntityGraph(String keyword) {
    return repository.findByNameContaining(keyword).stream()
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()))
        .toList();
  }

  @Transactional(readOnly = true)
  public Page<MenuResponse> searchMenus(
      Integer minPrice,
      String categoryName,
      Pageable pageable
  ) {
    return repository
        .findByMinPriceAndOptionalCategory(minPrice, categoryName, pageable)
        .map(m -> new MenuResponse(
            m.getId(),
            m.getName(),
            m.getPrice(),
            m.getCategory().getName() // ⚠️ LAZY → N+1 (다음 챕터에서 해결)
        ));
  }

  @Transactional(readOnly = true)
  public List<MenuResponse> findExpensiveMenusInCategory(String categoryName, int minPrice) {
    return repository.findByCategoryNameAndMinPrice(categoryName, minPrice).stream()
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()))
        .toList();

  }


  @Transactional(readOnly = true)
  public Page<MenuResponse> findMenusPageByCategoryAndMinPrice(
      String categoryName,
      int minPrice,
      int page,
      int size,
      String sortBy,
      String direction
  ) {
    Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
    Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));

    return repository.findByCategoryNameAndPriceGreaterThanEqual(categoryName, minPrice, pageable)
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()));
  }

  @Transactional(readOnly = true)
  public Page<MenuResponse> findMenusPageByCategoryAndMinPrice(
      String categoryName,
      int minPrice,
      Pageable pageable
  ) {
    return repository.findByCategoryNameAndPriceGreaterThanEqual(categoryName, minPrice, pageable)
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()));
  }

  @Transactional(readOnly = true)
  public Slice<MenuResponse> findMenusSliceByCategoryAndMinPrice(
      String categoryName,
      int minPrice,
      Pageable pageable
  ) {
    return repository.findSliceByCategoryNameAndPriceGreaterThanEqual(categoryName, minPrice, pageable)
        .map(m -> new MenuResponse(m.getId(), m.getName(), m.getPrice(), m.getCategory().getName()));
  }

  @Transactional
  public void txIncrease(String categoryName, int delta) {
    List<Menu> menus = repository.findByCategoryName(categoryName);
    menus.forEach(m -> m.increasePrice(delta));
  }

  @Transactional
  public void txCreateAndIncreaseWithRollback(
      String categoryName,
      String newMenuName,
      int newMenuPrice,
      int delta
  ) {
    Category category = categoryRepository.findByName(categoryName)
        .orElseThrow(() -> new IllegalArgumentException("카테고리 없음"));

    repository.save(new Menu(newMenuName, newMenuPrice, category));

    List<Menu> menus = repository.findByCategoryName(categoryName);
    menus.forEach(m -> m.increasePrice(delta));

    throw new RuntimeException("강제 예외(롤백 확인)");
  }
}

