package com.utility.agency.security;

import com.agency.management.security.JwtUtils;
import com.agency.management.security.filter.AuthTokenFilter;
import com.agency.management.security.impl.AgencyUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthTokenFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AgencyUserDetails userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void testDoFilterInternal_ValidToken() throws ServletException, IOException {

        String validToken = "valid.token.here";
        request.addHeader("Authorization", "Bearer " + validToken);

        UserDetails userDetails = User.builder()
                .username("testUser")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        when(jwtUtils.validateJwtToken(validToken)).thenReturn(true);
        when(jwtUtils.getUsernameFromToken(validToken)).thenReturn("testUser");
        when(userDetailsService.loadUserByUsername("testUser")).thenReturn(userDetails);

        authTokenFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_NoToken() throws ServletException, IOException {

        authTokenFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_InvalidTokenFormat() throws ServletException, IOException {

        request.addHeader("Authorization", "InvalidTokenFormat");
        authTokenFilter.doFilter(request, response, filterChain);
        verify(filterChain).doFilter(request, response);
    }


}
