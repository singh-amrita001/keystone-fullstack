package com.zidio.keystone.service;

import com.zidio.keystone.domain.Customer;
import com.zidio.keystone.domain.Site;
import com.zidio.keystone.dto.SiteRequest;
import com.zidio.keystone.dto.SiteResponse;
import com.zidio.keystone.entity.User;
import com.zidio.keystone.repository.CustomerRepository;
import com.zidio.keystone.repository.SiteRepository;
import com.zidio.keystone.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public SiteService(
            SiteRepository siteRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository) {

        this.siteRepository = siteRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    // =====================================================
    // CREATE SITE
    // =====================================================

    public SiteResponse createSite(SiteRequest request) {

        Customer customer = customerRepository.findById(
                request.getCustomerId()
        ).orElseThrow(() ->
                new RuntimeException("Customer not found")
        );

        Site site = new Site();

        site.setName(request.getName());
        site.setAddress(request.getAddress());
        site.setCity(request.getCity());
        site.setState(request.getState());
        site.setPostalCode(request.getPostalCode());
        site.setCustomer(customer);

        Site savedSite = siteRepository.save(site);

        return convertToResponse(savedSite);
    }

    // =====================================================
    // GET SITE BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public SiteResponse getSiteById(
            Long id,
            String email) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Site not found")
                );

        User user = getUserByEmail(email);

        checkCustomerAccess(
                user,
                site.getCustomer().getId()
        );

        return convertToResponse(site);
    }

    // =====================================================
    // GET ALL SITES
    // =====================================================

    @Transactional(readOnly = true)
    public Page<SiteResponse> getSites(
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

            if (search == null || search.trim().isEmpty()) {

                return siteRepository
                        .findByCustomerId(customerId, pageable)
                        .map(this::convertToResponse);
            }

            return siteRepository
                    .findByCustomerIdAndNameContainingIgnoreCase(
                            customerId,
                            search.trim(),
                            pageable
                    )
                    .map(this::convertToResponse);
        }

        if (search == null || search.trim().isEmpty()) {

            return siteRepository
                    .findAll(pageable)
                    .map(this::convertToResponse);
        }

        return siteRepository
                .findByNameContainingIgnoreCase(
                        search.trim(),
                        pageable
                )
                .map(this::convertToResponse);
    }

    // =====================================================
    // GET SITES BY CUSTOMER
    // =====================================================

    @Transactional(readOnly = true)
    public Page<SiteResponse> getSitesByCustomerId(
            Long customerId,
            Pageable pageable,
            String email) {

        User user = getUserByEmail(email);

        checkCustomerAccess(
                user,
                customerId
        );

        return siteRepository
                .findByCustomerId(customerId, pageable)
                .map(this::convertToResponse);
    }

    // =====================================================
    // UPDATE SITE
    // =====================================================

    public SiteResponse updateSite(
            Long id,
            SiteRequest request) {

        Site existingSite = siteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Site not found")
                );

        Customer customer = customerRepository.findById(
                request.getCustomerId()
        ).orElseThrow(() ->
                new RuntimeException("Customer not found")
        );

        existingSite.setName(request.getName());
        existingSite.setAddress(request.getAddress());
        existingSite.setCity(request.getCity());
        existingSite.setState(request.getState());
        existingSite.setPostalCode(request.getPostalCode());
        existingSite.setCustomer(customer);

        Site savedSite = siteRepository.save(existingSite);

        return convertToResponse(savedSite);
    }

    // =====================================================
    // DELETE SITE
    // =====================================================

    public void deleteSite(Long id) {

        Site existingSite = siteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Site not found")
                );

        siteRepository.delete(existingSite);
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
    // CONVERT ENTITY TO RESPONSE DTO
    // =====================================================

    private SiteResponse convertToResponse(Site site) {

        Long customerId = null;
        String customerName = null;

        if (site.getCustomer() != null) {
            customerId = site.getCustomer().getId();
            customerName = site.getCustomer().getName();
        }

        return new SiteResponse(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getCity(),
                site.getState(),
                site.getPostalCode(),
                customerId,
                customerName,
                site.getCreatedAt()
        );
    }
}