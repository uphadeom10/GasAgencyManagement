package com.utility.agency.security;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.security.impl.AgencyUserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgencyUserDetailsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AgencyUserDetails agencyUserDetails;

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Setup
        when(userRepository.findByUserNameAndIsActiveTrue("unknownUser")).thenReturn(Optional.empty());

        // Test & Verify
        assertThrows(UsernameNotFoundException.class, () -> {
            agencyUserDetails.loadUserByUsername("unknownUser");
        });
    }
}
