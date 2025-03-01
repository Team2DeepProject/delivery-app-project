package com.example.deliveryappproject.user.service;


import com.example.deliveryappproject.user.Exception.InvalidRequestException;
import com.example.deliveryappproject.user.config.PasswordEncoder;
import com.example.deliveryappproject.user.dto.UserSignupRequestDto;
import com.example.deliveryappproject.user.dto.UserSignupResponseDto;
import com.example.deliveryappproject.user.entity.User;
import com.example.deliveryappproject.user.entity.UserRole;
import com.example.deliveryappproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto signupRequestDto) {

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
                    savedUser.getUserRole(),
                    savedUser.getPoint());
        }
    }

