package main.AuthTemplate.user.dto;

import lombok.*;

public class UserResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserDto {

        private String username;
        private String role;

    }
}
