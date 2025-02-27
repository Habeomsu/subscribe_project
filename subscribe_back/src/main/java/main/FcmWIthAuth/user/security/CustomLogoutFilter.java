package main.FcmWIthAuth.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.fcm.service.TokenTopicService;
import main.FcmWIthAuth.user.dto.UserRequestDto;
import main.FcmWIthAuth.user.repository.RefreshRepository;
import main.FcmWIthAuth.user.util.JsonResponseUtil;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final TokenTopicService tokenTopicService;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository,
                               TokenTopicService tokenTopicService) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.tokenTopicService = tokenTopicService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        // 경로가 logout , POST 방식인지 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/auth/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }

        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 요청 본문에서 FCM 토큰을 추출
        ObjectMapper objectMapper = new ObjectMapper();
        UserRequestDto.LogoutDto requestDto = objectMapper.readValue(request.getReader(), UserRequestDto.LogoutDto.class);
        String fcmToken = requestDto.getFcmToken();


        // 쿠키에서 refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();

        // 쿠키가 null인지 확인
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break; // 쿠키를 찾으면 루프 종료
                }
            }
        }

        // refresh null check
        if (refresh == null) {

            // ApiResult 생성
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._NOTFOUND_REFRESH_TOKEN.getCode(),
                    ErrorStatus._NOTFOUND_REFRESH_TOKEN.getMessage(), null);

            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }

        // expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            // ApiResult 생성
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._EXFIRED_REFRESH_TOKEN.getCode(),
                    ErrorStatus._EXFIRED_REFRESH_TOKEN.getMessage(), null);

            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        } catch (SignatureException e) { // 서명 오류 처리
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._REFRESH_TOKEN_SIGNATURE_ERROR.getCode(),
                    "유효하지 않은 REFRESH JWT 서명입니다.", null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        } catch (JwtException e) {
            // 기타 JWT 관련 오류 처리
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._INVALID_REFRESH_TOKEN.getCode(),
                    "유효하지 않은 REFRESH JWT입니다.", null);
            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            // ApiResult 생성
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._INVALID_REFRESH_TOKEN.getCode(),
                    ErrorStatus._INVALID_REFRESH_TOKEN.getMessage(), null);

            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {

            // ApiResult 생성
            ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._NOFOUND_REFRESH_TOKEN.getCode(),
                    ErrorStatus._NOFOUND_REFRESH_TOKEN.getMessage(), null);

            JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, apiResult);
            return;
        }
        String username = jwtUtil.getUsername(refresh);

        tokenTopicService.logoutAndUnsubscribe(fcmToken,username);


        // 로그아웃 진행//Refresh 토큰 DB에서 제거
        // refreshRepository.deleteByRefresh(refresh);
        refreshRepository.deleteByUsername(username);

        // Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        // 로그아웃 성공 응답 설정
        ApiResult<?> successResult = ApiResult.onSuccess();
        JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, successResult);
    }

}
