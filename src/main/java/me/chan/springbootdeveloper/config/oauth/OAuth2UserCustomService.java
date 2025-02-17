package me.chan.springbootdeveloper.config.oauth;

import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chan.springbootdeveloper.domain.User;
import me.chan.springbootdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OAuth2UserCustomService  extends DefaultOAuth2UserService {

    private final UserRepository userRepository;



    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        // 기본적인 사용자 정보 로드
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        // OAuth2 인증 제공자 확인 (카카오, 구글 등)
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 로그인 제공자: {}", registrationId);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        log.info("OAuth2User Attributes: {}", attributes);

        // 사용자 정보를 가져오기 전에 필요한 추가 처리
        if ("kakao".equals(registrationId)) {
       //     oAuth2User =getKakaoUserInfo(oAuth2User);
            saveOrUpdateK(oAuth2User);
        } else if ("google".equals(registrationId)) {
        //    oAuth2User = getGoogleUserInfo(oAuth2User);
            saveOrUpdateG(oAuth2User);
        }


        // 다른 OAuth2 인증 제공자 처리 (필요시 추가)
        return oAuth2User;
    }

   




    // 유저가 있으면 업데이트 없으면 유저 생성
    private User saveOrUpdateK(OAuth2User oAuth2User){

        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");


        String email = (String) kakaoAccount.get("email");

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String name = (String) profile.get("nickname");


        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);

    }

    private User saveOrUpdateG(OAuth2User oAuth2User){

        Map<String, Object> attributes = oAuth2User.getAttributes();



         String email = (String) attributes.get("email");
        String name=(String) attributes.get("name");

        System.out.println("email = " + email);

        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);

    }









}
