package main.AuthTemplate.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class JoinDto {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String email;

}
