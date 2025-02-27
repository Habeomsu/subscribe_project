package main.FcmWIthAuth.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequestDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto{
        @NotNull
        private String username;
        @NotNull
        private String password;
        @NotNull
        private String fcmToken;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LogoutDto{
        @NotNull
        private String fcmToken;
    }
}
