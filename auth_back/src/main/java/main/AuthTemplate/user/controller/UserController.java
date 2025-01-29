package main.AuthTemplate.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import main.AuthTemplate.apiPayload.ApiResult;
import main.AuthTemplate.user.dto.CustomUserDetails;
import main.AuthTemplate.user.dto.JoinDto;
import main.AuthTemplate.user.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "회원가입 API",description = "POST (username,password)")
    @PostMapping("/join")
    public ApiResult<Void> join(@RequestBody JoinDto joinDto) {
        userService.joinProcess(joinDto);
        return ApiResult.onSuccess();

    }
    @Operation(summary = "탈퇴 API",description = "DELETE ")
    @DeleteMapping("/resign")
    public ApiResult<?> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletResponse response){
        String username = userDetails.getUsername(); // JWT에서 사용자 ID 가져오기
        userService.deleteUser(username);

        // 쿠키 무효화
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0); // 쿠키 만료 시간 설정
        cookie.setPath("/"); // 쿠키 경로 설정
        response.addCookie(cookie); // 쿠키 삭제 요청 추가

        return ApiResult.onSuccess();
    }


}
