package me.chan.springbootdeveloper.config.oauth;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chan.springbootdeveloper.config.jwt.TokenProvider;
import me.chan.springbootdeveloper.domain.RefreshToken;
import me.chan.springbootdeveloper.domain.User;
import me.chan.springbootdeveloper.repository.RefreshTokenRepository;
import me.chan.springbootdeveloper.service.UserService;
import me.chan.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Component

@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler{
    public static final String REFRESH_TOKEN_COOKIE_NAME="refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION=Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION=Duration.ofDays(1);
    public static final String REDIRECT_PATH="/articles";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //인증된 사용자의 주체(Principal), 즉 현재 인증된 사용자의 정보를 반환합니다.
        //Principal은 보통 사용자 이름, 이메일, 또는 사용자 ID와 같은 사용자 식별 정보입니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuth2AuthenticationToken oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String registrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

        System.out.println("registrationId = " + registrationId);


        User user =null;
        if ("kakao".equals(registrationId)) {
            user =getUserInfo(oAuth2User);

        } else if ("google".equals(registrationId)) {
            // 구글에서 받은 사용자 정보 처리
           user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        } else {
            log.error("알 수 없는 OAuth2 제공자입니다.");
        }

////////////////


        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);

        saveRefreshToken(user.getId(),refreshToken);

        addRefreshTokenToCookie(request,response,refreshToken);
        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        clearAuthenticationAttributes(request,response);

       getRedirectStrategy().sendRedirect(request,response,targetUrl);


    }

    private User getUserInfo(OAuth2User oAuth2User) {



        Map<String, Object> attributes = oAuth2User.getAttributes();


        // 카카오 계정 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount == null) {
            log.error("카카오 계정 정보가 없습니다.");
            throw new RuntimeException("카카오 계정 정보를 찾을 수 없습니다.");
        }

        // 이메일 정보 추출
        String email = (String) kakaoAccount.get("email");

        return userService.findByEmail(email );
    }









    //생성된 리프레시 토큰을 전달받아 데이터베이스에 저장
    private void saveRefreshToken(Long userId,String newRefreshToken){
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepository.save(refreshToken);


    }
    // 생성된 리프레시 토큰을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request,HttpServletResponse response,String refreshToken){
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request,response,REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response,REFRESH_TOKEN_COOKIE_NAME,refreshToken,cookieMaxAge);
    }

    // 인증관련 설정값 , 쿠키제거
    private void clearAuthenticationAttributes(HttpServletRequest request,HttpServletResponse response){
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request,response);

    }

    // 엑세스 토큰을 패스에 추가
    private String getTargetUrl(String token){
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token",token)
                .build()
                .toUriString();


    }



}