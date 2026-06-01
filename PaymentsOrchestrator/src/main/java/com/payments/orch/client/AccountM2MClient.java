package com.payments.orch.client;

import com.account.dto.AccountResponse;
import com.account.dto.HoldResponse;
import com.account.dto.PostingRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "account-servicem2m",
        url = "${account.service.url}",         
        configuration = com.payments.orch.security.FeignM2MOAuth2Config.class
)
public interface AccountM2MClient {

   
	  @PostMapping("/api/v1/accounts/{accountId}/holds/{holdId}/release")
	  HoldResponse releaseHold(
	      @PathVariable("accountId") UUID accountId,
	      @PathVariable("holdId") UUID holdId
	  );
    
    
	  @PostMapping("/api/v1/accounts/{id}/debit")
	  AccountResponse debit(
	      @PathVariable("id") UUID id,
	      @RequestHeader(name = "If-Match", required = false) String ifMatch,
	      @Valid @RequestBody PostingRequest request
	  );
}