package com.example.deliveryappproject.domain.auth.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.config.PasswordEncoder;
import com.example.deliveryappproject.domain.auth.dto.request.AuthLoginRequest;
import com.example.deliveryappproject.domain.auth.dto.request.AuthRefreshTokenRequest;
import com.example.deliveryappproject.domain.auth.dto.response.AuthTokenResponse;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /* 로그인 */
    @Transactional
    public AuthTokenResponse login(AuthLoginRequest request) {
        User user = userService.findUserByEmailOrElseThrow(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("잘못된 비밀번호입니다.");
        }

        return getTokenResponse(user);
    }

    /* Refresh token 만료로 로그아웃 */
    @Transactional
    public void logout(AuthUser authUser) {
        tokenService.revokeRefreshToken(authUser.getId());
    }

    /* Access Token, Refresh Token 재발급 */
    @Transactional
    public AuthTokenResponse reissueAccessToken(AuthRefreshTokenRequest request) {
        User user = tokenService.reissueToken(request);

        return getTokenResponse(user);
    }

    /* Access Token, Refresh Token 생성 및 저장 */
    private AuthTokenResponse getTokenResponse(User user) {

        String accessToken = tokenService.createAccessToken(user);
        String refreshToken = tokenService.createRefreshToken(user);

        return new AuthTokenResponse(accessToken, refreshToken);
    }
}
