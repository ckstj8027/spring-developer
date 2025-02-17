package me.chan.springbootdeveloper.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import lombok.RequiredArgsConstructor;
import me.chan.springbootdeveloper.config.jwt.TokenAuthenticationFilter;
import me.chan.springbootdeveloper.config.jwt.TokenProvider;
import me.chan.springbootdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.chan.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import me.chan.springbootdeveloper.config.oauth.OAuth2UserCustomService;
import me.chan.springbootdeveloper.repository.RefreshTokenRepository;
import me.chan.springbootdeveloper.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){
        return (web) ->web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers(
                        new AntPathRequestMatcher("/img/**"),
                        new AntPathRequestMatcher("/css/**"),
                        new AntPathRequestMatcher("/js/**")


                );

    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 토큰 방식응로 인증을 하기 때문에 기존에 사용하던 폼 로그인 세션 비활성화
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(management->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //왜 UsernamePasswordAuthenticationFilter 전에 TokenAuthenticationFilter를 추가해야 할까요?
                //필터 체인은 요청이 들어올 때 순차적으로 실행됩니다. TokenAuthenticationFilter는 요청에 포함된 JWT 토큰을 먼저 확인하여 인증 정보를 확인한 뒤,
                // 인증된 사용자 정보를 SecurityContextHolder에 저장합니다.
                //TokenAuthenticationFilter가 UsernamePasswordAuthenticationFilter 이전에 실행되면,
                // 이후에 실행되는 UsernamePasswordAuthenticationFilter나 다른 필터들이 SecurityContextHolder에서 이미 인증된 사용자의 정보를 사용할 수 있게 됩니다.
                //만약 TokenAuthenticationFilter를 UsernamePasswordAuthenticationFilter 뒤에 추가한다면, JWT 토큰을 사용한 인증을 처리할 기회가 없고,
                // 폼 로그인이 먼저 처리되어 세션 기반 인증으로 작동하게 됩니다.
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(auth->auth
                        .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                          .requestMatchers(new AntPathRequestMatcher("/articles")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()


                        //     .requestMatchers(new An`tPathRequestMatcher("/new-article")).authenticated()  // new-article 경로 인증 요구

                        //      .requestMatchers(new AntPathRequestMatcher("/articles/**")).authenticated()


                        .anyRequest().permitAll())



                .oauth2Login(oauth2->oauth2.loginPage("/login")
                        .authorizationEndpoint(authorizationEndpoint->
                                authorizationEndpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(userInfoEndpoint->userInfoEndpoint.userService(oAuth2UserCustomService))
                        .successHandler(oAuth2SuccessHandler())
                )
                .exceptionHandling(exceptionHandling->exceptionHandling.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                ))
                .build();


    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
        );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository(){
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }






}
