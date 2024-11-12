package com.lyj.securitydomo.service;

import com.lyj.securitydomo.domain.User;
import com.lyj.securitydomo.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.lyj.securitydomo.domain.QUser.user;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자 주입
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 사용자 정보를 username으로 불러오는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // Spring Security가 인식할 수 있는 UserDetails 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername()) // 사용자 이름
                .password(user.getPassword()) // 비밀번호
                .roles(user.getRole()) // 권한
                .build();
    }
}