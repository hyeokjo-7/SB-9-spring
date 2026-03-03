package com.example.spring.service;

import com.example.spring.domain.Menu;
import com.example.spring.exception.MenuCheckedException;
import com.example.spring.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

@Service
@RequiredArgsConstructor
public class MenuTxService {

  private final MenuRepository menuRepository;

  @Transactional
  public void updateThenRuntimeRollback(Long menuId, int newPrice) {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    throw new IllegalStateException("런타임 예외 발생 -> 롤백 기대");
  }

  @Transactional
  public void updateThenCheckedCommit(Long menuId, int newPrice) throws MenuCheckedException {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    throw new MenuCheckedException("체크 예외 발생 -> 기본 정책이면 커밋될 수 있음");
  }

  @Transactional(rollbackFor = MenuCheckedException.class)
  public void updateThenCheckedRollback(Long menuId, int newPrice) throws MenuCheckedException {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    throw new MenuCheckedException("체크 예외지만 rollbackFor로 롤백 기대");
  }

  @Transactional(noRollbackFor = IllegalArgumentException.class)
  public void updateThenNoRollbackFor(Long menuId, int newPrice) {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    throw new IllegalArgumentException("기본은 롤백 대상이지만 noRollbackFor로 커밋 기대");
  }


  @Transactional
  public void updateThenSwallowExceptionCommit(Long menuId, int newPrice) {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    try {
      throw new RuntimeException("예외 발생");
    } catch (Exception e) {
    }
  }

  @Transactional
  public void updateThenSwallowButRollback(Long menuId, int newPrice) {
    Menu menu = menuRepository.findById(menuId).orElseThrow();
    menu.changePrice(newPrice);

    try {
      throw new RuntimeException("예외 발생");
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
  }
}
