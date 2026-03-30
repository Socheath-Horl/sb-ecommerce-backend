package com.ecommerce.project.service.interfaces;

import com.ecommerce.project.dtos.AuthenticationResult;
import com.ecommerce.project.dtos.PaginationResponseDto;
import com.ecommerce.project.dtos.UserDto;
import com.ecommerce.project.model.User;
import com.ecommerce.project.security.request.LoginRequest;
import com.ecommerce.project.security.request.SignupRequest;
import com.ecommerce.project.security.response.MessageResponse;
import com.ecommerce.project.security.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface IAuthService {
    AuthenticationResult login(LoginRequest loginRequest);

    ResponseEntity<MessageResponse> register(SignupRequest signupRequest);

    UserInfoResponse getCurrentUserDetails(Authentication authentication);

    ResponseCookie logoutUser();

    PaginationResponseDto<UserDto, User> getAllSellers(HttpServletRequest request, Pageable pageable);
}
