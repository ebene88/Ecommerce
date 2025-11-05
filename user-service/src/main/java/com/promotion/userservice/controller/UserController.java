package com.promotion.userservice.controller;

import com.promotion.userservice.dto.UserResponse;
import com.promotion.userservice.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final KeycloakUserService keycloakUserService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserResponse createUser(@RequestParam String username,
                             @RequestParam String email,
                             @RequestParam String firstName,
                             @RequestParam String lastName,
                             @RequestParam String password,
                             @RequestPart(value = "profileImageUrl", required = false) MultipartFile profileImageUrl) {


        return keycloakUserService.createUser(username, email, firstName, lastName, password, profileImageUrl );
       
    }

    @PutMapping("/{userId}/attribute")
    public void addAttribute(@PathVariable String userId,
                             @RequestParam String key,
                             @RequestParam String value) {
        keycloakUserService.addCustomAttribute(userId, key, value);
    }


    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable String id) {
        return keycloakUserService.getUserById(id);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return keycloakUserService.getAllUsers();
    }


}
