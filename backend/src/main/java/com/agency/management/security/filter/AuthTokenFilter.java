package com.agency.management.security.filter;

import com.agency.management.security.JwtUtils;
import com.agency.management.security.exception.ExpiredJwtTokenException;
import com.agency.management.security.impl.AgencyUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AgencyUserDetails userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //System.out.println("request " + request.getRequestURI());
        String jwt = request.getHeader("Authorization");
        if ((jwt != null && jwt.startsWith("Bearer "))){
            jwt = jwt.substring(7).trim();

            // Validate the token
            if (jwtUtils.validateJwtToken(jwt)) {
                try {
                    String username = jwtUtils.getUsernameFromToken(jwt);
                    logger.info("Filter username " + username);
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                }
                catch (ExpiredJwtTokenException ex){
                    logger.error("JWT token is expired: {}"+ ex.getMessage());
                    throw new ExpiredJwtTokenException("Jwt token is expired");
                }
                catch (Exception e) {
                    logger.error("Cannot set user authentication: {}", e);
                }
            }
        }
        filterChain.doFilter(request, response);
    }


}