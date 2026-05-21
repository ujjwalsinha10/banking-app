package com.digitalbank.customerservice.service;

import com.commons.exceptions.*;
import com.digitalbank.customerservice.dto.*;
import com.digitalbank.customerservice.mapper.CustomerMapper;
import com.digitalbank.customerservice.model.Customer;
import com.digitalbank.customerservice.model.KycStatus;
import com.digitalbank.customerservice.repository.CustomerRepository;
import com.digitalbank.customerservice.util.Fingerprints;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;


    public CustomerCreatedResponse create(CustomerRequest request) {
        String externalId = request.getExternalId();
        String email = request.getEmail();
        String fp = Fingerprints.customerCreate(request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhone(), request.getAddress());
        // Fast path: same externalId already present?
        Optional<Customer> byExt = customerRepository.findByExternalId(externalId);
        if (byExt.isPresent()) {
            Customer ex = byExt.get();
            if (fp.equals(ex.getRequestFingerprint())) {
                return customerMapper.toCreateResponse(ex); // idempotent replay
            }
            throw new ConflictException("Same externalId used with different data");
        }
        // Fast path: same Email already present?
        Optional<Customer> byEmail = customerRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            Customer ex = byEmail.get();
            if (fp.equals(ex.getRequestFingerprint())) {
                return customerMapper.toCreateResponse(ex); // idempotent replay
            }
            throw new ConflictException("Same email used with different data");
        }
        // New record attempt
        Customer entity = customerMapper.toEntity(request);
        entity.setExternalId(externalId);
        entity.setActive(false);
        entity.setKycStatus(KycStatus.PENDING);
        entity.setRequestFingerprint(fp);
        Customer saved = customerRepository.saveAndFlush(entity);
        return customerMapper.toCreateResponse(saved);
    }

    public Integer updateKycStatus(String id, String kycStatus) {
        Customer c = customerRepository.findByExternalId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with externalId: " + id));


        if ("VERIFIED".equalsIgnoreCase(kycStatus)) {
//            CustomerRegistrationRequest request =
//                    new CustomerRegistrationRequest(c.getEmail(), "default-password", c.getExternalId());
//            authServiceClient.registerCustomer(request);
            c.setKycStatus(KycStatus.VERIFIED);
            c.setActive(true);
            customerRepository.save(c);
        } else {
            c.setKycStatus(KycStatus.REJECTED);
            customerRepository.save(c);
        }
        return c.getVersion();
    }

    public CustomerResponse getByExternalId(String externalId) {
        Customer customer = customerRepository.findByExternalId(externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer Not Found with ExternalId: " + externalId));
        return customerMapper.toResponse(customer);
    }

    public boolean exists(String externalId) {
        return customerRepository.findByExternalId(externalId).isPresent();
    }

    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    public Integer updateCustomer(String id, UpdateCustomerRequest request, Integer expected) {
        Customer customer = customerRepository.findByExternalId(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer Not Found with ExternalId: " + id));
        if (expected == null) {
            throw new PreconditionRequiredException("If-Match header required");
        }
        if (!expected.equals(customer.getVersion())) {
            throw new VersionMismatchException("Stale Version. Current=" + customer.getVersion() + ", If-Match=" + expected);
        }
        customerMapper.updateCustomerFromRequest(request, customer);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.toResponse(savedCustomer).getVersion();
    }
}
