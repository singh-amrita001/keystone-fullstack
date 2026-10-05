package com.zidio.keystone.service;

import com.zidio.keystone.domain.Customer;
import com.zidio.keystone.domain.Site;
import com.zidio.keystone.dto.SiteRequest;
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

    public Site createSite(SiteRequest request) {

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

        return siteRepository.save(site);
    }

    // =====================================================
    // GET SITE BY ID
    // =====================================================

    @Transactional(readOnly = true)
    public Site getSiteById(Long id, String email) {

        Site site = siteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Site not found")
                );

        User user = getUserByEmail(email);

        checkCustomerAccess(
                user,
                site.getCustomer().getId()
        );

        return site;
    }

    // =====================================================
    // GET ALL SITES
    // =====================================================

    @Transactional(readOnly = true)
    public Page<Site> getSites(
            String search,
            Pageable pageable,
            String email) {

        User user = getUserByEmail(email);

        // CUSTOMER can only see sites belonging to their organization
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {

            if (user.getCustomer() == null) {
                throw new AccessDeniedException(
                        "Customer account is not linked to an organization."
                );
            }

            Long customerId = user.getCustomer().getId();

            if (search == null || search.trim().isEmpty()) {

                return siteRepository.findByCustomerId(
                        customerId,
                        pageable
                );
            }

            return siteRepository
                    .findByCustomerIdAndNameContainingIgnoreCase(
                            customerId,
                            search.trim(),
                            pageable
                    );
        }

        // ADMIN / DISPATCHER / other authorized roles
        if (search == null || search.trim().isEmpty()) {

            return siteRepository.findAll(pageable);
        }

        return siteRepository.findByNameContainingIgnoreCase(
                search.trim(),
                pageable
        );
    }

    // =====================================================
    // GET SITES BY CUSTOMER
    // =====================================================

    @Transactional(readOnly = true)
    public Page<Site> getSitesByCustomerId(
            Long customerId,
            Pageable pageable,
            String email) {

        User user = getUserByEmail(email);

        checkCustomerAccess(
                user,
                customerId
        );

        return siteRepository.findByCustomerId(
                customerId,
                pageable
        );
    }

    // =====================================================
    // UPDATE SITE
    // =====================================================

    public Site updateSite(
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

        return siteRepository.save(existingSite);
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
}
