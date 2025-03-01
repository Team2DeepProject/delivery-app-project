package com.example.deliveryappproject.domain.auth.service;

import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.config.JwtUtil;
import com.example.deliveryappproject.domain.auth.dto.request.AuthLoginRequest;
import com.example.deliveryappproject.domain.auth.dto.request.AuthRefreshTokenRequest;
import com.example.deliveryappproject.domain.auth.entity.RefreshToken;
import com.example.deliveryappproject.domain.auth.repository.RefreshTokenRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.deliveryappproject.domain.auth.enums.TokenStatus.INVALIDATED;

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
    public User reissueToken(AuthRefreshTokenRequest request) {
        String token = request.getRefreshToken();

        RefreshToken refreshToken = findByTokenOrElseThrow(token);

        if (refreshToken.getTokenStatus() == INVALIDATED ||
                refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("유효기간이 지난 refresh 토큰입니다. 다시 로그인 해주세요");
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
