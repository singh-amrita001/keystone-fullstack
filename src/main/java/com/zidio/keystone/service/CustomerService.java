package com.zidio.keystone.service;

import com.zidio.keystone.domain.Customer;
import com.zidio.keystone.dto.CustomerResponse;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.CustomerRepository;
import com.zidio.keystone.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(
            CustomerRepository customerRepository,
            UserRepository userRepository) {

        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // CREATE CUSTOMER
    // ADMIN / DISPATCHER
    // =====================================================

    public Customer createCustomer(Customer customer) {

        return customerRepository.save(customer);
    }

    // =====================================================
    // GET CUSTOMERS
    // CUSTOMER -> ONLY OWN ORGANIZATION
    // OTHER ROLES -> ALL CUSTOMERS
    // =====================================================

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getCustomers(
            String search,
            Pageable pageable,
            String email) {

        User user = getUserByEmail(email);

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

            if (user.getCustomer() == null) {
                throw new AccessDeniedException(
                        "Customer account is not linked to an organization."
                );
            }

            Long customerId = user.getCustomer().getId();

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() ->
                            new RuntimeException("Customer not found")
                    );

            CustomerResponse response =
                    convertToResponse(customer);

            return new PageImpl<>(
                    List.of(response),
                    pageable,
                    1
            );
        }

        Page<Customer> customerPage;

        if (search == null || search.trim().isEmpty()) {

            customerPage = customerRepository.findAll(pageable);

        } else {

            customerPage =
                    customerRepository.findByNameContainingIgnoreCase(
                            search.trim(),
                            pageable
                    );
        }

        return customerPage.map(this::convertToResponse);
    }

    // =====================================================
    // GET CUSTOMER BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(
            Long id,
            String email) {

        User user = getUserByEmail(email);

        checkCustomerAccess(user, id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found")
                );

        return convertToResponse(customer);
    }

    // =====================================================
    // UPDATE CUSTOMER
    // ADMIN / DISPATCHER
    // =====================================================

    public Customer updateCustomer(
            Long id,
            Customer updatedCustomer) {

        Customer existingCustomer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                )
                        );

        existingCustomer.setName(
                updatedCustomer.getName()
        );

        existingCustomer.setEmail(
                updatedCustomer.getEmail()
        );

        existingCustomer.setPhone(
                updatedCustomer.getPhone()
        );

        existingCustomer.setAddress(
                updatedCustomer.getAddress()
        );

        return customerRepository.save(existingCustomer);
    }

    // =====================================================
    // DELETE CUSTOMER
    // ADMIN / DISPATCHER
    // =====================================================

    public void deleteCustomer(Long id) {

        Customer customer =
                customerRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found"
                                )
                        );

        customerRepository.delete(customer);
    }

    // =====================================================
    // FIND LOGGED-IN USER
    // =====================================================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found."
                        )
                );
    }

    // =====================================================
    // CUSTOMER OWNERSHIP CHECK
    // =====================================================

    private void checkCustomerAccess(
            User user,
            Long customerId) {

        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

            if (user.getCustomer() == null) {
                throw new AccessDeniedException(
                        "Customer account is not linked to an organization."
                );
            }

            Long loggedInCustomerId =
                    user.getCustomer().getId();

            if (!loggedInCustomerId.equals(customerId)) {
                throw new AccessDeniedException(
                        "Access denied for this customer."
                );
            }
        }
    }

    // =====================================================
    // CUSTOMER -> RESPONSE DTO
    // =====================================================

    private CustomerResponse convertToResponse(
            Customer customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt()
        );
    }
}
