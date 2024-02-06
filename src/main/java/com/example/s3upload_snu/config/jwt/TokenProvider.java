package com.example.s3upload_snu.config.jwt;

import com.example.s3upload_snu.user.entity.User;
import com.example.s3upload_snu.user.repository.CustomUserDetails;
import com.example.s3upload_snu.user.repository.UserRepository;
import com.example.s3upload_snu.user.role.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    @Value("${security.jwt.secret}")
    private String secretKey;

    private long accessTokenValidSecond = 500L * 60 * 60; //30분
    private long refreshTokenValidSecond = 1000L * 60 * 60; //1시간

    private final CustomUserDetails customUserDetails;
    private final UserRepository userRepository;

    @PostConstruct
    protected void init(){
        secretKey  = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     *
     * @param email
     * @param accessTokenValidSecond
     * @param role
     * @return
     * Jwt 토큰을 빌드
     */

    private String getString(String email, Long accessTokenValidSecond, Role role){

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);
        Date now = new Date();
        Date accessValidate = new Date(now.getTime() + accessTokenValidSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setExpiration(accessValidate)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     *
     * @param email
     * @return accessToken
     */
    public String createAccessToken(String email){
        User user = userRepository.findByEmail(email);
        return getString(user.getEmail(), accessTokenValidSecond, user.getRole());
    }
    /**
     *
     * @param email
     * @return refreshToken
     */
    public String createRefreshToken(String email){
        User user = userRepository.findByEmail(email);
        return getString(user.getEmail(), refreshTokenValidSecond, user.getRole());
    }

    /**
     *
     * @param token
     * @return user email
     */
    public String getUserEmail(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     *
     * @param token
     * @return user authorities
     *토큰을 매개 변수로 받아 사용자의 인증 정보를 가져옴
     * 사용자 이름, 비밀번호는 공백 처리 사용자의 권한 정보를 포함하여 UsernamePasswordAuthenticationToken 객체를 생성
     */

    public Authentication getAuthentication(String token){
        UserDetails userDetails = customUserDetails.loadUserByUsername(this.getUserEmail(token));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "",userDetails.getAuthorities());
        log.info("Authentication authorities: " + authentication.getAuthorities());
        return authentication;
    }

    public String resolveToken(HttpServletRequest request){return request.getHeader("access_token");}

    /**
     *
     * @param jwtToken
     * @apiNote  토큰의 유효성 검사
     */
    public boolean validateToken(String jwtToken){
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken)
                    .getBody();
            Date now = new Date();
            return claims.getExpiration().after(now);
        }catch (Exception e){
            return false;
        }
    }

    /**
     *
     * @param token
     * @apiNote 토큰에서 이메일 추출
     */
    public String getEmailBytoken(String token) {

        // JWT 토큰을 디코딩하여 페이로드를 얻기
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        // "userId" 클레임의 값을 얻기
        return claims.isEmpty() ? null : claims.get("sub", String.class);
    }
}
