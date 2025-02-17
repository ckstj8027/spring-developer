package me.chan.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.chan.springbootdeveloper.domain.User;
import me.chan.springbootdeveloper.dto.AddUserRequest;
import me.chan.springbootdeveloper.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {


    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                // 패스 워드를 저장할 때  Spring Security에서 제공하는 설정하며 패스워드 인코딩더로 등록한 빈을 사용해서 암호화 한 후에 저장합니다
                .password(encoder.encode(dto.getPassword()))
                .build()).getId();

    }

    public User findById(Long userId){
        return userRepository.findById(userId).orElseThrow( () -> new IllegalArgumentException("Unexpected user" ) );

    }
    // oauth 에서 제공하는 이메일은 유일 값이므로 이 메서드를 사용해 유저를 찾을 수 있습니다
    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new IllegalArgumentException("Unexpected user"));
    }


}
