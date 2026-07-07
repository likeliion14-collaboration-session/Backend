package com.likelion_collb.domain.user.controller;

import com.likelion_collb.domain.user.dto.request.LoginRequest;
import com.likelion_collb.domain.user.dto.response.LoginResponse;
import com.likelion_collb.domain.user.service.UserService;
import com.likelion_collb.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/login", consumes = "multipart/form-data")
    public ResponseEntity<BaseResponse<LoginResponse>> login(
            @Valid @ModelAttribute LoginRequest request) {

        LoginResponse loginResponse = userService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success(loginResponse));
    }
}
