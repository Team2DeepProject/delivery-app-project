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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원_가입_테스트() {
        //given
        UserSignupRequest request = new UserSignupRequest("email@gmail.com", "password123!##", "password123!##", "르탄이", "USER");

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User("email@gmail.com", encodedPassword, "르탄이", UserRole.USER);

        given(userRepository.existsByEmail(anyString())).willReturn(false);

        userRepository.save(user);

        //when
        userService.signup(request);

        //then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void 회원_가입_시_비밀번호와_비밀번호확인이_맞지_않으면_에러가_발생한다() {
        //given
        UserSignupRequest request = new UserSignupRequest("email@gmail.com", "password123!##", "password123!%#", "르탄이", "USER");

        //when
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                userService.signup(request));

        //then
        assertEquals("비밀번호와 비밀번호 확인이 같지 않습니다.", exception.getMessage());
    }

    @Test
    void 회원_가입_시_중복된_이메일을_사용하면_에러가_발생한다() {
        //given
        UserSignupRequest request = new UserSignupRequest("email@gmail.com", "password123!##", "password123!##", "르탄이", "USER");

        given(userRepository.existsByEmail(anyString())).willReturn(true);

        //when
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                userService.signup(request));

        //then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void 회원_전체조회_테스트() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        List<User> userList = IntStream.rangeClosed(1, 20).mapToObj(i ->
                new User("email@gmail.com",
                        "123!@#abcde",
                        "userName",
                        UserRole.OWNER)).collect(Collectors.toList());

        Page<User> page = new PageImpl<>(userList.subList(0, 10), pageable, userList.size());

        given(userRepository.findAll(pageable)).willReturn(page);

        // when
        Page<UserResponse> userResponses = userService.findAll(1, 10);

        // then
        assertEquals(2, userResponses.getTotalPages());
        assertEquals(20, userResponses.getTotalElements());
        assertEquals(0, userResponses.getNumber());
        assertEquals(10, userResponses.getSize());
        assertTrue(userResponses.hasNext());
    }

    @Test
    void 로그인_회원_조회_테스트() {
        //given
        Long userId = 1L;
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        UserResponse userResponse = userService.fetchProfile(userId);

        //then
        assertEquals("email@gmail.com", userResponse.getEmail());
    }

    @Test
    void 회원정보_수정_테스트() {
        //given
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        UserUpdateRequest request = new UserUpdateRequest("userName2");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        user.update(request.getUserName());

        userService.updateUserName(1L, request);

        //then
        assertEquals("userName2", user.getUserName());
    }

    @Test
    void 회원_탈퇴_테스트() {
        //given
        Long userId=1L;
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        UserDeleteRequest request = new UserDeleteRequest("123!@#abcde");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(passwordEncoder.matches(anyString(),anyString())).willReturn(true);

        //when
//        user.setUserState(UserState.DELETE);

        userService.deleteUser(userId, request);

        //then
        assertEquals("DELETE", user.getUserState().toString());

    }

    @Test
    void 회원_탈퇴_시_비밀번호가_맞지_않으면_에러가_발생한다() {
        //given
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        UserDeleteRequest request = new UserDeleteRequest("123!@#abcde");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        boolean passwordCheck = passwordEncoder.matches(request.getPassword(), user.getPassword());

        //when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.deleteUser(1L, request);
        });

        //then
        assertFalse(passwordCheck);
        assertEquals("비밀번호가 맞지 않습니다.", exception.getMessage());
    }

    @Test
    void 이메일을_찾을_수_없으면_에러가_발생한다() {
        //given
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.findUserByEmailOrElseThrow("email@gmail.com"));

        //then
        assertEquals("Not Found Email", exception.getMessage());
    }

    @Test
    void 사용자를_찾을_수_없으면_에러가_발생한다() {
        //given
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        //when
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                userService.findUserByIdOrElseThrow(1L));

        //then
        assertEquals("Not Found UserId", exception.getMessage());
    }


}
