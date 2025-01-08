package com.board.board.global.config.jwt;

import com.board.board.global.dto.response.ExceptionResponse;
import com.board.board.global.exception.CommonErrorCode;
import com.board.board.global.exception.RestApiException;
import com.board.board.global.service.MemberDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.board.board.global.exception.CommonErrorCode.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${spring.jwt.token.access-expiration-time}")
    private long accessExpirationTime;

    @Value("${spring.jwt.token.refresh-expiration-time}")
    private long refreshExpirationTime;

    private final MemberDetailsServiceImpl memberDetailsService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final ObjectMapper objectMapper;

    /**
     * Access Token 생성
     */
    public String generateAccessToken(UsernamePasswordAuthenticationToken authenticationToken){
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + accessExpirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    /**
     * Refresh Token 생성
     */
    public void generateRefreshToken(UsernamePasswordAuthenticationToken authenticationToken){
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Claims claims = Jwts.claims().setSubject(authentication.getName());
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + refreshExpirationTime);

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        // 이걸 Redis 에 저장해야 함
        // Key = authentication.getName(), Value = refreshToken
    }



    /**
     * 토큰으로부터 Claims 을 만들고, 이를 통해 User 객체와 Authentication 객체 리턴
     */
    public Authentication getAuthentication(String token) {
        String email = Jwts.parser().
                setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getSubject();

        UserDetails userDetails = memberDetailsService.loadUserByUsername(email);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * JWT 검증
     */
    public boolean validateToken(String token, HttpServletResponse response) throws IOException {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            jwtExceptionHandler(EXPIRED_ACCESS_TOKEN, response);
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            jwtExceptionHandler(INVALID_TOKEN, response);
        }

        return false;
    }

    private void jwtExceptionHandler(CommonErrorCode wrongTokenType, HttpServletResponse response) throws IOException {
        ExceptionResponse errorResultDto = new ExceptionResponse(wrongTokenType.getStatus(),
                wrongTokenType.getStatus().value(),
                wrongTokenType.getMessage());

        response.setStatus(wrongTokenType.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResultDto));
    }
}