package com.example.deliveryappproject.domain.user.service;

import com.example.deliveryappproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void 회원_가입_테스트(){

    }

    @Test
    void 회원_전체조회_테스트(){

    }

    @Test
    void 로그인_회원_조회_테스트(){

    }

    @Test
    void 회원정보_수정_테스트(){

    }

    @Test
    void 회원_탈퇴_테스트(){

    }

    @Test
    void 탈퇴한_회원은_다시_가입할_수_없다(){

    }

    @Test
    void 동일한_이메일은_가입할_수_없다(){

    }


}
