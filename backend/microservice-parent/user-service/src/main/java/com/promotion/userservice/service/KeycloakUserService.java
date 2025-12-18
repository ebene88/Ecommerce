package com.promotion.userservice.service;

import com.promotion.userservice.dto.UserResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final MinioService minioService;

    @Value("${keycloak.realm}")
    private String realm;

    public UserResponse createUser(String username, String email, String firstName, String lastName, String password,   @RequestPart(required = false) MultipartFile profileImage) {


        try {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        Response response = keycloak.realm(realm).users().create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user: " + response.getStatus());
        }

        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Set password
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(credential);

            // 4️⃣ Handle profile image (optional)
            String imageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                imageUrl = minioService.uploadOrReplaceProfileImage(profileImage, userId);
            }
            // 3️⃣ Upload/Replace Profile Image if provided
            return new UserResponse(
                    userId,
                    username,
                    email,
                    firstName,
                    lastName,

                    imageUrl != null
                            ? "User created successfully with profile image"
                            : "User created successfully"
            );
        } catch (Exception e) {
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    public UserResponse getUserById(String userId) {
        try {
            UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            String username = user.getUsername();
            String firstName = user.getFirstName();
            String lastName = user.getLastName();
            String email = user.getEmail();

            String profileImageUrl = minioService.getProfileImageUrl(userId, "png");

            return new UserResponse(userId, username, firstName, lastName,email,   profileImageUrl);
            // or use firstName + lastName instead of username
            // return new UserResponse(userId, firstName + " " + lastName, email);

        } catch (NotFoundException ex) {
            throw new NotFoundException("User not found");
        }

    }

    public List<UserResponse> getAllUsers() {
        UsersResource usersResource = keycloak.realm(realm).users();
        List<UserRepresentation> userList = usersResource.list();

        return userList.stream()
                .map(user -> {
                    String profileImageUrl = minioService.getProfileImageUrl(user.getId(), "png");
                    return  new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        profileImageUrl

                    );
                })
                .collect(Collectors.toList());
    }

    public void addCustomAttribute(String userId, String key, String value) {
        UserRepresentation user = keycloak.realm(realm).users().get(userId).toRepresentation();
        user.singleAttribute(key, value);
        keycloak.realm(realm).users().get(userId).update(user);
    }


    public void updateUserStatus(String userId, boolean active) {
        try {
            UsersResource usersResource = keycloak.realm(realm).users();

            UserRepresentation user = usersResource.get(userId).toRepresentation();
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            user.setEnabled(active); // true = active, false = inactive

            usersResource.get(userId).update(user);

        } catch (Exception e) {
            throw new RuntimeException("Error updating user status: " + e.getMessage(), e);
        }
    }

}
