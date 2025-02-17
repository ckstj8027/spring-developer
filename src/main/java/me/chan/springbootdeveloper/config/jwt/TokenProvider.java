package me.chan.springbootdeveloper.config.jwt;
import io.jsonwebtoken.Jws;
import me.chan.springbootdeveloper.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import lombok.RequiredArgsConstructor;

import me.chan.springbootdeveloper.domain.User;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt){
        Date now=new Date();

        return makeToken(new Date(now.getTime()+expiredAt.toMillis()),user);

    }

    private String makeToken(Date expiry,User user){

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id",user.getId())
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey()) // 위조 방지 서명
                .compact(); // JWT 토큰을 문자열 형태로 변환


    }
    //jwt 토큰 유효성 검증
    public boolean validToken(String token){
        try{
            Jwts.parser().setSigningKey(jwtProperties.getSecretKey())  // 비밀값으로 복호화
                    // Jws는 서명된 JWT을 의미
                    .parseClaimsJws(token);

            return true;

        }
        catch (Exception e){  // 복호화 과정에서 에러가 나면 유효하지않는 토큰
            return false;
        }

    }

 // 토큰 기반으로 인증 정보를 가져오는 메서드
    public Authentication getAuthentication(String token){
        Claims claims= getClaims(token);

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User
                //Spring Security에서 제공하는 사용자 정보 모델(~~.userdetails.User)로, 사용자의 아이디와 비밀번호, 권한 정보를 담습니다.
                (claims.getSubject(),"",authorities),token,authorities);

        //UsernamePasswordAuthenticationToken 이 객체는 Spring Security에서 인증 정보로 사용됩니다.

    }
    public Long getUserId(String token){
        Claims claims=getClaims(token);
        return claims.get("id", Long.class);

    }

    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();



    }








}
