package com.agency.management.login.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.login.dto.request.ForgotPasswordRequestDto;
import com.agency.management.login.dto.request.LoginRequestDto;
import com.agency.management.login.dto.response.LoginUserResponseDto;
import com.agency.management.login.service.LoginService;
import com.agency.management.masters.entity.Users;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) {
        var response = new ApiResponse<>();

        if(loginRequestDto.getUsername() != null || loginRequestDto.getPassword() != null ){

            Optional<Users> getData = userRepository.findByUserName(loginRequestDto.getUsername());
            if(!getData.isEmpty() && (!getData.get().getIsDelete())) {
                if (passwordEncoder.matches(loginRequestDto.getPassword(), getData.get().getPassword())) {

                    String token = jwtUtils.generateToken(getData.get().getUserName(), getData.get().getId());

                    //for return the login user response
                    LoginUserResponseDto userResponseDto = new LoginUserResponseDto();
                    userResponseDto.setUserName(getData.get().getFirstName() + " " + getData.get().getLastName());
                    userResponseDto.setUserId(getData.get().getId());
                    userResponseDto.setToken(token);
                    //String rolling = userRepository.getUserTypeByUsername(loginRequestDto.getUsername());
                    //System.out.printf("rooling : " + rolling);
                    userResponseDto.setRole(userRepository.getUserTypeByUsername(loginRequestDto.getUsername()));
                    //userResponseDto.setRole("Admin");
                    response.responseMethod(HttpStatus.OK.value(), "Login successfully", userResponseDto, null);
                }

                else{
                    response.responseMethod(HttpStatus.UNAUTHORIZED.value(),  "Invalid Credentials", null, null);
                }
            }

            else{
                response.responseMethod(HttpStatus.UNAUTHORIZED.value(),  "Invalid Credentials", null, null);
            }
        }else{
            response.responseMethod(HttpStatus.UNAUTHORIZED.value(), "Invalid Credentials", null, null);
        }
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequestDto requestDto) {
        var response = new ApiResponse<>();

        Optional<Users> userOptional = userRepository.findByUserName(requestDto.getUsername());

        if (userOptional.isPresent() && !userOptional.get().getIsDelete()) {
            Users user = userOptional.get();
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            userRepository.save(user);

            response.responseMethod(HttpStatus.OK.value(), "Password reset successfully", null, null);
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "User not found", null, null);
        }

        return ResponseEntity.ok(response);
    }

}
