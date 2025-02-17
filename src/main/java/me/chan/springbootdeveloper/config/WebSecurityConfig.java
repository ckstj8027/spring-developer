//package me.chan.springbootdeveloper.config;
//
//import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
//
//import lombok.RequiredArgsConstructor;
//import me.chan.springbootdeveloper.service.UserDetailService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class WebSecurityConfig {
//
//    private final UserDetailService userService;
//
//
//    //스프링 시큐 리티 기능 비활성화
//    @Bean
//    public WebSecurityCustomizer configure(){
//
//        return (web -> web.ignoring()
//                .requestMatchers(toH2Console())
//                .requestMatchers(new AntPathRequestMatcher("/static/**")));
//
//    }
//
//
//    // 인증 관리자 관련 설정
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http,
//                                                       BCryptPasswordEncoder bCryptPasswordEncoder,UserDetailService userDetailService) throws Exception{
//
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userService);  // UserDetailsService 설정 (여기서 loadUserByUsername 호출)
//        authProvider.setPasswordEncoder(bCryptPasswordEncoder);// 비밀번호 비교를 위한 암호화 방식 설정
//        return new ProviderManager(authProvider);// 압력된 비번을 암호화해서 db에 있는 비번과 비교
//
//    }
//
//
//
//
//    // 특정 http 요청에 대한 웹 기반 보안 구성
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        return http
//                .authorizeRequests(auth -> auth
//                .requestMatchers(
//                        new AntPathRequestMatcher("/login"),
//                        new AntPathRequestMatcher("/signup"),
//                        new AntPathRequestMatcher("/user")
//                ).permitAll()
//                .anyRequest().authenticated())
//        .formLogin(formLogin ->formLogin
//                .loginPage("/login")
//                .defaultSuccessUrl("/articles")
//        )
//                .logout(logout -> logout
//                .logoutSuccessUrl("/login")
//                .invalidateHttpSession(true)
//                )
//        .csrf(AbstractHttpConfigurer::disable).build();
//    }
//
//
//    // 패스워드 인코더로 사용할 빈 등록
//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//
//
//
//}
