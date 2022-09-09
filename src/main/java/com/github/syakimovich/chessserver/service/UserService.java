package com.github.syakimovich.chessserver.service;

import com.github.syakimovich.chessserver.entities.User;
import com.github.syakimovich.chessserver.repositories.UserRepository;
import com.github.syakimovich.chessserver.user.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    public static final String USER_ROLE = "USER";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsernameOrThrowException(username);
        return new UserDetailsImpl(user.getUsername(), user.getPassword(), user.getRole());
    }

    @Transactional(readOnly = false)
    public void createUser(String username, String password) {
        userRepository.save(new User(username, passwordEncoder.encode(password), USER_ROLE, true));
    }
}
