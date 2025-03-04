package com.example.deliveryappproject.domain.auth.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.config.PasswordEncoder;
import com.example.deliveryappproject.domain.auth.dto.request.AuthLoginRequest;
import com.example.deliveryappproject.domain.auth.dto.response.AuthTokenResponse;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @InjectMocks
    private AuthService authService;

    /* login */
    @Test
    void signin에서_성공적으로_로그인을_할_수_있는가() {
        // given
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        User user = new User(email, encodedPassword, "nickname", UserRole.USER);
        AuthLoginRequest authLoginRequest = new AuthLoginRequest(email, password);

        given(userService.findUserByEmailOrElseThrow(any(String.class))).willReturn(user);
        given(passwordEncoder.matches(authLoginRequest.getPassword(), user.getPassword())).willReturn(true);
        given(tokenService.createAccessToken(any(User.class))).willReturn(accessToken);
        given(tokenService.createRefreshToken(any(User.class))).willReturn(refreshToken);

        // when
        AuthTokenResponse response = authService.login(authLoginRequest);

        // then
        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshToken, response.getRefreshToken());
    }

    @Test
    void login에서_비밀번호가_일치하지_않을_떄_UnauthorizedException를_출력할_수_있는가() {
        //given
        String email = "test@example.com";
        String wrongPassword = "password123";
        String encodedPassword = "encodedPassword";

        User user = new User(email, encodedPassword, "nickname", UserRole.USER);
        AuthLoginRequest authLoginRequest = new AuthLoginRequest(email, wrongPassword);

        given(userService.findUserByEmailOrElseThrow(any(String.class))).willReturn(user);
        given(passwordEncoder.matches(authLoginRequest.getPassword(), user.getPassword())).willReturn(false);

        //when & then
        assertThrows(UnauthorizedException.class,
                () -> authService.login(authLoginRequest),
                "잘못된 비밀번호입니다.");
    }

    /* logout */
    @Test
    void logout에서_성공적으로_로그아웃을_할_수_있는가() {
        // given
        Long userId = 1L;
        AuthUser authUser = new AuthUser(userId, "test@example.com", UserRole.USER);

        // when
        authService.logout(authUser);

        // then
        verify(tokenService, times(1)).revokeRefreshToken(userId);
    }

    /* reissueAccessToken */
    @Test
    void reissueAccessToken에서_정상적으로_토큰을_발급받을_수_있는가() {
        // given
        Long userId = 1L;
        String refreshToken = "refresh-token";
        String newAccessToken = "reissued-access-token";
        String newRefreshToken = "reissued-refresh-token";

        User user = new User(userId);

        given(tokenService.reissueToken(any(String.class))).willReturn(user);
        given(tokenService.createAccessToken(any(User.class))).willReturn(newAccessToken);
        given(tokenService.createRefreshToken(any(User.class))).willReturn(newRefreshToken);

        // when
        AuthTokenResponse response = authService.reissueAccessToken(refreshToken);

        // then
        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals(newRefreshToken, response.getRefreshToken());
    }
}
