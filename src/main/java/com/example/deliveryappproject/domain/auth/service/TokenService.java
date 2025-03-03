package com.example.deliveryappproject.domain.auth.service;

import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.config.JwtUtil;
import com.example.deliveryappproject.domain.auth.entity.RefreshToken;
import com.example.deliveryappproject.domain.auth.repository.RefreshTokenRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.deliveryappproject.domain.auth.enums.TokenStatus.INVALIDATED;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /* Access Token 생성 */
    public String createAccessToken(User user) {
        return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    /* Refresh Token 생성 */
    public String createRefreshToken(User user) {
        RefreshToken refreshToken = refreshTokenRepository.save(new RefreshToken(user.getId()));
        return refreshToken.getToken();
    }

    /* Refresh Token 만료 */
    public void revokeRefreshToken(Long userId) {
        RefreshToken refreshToken = findRefreshTokenById(userId);
        refreshToken.updateTokenStatus(INVALIDATED);
    }

    /* Refresh Token 유효성 검사 */
    public User reissueToken(String token) {

        RefreshToken refreshToken = findByTokenOrElseThrow(token);

        if (refreshToken.getTokenStatus() == INVALIDATED) {
            throw new UnauthorizedException("사용이 만료된 refresh token 입니다.");
        }
        refreshToken.updateTokenStatus(INVALIDATED);

        return userService.findUserByIdOrElseThrow(refreshToken.getUserId());
    }

    private RefreshToken findByTokenOrElseThrow(String token) {
        return refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new UnauthorizedException("Not Found Token"));
    }

    private RefreshToken findRefreshTokenById(Long userId) {
        return refreshTokenRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException("해당 유저의 Token이 존재하지 않음."));
    }
}
