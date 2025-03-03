package com.example.deliveryappproject.domain.user.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.config.PasswordEncoder;
import com.example.deliveryappproject.domain.user.dto.request.UserDeleteRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserUpdateRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserResponse;
import com.example.deliveryappproject.domain.user.dto.response.UserUpdateResponse;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.regex.Pattern.matches;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(UserSignupRequest signupRequestDto) {

        if (!Objects.equals(signupRequestDto.getPassword(), signupRequestDto.getPasswordCheck())) {     // 추가
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.");
        }

        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new BadRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        UserRole userRole = UserRole.of(signupRequestDto.getUserRole());

        User newUser = new User(signupRequestDto.getEmail(), encodedPassword, signupRequestDto.getUserName(), userRole);

        userRepository.save(newUser);

//        return new UserSignupResponse(
//                newUser.getId(),
//                newUser.getEmail(),
//                newUser.getUserName(),
//                newUser.getUserRole().name(),
//                newUser.getPoint()
//        );
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> user = userRepository.findAll();
        List<UserResponse> users = new ArrayList<>();
        for (User u : user) {
            users.add(new UserResponse(u.getId(),
                    u.getEmail(),
                    u.getUserName(),
                    u.getUserRole().toString(),
                    u.getPoint()));
        }
        return users;
    }

    @Transactional(readOnly = true)
    public User findUserByEmailOrElseThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new BadRequestException("Not Found Email"));

    }

    @Transactional(readOnly = true)
    public User findUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BadRequestException("Not Found UserId"));
    }

    @Transactional
    public UserResponse fetchProfile(Long id) {
        User user = findUserByIdOrElseThrow(id);

        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getUserRole().toString(),
                user.getPoint());
    }

    @Transactional
    public UserUpdateResponse updateUserName(Long id, UserUpdateRequest dto) {
        User user = findUserByIdOrElseThrow(id);

        user.update(dto.getUserName());
        return new UserUpdateResponse(user.getUserName(),
                user.getPoint(),
                user.getUserRole().toString()
        );
    }

    @Transactional
    public void deleteUser(Long id, UserDeleteRequest dto) {
        User user = findUserByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadRequestException("비밀번호가 맞지 않습니다.");

        userRepository.delete(user);
    }
}