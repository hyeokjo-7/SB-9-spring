package com.example.spring.service;

import com.example.spring.repository.MenuRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

  private final MenuRepository menuRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void writeAuditMenu(Long categoryId) {
    log.info("[Audit] tx active={}, name={}",
        TransactionSynchronizationManager.isActualTransactionActive(),
        TransactionSynchronizationManager.getCurrentTransactionName());

    menuRepository.updatePrice(1L, 9999);

    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override public void afterCompletion(int status) {
        log.info("[Audit] END = {}", status == STATUS_COMMITTED ? "COMMIT" : "ROLLBACK");
      }
    });
  }
}
