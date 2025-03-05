package com.example.deliveryappproject.domain.user.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.config.PasswordEncoder;
import com.example.deliveryappproject.domain.user.dto.request.UserDeleteRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserUpdateRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserResponse;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.enums.UserState;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //회원가입
    @Transactional
    public void signup(UserSignupRequest signupRequestDto) {

        if (!Objects.equals(signupRequestDto.getPassword(), signupRequestDto.getPasswordCheck())) {
            throw new BadRequestException("비밀번호와 비밀번호 확인이 같지 않습니다.");
        }

        if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new BadRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        UserRole userRole = UserRole.of(signupRequestDto.getUserRole());

        User newUser = new User(signupRequestDto.getEmail(), encodedPassword, signupRequestDto.getUserName(), userRole);

        userRepository.save(newUser);

    }

    //회원 전체 조회
    @Transactional(readOnly = true)
    public Page<UserResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> user = userRepository.findAll(pageable);
        Page<UserResponse> users = user.map(User ->
           new UserResponse(User.getId(),
                   User.getEmail(),
                   User.getUserName(),
                   User.getUserRole().toString(),
                   User.getPoint()));

        return users;
    }

    //로그인한 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponse fetchProfile(Long id) {
        User user = findUserByIdOrElseThrow(id);

        return new UserResponse(user.getId(),
                user.getEmail(),
                user.getUserName(),
                user.getUserRole().toString(),
                user.getPoint());
    }

    //닉네임 수정
    @Transactional
    public void updateUserName(Long id, UserUpdateRequest dto) {
        User user = findUserByIdOrElseThrow(id);

        user.update(dto.getUserName());
    }

    //회원 탈퇴
    @Transactional
    public void deleteUser(Long id, UserDeleteRequest dto) {
        User user = findUserByIdOrElseThrow(id);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new BadRequestException("비밀번호가 맞지 않습니다.");

        user.setUserState(UserState.DELETE);
    }

    //이메일 없으면 예외처리
    @Transactional(readOnly = true)
    public User findUserByEmailOrElseThrow(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException("Not Found Email"));
    }

    //사용자 없으면 예외처리
    @Transactional(readOnly = true)
    public User findUserByIdOrElseThrow(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not Found UserId"));
    }
}