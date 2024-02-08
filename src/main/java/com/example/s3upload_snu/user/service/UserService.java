package com.example.s3upload_snu.user.service;

import com.example.s3upload_snu.config.jwt.TokenProvider;
import com.example.s3upload_snu.user.dto.LoginDto;
import com.example.s3upload_snu.user.dto.SignUpDto;
import com.example.s3upload_snu.user.entity.User;
import com.example.s3upload_snu.user.repository.UserRepository;
import com.example.s3upload_snu.user.role.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String,String > redisTemplate;


    public ResponseEntity<?>signup(SignUpDto sign){
        String email = sign.getEmail();
        String password = sign.getPassword();

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

    public ResponseEntity<?>login(LoginDto loginDto, HttpServletResponse httpServletResponse) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String accessToken = tokenProvider.createAccessToken(email);
            String refreshToken = tokenProvider.createRefreshToken(email);

            redisTemplate.opsForValue().set(email, accessToken, Duration.ofSeconds(1800));
            redisTemplate.opsForValue().set("RF: "+email, refreshToken, Duration.ofHours(1L));

            httpServletResponse.addCookie(new Cookie("access_token", accessToken));
            httpServletResponse.addCookie(new Cookie("refresh_token", refreshToken));

            Map<String, String> response = new HashMap<>();
            response.put("message", "로그인 되었습니다");
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("http_status", HttpStatus.OK.toString());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "아이디 또는 패스워드가 틀렸습니다");
            response.put("http_status", HttpStatus.NOT_ACCEPTABLE.toString());
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "가입되지 않은 회원입니다");
            response.put("http_status", HttpStatus.NOT_FOUND.toString());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> response = new HashMap<>();
            response.put("message", "알 수 없는 오류가 발생했습니다");
            response.put("http_status", HttpStatus.INTERNAL_SERVER_ERROR.toString());
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(response);

        }
    }

    public ResponseEntity<?> logout(String token){
        redisTemplate.delete(tokenProvider.getEmailBytoken(token));
        redisTemplate.delete("RF: " + tokenProvider.getEmailBytoken(token));
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

}
