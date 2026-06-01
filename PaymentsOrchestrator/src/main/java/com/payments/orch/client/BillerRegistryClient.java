package com.payments.orch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="registry", url="${biller.service.url}")
public interface BillerRegistryClient {
  @GetMapping("/api/v1/billers/{billerId}/active")
  boolean isActive(@PathVariable("billerId") String referenceNumber);
}