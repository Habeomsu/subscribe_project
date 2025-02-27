package main.FcmWIthAuth.user.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.code.status.SuccessStatus;
import main.FcmWIthAuth.user.entity.Refresh;
import main.FcmWIthAuth.user.repository.RefreshRepository;
import main.FcmWIthAuth.user.security.JWTUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ReissueController(JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Operation(summary = "토큰 재발급 API", description = "POST 쿠키에 refresh토큰 포함")
    @PostMapping("/auth/reissue")
    public ApiResult<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        // 쿠키가 null인지 확인
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }
        if (refresh == null) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // response status code
            return ApiResult.onFailure(ErrorStatus._NOTFOUND_REFRESH_TOKEN.getCode(),
                    ErrorStatus._NOTFOUND_REFRESH_TOKEN.getMessage(), null);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResult.onFailure(ErrorStatus._EXFIRED_REFRESH_TOKEN.getCode(),
                    ErrorStatus._EXFIRED_REFRESH_TOKEN.getMessage(), null);
        } catch (SignatureException e) { // 서명 오류 처리

            return ApiResult.onFailure(ErrorStatus._REFRESH_TOKEN_SIGNATURE_ERROR.getCode(),
                    ErrorStatus._REFRESH_TOKEN_SIGNATURE_ERROR.getMessage(), null);

        } catch (JwtException e) {
            // 기타 JWT 관련 오류 처리
            return ApiResult.onFailure(ErrorStatus._INVALID_REFRESH_TOKEN.getCode(),
                    ErrorStatus._INVALID_REFRESH_TOKEN.getMessage(), null);

        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            // response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return ApiResult.onFailure(ErrorStatus._INVALID_REFRESH_TOKEN.getCode(),
                    ErrorStatus._INVALID_REFRESH_TOKEN.getMessage(), null);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // make new JWT
        String newAccess = jwtUtil.createJwt("access", username, role, 6000000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshRepository.deleteByUsername(username);
        addRefresh(username, newRefresh, 86400000L);

        // response
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return ApiResult.onSuccess(SuccessStatus._OK);

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefresh(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refresh_data = Refresh.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refresh_data);
    }

}
