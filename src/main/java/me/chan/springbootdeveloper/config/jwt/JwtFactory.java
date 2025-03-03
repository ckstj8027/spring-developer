package me.chan.springbootdeveloper.config.jwt;

import static java.util.Collections.emptyMap;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class JwtFactory {


    private String subject ="test@email.com";
    private Date issuedAt=new Date();
    private Date expiration =new Date(new Date().getTime()+ Duration.ofDays(14).toMillis());
    private Map<String, Object> claims=emptyMap();

    //빌더 패턴을 이용해 설정이 필요한 데이터만 선택 설정
    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims) {
        this.subject = subject;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.claims = claims;
    }

    public static JwtFactory withDefaultValues(){
        return JwtFactory.builder().build();
    }
    public String createToken(JwtProperties jwtProperties){
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam(Header.TYPE,Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256,jwtProperties.getSecretKey())
                .compact();




    }






}
