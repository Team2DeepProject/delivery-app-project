package com.example.deliveryappproject.domain.auth.service;

import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.config.JwtUtil;
import com.example.deliveryappproject.domain.auth.entity.RefreshToken;
import com.example.deliveryappproject.domain.auth.repository.RefreshTokenRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.deliveryappproject.domain.auth.enums.TokenStatus.INVALIDATED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private TokenService tokenService;

    /* createAccessToken */
    @Test
    void createAccessToken에서_정상적으로_AccessToken을_생성할_수_있는가() {
        // given
        Long userId = 1L;
        String accessToken = "access-token";

        User user = new User(userId);

        given(jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn(accessToken);

        // when
        String createdToken = tokenService.createAccessToken(user);

        // then
        assertEquals(accessToken, createdToken);
    }

    /* createRefreshToken */
    @Test
    void createRefreshToken에서_정상적으로_RefreshToken을_생성할_수_있는가() {
        // given
        Long userId = 1L;
        User user = new User(userId);
        RefreshToken mockRefreshToken = new RefreshToken(userId);

        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(mockRefreshToken);

        // when
        String createdRefreshToken = tokenService.createRefreshToken(user);

        // then
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        assertEquals(mockRefreshToken.getToken(), createdRefreshToken);
    }

    /* revokeRefreshToken */
    @Test
    void revokeRefreshToken에_정상적으로_RefreshToken을_만료할_수_있는가() {
        // given
        Long userId = 1L;
        RefreshToken mockRefreshToken = mock(RefreshToken.class);

        given(refreshTokenRepository.findById(anyLong())).willReturn(Optional.of(mockRefreshToken));

        // when
        tokenService.revokeRefreshToken(userId);

        // then
        verify(refreshTokenRepository, times(1)).findById(userId);
        verify(mockRefreshToken, times(1)).updateTokenStatus(INVALIDATED);
    }

    @Test
    void revokeRefreshToken에_토큰을_찾지_못할_시_NotFoundException를_던지는가() {
        // given
        Long userId = 1L;

        given(refreshTokenRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> tokenService.revokeRefreshToken(userId),
                "Not Found Token");
    }

    /* reissueToken */
    @Test
    void reissueToken에서_정상적으로_토큰의_유효성을_검사할_수_있는가() {
        // given
        Long userId = 1L;
        User mockUser = new User(userId);
        String refreshToken = "refresh-token";

        RefreshToken mockRefreshToken = mock(RefreshToken.class);

        given(refreshTokenRepository.findByToken(any(String.class))).willReturn(Optional.of(mockRefreshToken));
        given(userService.findUserByIdOrElseThrow(mockRefreshToken.getUserId())).willReturn(mockUser);

        // when
        User user = tokenService.reissueToken(refreshToken);

        // then
        assertNotNull(user);
        verify(mockRefreshToken, times(1)).updateTokenStatus(INVALIDATED);
    }

    @Test
    void reissueToken에서_토큰이_비활성_상태일_경우_UnauthorizedException을_던지는가() {
        // given
        String refreshToken = "refresh-token";

        RefreshToken mockRefreshToken = mock(RefreshToken.class);

        given(refreshTokenRepository.findByToken(any(String.class))).willReturn(Optional.of(mockRefreshToken));
        given(mockRefreshToken.getTokenStatus()).willReturn(INVALIDATED);

        // when & then
        assertThrows(UnauthorizedException.class,
                () -> tokenService.reissueToken(refreshToken),
                "사용이 만료된 refresh token 입니다.");
    }

    @Test
    void findByTokenOrElseThrow에서_토큰이_없을_시_NotFoundException를_던지는가() {
        //given
        String refreshToken = "refresh-token";

        given(refreshTokenRepository.findByToken(any(String.class))).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> tokenService.reissueToken(refreshToken),
                "Not Found Token");
    }
}