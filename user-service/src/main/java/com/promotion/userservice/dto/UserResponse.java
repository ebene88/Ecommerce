

package com.promotion.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private String id;
    private String username;   // or firstName + lastName
    private String firstName;
    private String lastName;
    private String email;
    private String profileImageUrl;
}
