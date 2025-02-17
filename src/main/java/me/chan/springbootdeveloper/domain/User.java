package me.chan.springbootdeveloper.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
@Table(name="users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class User implements UserDetails {
    @Column(name="id",updatable = false)
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="email",nullable = false,unique = true)
    private String email;

    @Column(name="password")
    private String password;


    @Column(name="nickname",unique=true)
    private String nickname;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public User update(String nickname){
        this.nickname=nickname;
        return this;
    }










    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 권한 반환
        return List.of(new SimpleGrantedAuthority("user"));
    }
    @Override  // 사용자의 id 를 반환 여기서는 이메일
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }


    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;  // 트루면 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() { // 계정이 잠금 되지않음 ?
        return true;  // 응 잠금 안됨
    }

    @Override
    public boolean isCredentialsNonExpired() { // 페스만료가 잠금되지 않았지?
        return true;   //  ㅇㅇ 만료 안됨
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true;
    }
}
