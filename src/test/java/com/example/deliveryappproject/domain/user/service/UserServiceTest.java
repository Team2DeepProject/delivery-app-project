package com.example.deliveryappproject.domain.user.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.config.PasswordEncoder;
import com.example.deliveryappproject.domain.menu.dto.response.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.store.entity.Store;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원_가입_테스트(){
        //given
        UserSignupRequest signupRequestDto = new UserSignupRequest("email@gmail.com", "password123!##", "password123!##" , "르탄이" , "USER");

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());

        User user= new User("email@gmail.com", encodedPassword, "르탄이" , UserRole.USER );

        given(userRepository.existsByEmail(anyString())).willReturn(false);

        userRepository.save(user);

        //when
        userService.signup(signupRequestDto);

        //then
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void 회원_전체조회_테스트(){
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
    void 로그인_회원_조회_테스트(){
        //given
        Long userId=1L;
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        UserResponse userResponse = userService.fetchProfile(userId);

        //then
        assertEquals("email@gmail.com", userResponse.getEmail());
    }

    @Test
    void 회원정보_수정_테스트(){
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
    void 회원_탈퇴_테스트(){
        //given
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        UserDeleteRequest request = new UserDeleteRequest("123!@#abcde");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        passwordEncoder.matches(request.getPassword(), user.getPassword());

        //when
        user.setUserState(UserState.DELETE);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.deleteUser(1L,request);
        });

        //then
        assertEquals("DELETE", user.getUserState().toString());
    }

    @Test
    void 탈퇴한_회원은_다시_가입할_수_없다(){

    }

    @Test
    void 동일한_이메일은_가입할_수_없다(){

    }


}
