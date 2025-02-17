package me.chan.springbootdeveloper.config.oauth;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.chan.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.util.WebUtils;

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    // 권한 인증 흐름에서 클라이언트의 요청을 유지하는데 사용하는 AuthorizationRequestRepository 클래스를 구현해 쿠키를 사용해
    //OAuth의 정보를 가져오고 저장하는 로직을 작성하겠습니다.

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME="oauth2_auth_request";
    private final static int COOKIE_EXPIRE_SECONDS=18000;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);

        return CookieUtil.deserialize(cookie,OAuth2AuthorizationRequest.class);


    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
                                         HttpServletResponse response) {


        if(authorizationRequest ==null ){
            removeAuthorizationRequestCookies(request,response);
            return;
        }
        CookieUtil.addCookie(response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,CookieUtil.serialize(authorizationRequest) ,COOKIE_EXPIRE_SECONDS    );


    }

    // 이거 왜 이럼 ? 이러면 loadAuthorizationRequest() 이거랑 removeAuthorizationRequest() 이거랑 똑같은거 아님 ?
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
                                                                 HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request,
                                                  HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }
}
