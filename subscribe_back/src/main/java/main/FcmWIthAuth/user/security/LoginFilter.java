package main.FcmWIthAuth.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.FcmWIthAuth.apiPayload.ApiResult;
import main.FcmWIthAuth.apiPayload.code.status.ErrorStatus;
import main.FcmWIthAuth.apiPayload.code.status.SuccessStatus;
import main.FcmWIthAuth.fcm.service.TokenTopicService;
import main.FcmWIthAuth.user.dto.CustomUserDetails;
import main.FcmWIthAuth.user.dto.JoinDto;
import main.FcmWIthAuth.user.dto.UserRequestDto;
import main.FcmWIthAuth.user.dto.UserResponseDto;
import main.FcmWIthAuth.user.entity.Refresh;
import main.FcmWIthAuth.user.repository.RefreshRepository;
import main.FcmWIthAuth.user.util.JsonResponseUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final TokenTopicService tokenTopicService;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil,
            RefreshRepository refreshRepository, TokenTopicService tokenTopicService) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.tokenTopicService = tokenTopicService;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {


        UserRequestDto.LoginDto loginDto = new UserRequestDto.LoginDto();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginDto = objectMapper.readValue(messageBody, UserRequestDto.LoginDto.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String username = loginDto.getUsername();
        String password = loginDto.getPassword();
        String fcmToken = loginDto.getFcmToken();

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password,
                null);

        Authentication authentication = authenticationManager.authenticate(authToken);

        tokenTopicService.saveFCMToken(username, fcmToken);
        // token에 담은 검증을 위한 AuthenticationManager로 전달
        return authentication;
    }

    // 로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authentication) throws IOException {

        // UserDetails
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", username, role, 6000000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        addRefresh(username, refresh, 86400000L);

        response.setHeader("Authorization", "Bearer " + access);
        response.addCookie(createCookie("refresh", refresh));

        // ApiResult 생성
        ApiResult<?> apiResult = ApiResult.onSuccess(SuccessStatus._OK, new UserResponseDto.UserDto(username, role));

        JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_OK, apiResult);

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

    // 로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException {
        // ApiResult 생성
        ApiResult<?> apiResult = ApiResult.onFailure(ErrorStatus._USERNAME_NOT_FOUND.getCode(),
                ErrorStatus._USERNAME_NOT_FOUND.getMessage(), null);

        JsonResponseUtil.sendJsonResponse(response, HttpServletResponse.SC_NOT_FOUND, apiResult);

    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        // cookie.setSecure(true);
        // cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
