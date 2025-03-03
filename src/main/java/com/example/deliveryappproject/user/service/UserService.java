package com.example.deliveryappproject.user.service;


import com.example.deliveryappproject.user.Exception.InvalidRequestException;
import com.example.deliveryappproject.user.config.PasswordEncoder;
import com.example.deliveryappproject.user.dto.UserResponseDto;
import com.example.deliveryappproject.user.dto.UserSignupRequestDto;
import com.example.deliveryappproject.user.dto.UserSignupResponseDto;
import com.example.deliveryappproject.user.entity.User;
import com.example.deliveryappproject.user.entity.UserRole;
import com.example.deliveryappproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto signupRequestDto) {

        if(!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())){
            throw new InvalidRequestException("비밀번호를 맞게 입력해주세요.");
        }

        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        UserRole userRole = UserRole.of(signupRequestDto.getUserRole());

        User newUser = new User(signupRequestDto.getEmail(), encodedPassword, signupRequestDto.getUserName(), userRole);

        User savedUser = userRepository.save(newUser);

        //     String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        return new UserSignupResponseDto(
                savedUser.getUserId(),
                savedUser.getEmail(),
                savedUser.getUserName(),
                savedUser.getUserRole().toString(),
                savedUser.getPoint());
    }

    @Transactional(readOnly=true)
    public List<UserResponseDto> findAll() {
        List<User> user = userRepository.findAll();
        List<UserResponseDto> users = new ArrayList<>();
        for (User u : user) {
            users.add ( new UserResponseDto(u.getUserId(),
                    u.getEmail(),
                    u.getUserName(),
                    u.getUserRole().toString(),
                    u.getPoint()));
        }
        return users;
    }

}

