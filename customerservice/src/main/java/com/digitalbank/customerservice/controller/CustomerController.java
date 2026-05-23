package com.digitalbank.customerservice.controller;


import com.digitalbank.customerservice.dto.*;
import com.digitalbank.customerservice.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customers/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service is up");
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerCreatedResponse> createCustomer(@Valid @RequestBody CustomerRequest request) {
        CustomerCreatedResponse body = customerService.create(request);
        URI loc = URI.create("/api/v1/customers/" + body.getExternalId());
        return ResponseEntity.created(loc).body(body);
    }

    @PreAuthorize("hasAuthority('SCOPE_admin:users.write')")
    @PatchMapping("/customers/{id}/kyc-status")
    public ResponseEntity<Void> updateKycStatus(@PathVariable String id, @RequestBody UpdateKycStatusRequest request) {
        Integer newVersion = customerService.updateKycStatus(id, request.getKycStatus());
        return ResponseEntity.noContent().eTag("\"" + newVersion + "\"").build();
    }

    @GetMapping("/customers/{externalId}")
    public ResponseEntity<CustomerResponse> getCustomerByExternalId(@PathVariable String externalId) {
        CustomerResponse dto = customerService.getByExternalId(externalId);
        return ResponseEntity.ok()
                .eTag("\"" + dto.getVersion() + "\"")  // RFC: ETag is a quoted string
                .body(dto);
    }

    @GetMapping("/customers/exists")
    public ResponseEntity<Boolean> existsByEmail(@RequestParam String email) {
        //return ResponseEntity.ok(service.existsByEmail(email));
        return ResponseEntity.ok(customerService.existsByEmail(email));

    }

    @GetMapping("/customers/{externalId}/exists")
    public boolean exists(@PathVariable String externalId) {
        return customerService.exists(externalId);
    }

    @PatchMapping("/customers/{id}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable String id,
            @RequestHeader(name = "If-Match", required = true) String ifMatch,
            @RequestBody UpdateCustomerRequest request
    ) {
        Integer expected = parseIfMatch(ifMatch);
        Integer newVersion = customerService.updateCustomer(id, request, expected);

        return ResponseEntity.ok()
                .eTag("\"" + newVersion + "\"")  // RFC: ETag is a quoted string
                .build();
    }

    private Integer parseIfMatch(String ifMatch) {
        if (null == ifMatch || ifMatch.isBlank()) return null;
        // Accept bare numbers (e.g. 3) or quoted ("3")
        String v = ifMatch.replace("\"", "").trim();
        return Integer.valueOf(v);
    }
}
