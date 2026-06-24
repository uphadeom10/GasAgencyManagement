package com.agency.management.security.impl;

import com.agency.management.masters.entity.Users;
import com.agency.management.masters.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AgencyUserDetails implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(AgencyUserDetails.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Users user = userRepository.findByUserNameAndIsActiveTrue(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return UserDetailsImpl.build(user); // Ensure UserDetailsImpl is properly implemented
        } catch (UsernameNotFoundException ex) {
            logger.error("User not found: {}", username, ex);
            throw ex; // Re-throw to maintain Spring Security's expected behavior
        }
    }
}