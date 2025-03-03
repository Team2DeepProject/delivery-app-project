package com.example.deliveryappproject.domain.notice.service;

import com.example.deliveryappproject.domain.notice.entity.Notice;
import com.example.deliveryappproject.domain.notice.repository.NoticeRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;
    @Mock
    private StoreRepository storeRepository;
    @InjectMocks
    private NoticeService noticeService;

    @Test
    void 공지_생성_테스트() {
        // Given
        Long storeId = 1L;

        User user = new User("test@sample.com", "password1234", "테스트", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        Store store = Store.builder()
                .user(user)
                .storeName("테스트 가게")
                .build();
        ReflectionTestUtils.setField(store, "id", storeId);

        Notice notice = Notice.builder()
                .store(store)
                .title("공지 제목")
                .contents("공지 내용")
                .build();
        ReflectionTestUtils.setField(notice, "id", 1L);

        given(storeRepository.existsById(storeId)).willReturn(true);
        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));
        given(noticeRepository.save(any(Notice.class))).willReturn(notice);

        // When
        Long noticeId = noticeService.createNotice(storeId, "공지 제목", "공지 내용");

        // Then
        assertThat(noticeId).isNotNull();
        assertThat(noticeId).isEqualTo(1L);
        verify(noticeRepository, times(1)).save(any(Notice.class));
    }
}