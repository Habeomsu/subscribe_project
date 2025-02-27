package main.FcmWIthAuth.user.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.user.dto.CustomUserDetails;
import main.FcmWIthAuth.user.entity.Role;
import main.FcmWIthAuth.user.entity.User;
import main.FcmWIthAuth.user.util.JsonResponseUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/auth/reissue")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authorizationHeader.substring(7);

        try {
            jwtUtil.isExpired(accessToken); // JWT 만료 확인
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 처리
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._EXFIRED_ACCESS_TOKEN.getCode(), ErrorStatus._EXFIRED_ACCESS_TOKEN.getMessage(), null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        } catch (SignatureException e) { // 서명 오류 처리
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._ACCESS_TOKEN_SIGNATURE_ERROR.getCode(), "유효하지 않은 JWT 서명입니다.", null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        } catch (JwtException e) {
            // 기타 JWT 관련 오류 처리
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._INVALID_ACCESS_TOKEN.getCode(), "유효하지 않은 JWT입니다.", null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }


        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {

            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._INVALID_ACCESS_TOKEN.getCode(), ErrorStatus._INVALID_ACCESS_TOKEN.getMessage(), null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }


        String username= jwtUtil.getUsername(accessToken);
        String role= jwtUtil.getRole(accessToken);

        User user = User.builder()
                .username(username)
                .role(Role.valueOf(role))
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}
