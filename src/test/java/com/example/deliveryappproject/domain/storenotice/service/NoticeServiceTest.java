package com.example.deliveryappproject.domain.storenotice.service;

import com.example.deliveryappproject.domain.storenotice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.domain.storenotice.entity.Notice;
import com.example.deliveryappproject.domain.storenotice.repository.NoticeRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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

    @Test
    void 가게_없을경우_공지생성_불가() {
        // Given
        Long storeId = 1L;

        given(storeRepository.existsById(storeId)).willReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> noticeService.createNotice(storeId, "공지 제목", "공지 내용"));
        assertThat(exception.getMessage()).isEqualTo("가게 확인 불가: " + storeId);
        verify(noticeRepository, never()).save(any(Notice.class));
    }

    @Test
    void 공지_조회_테스트() {
        // Given
        Long storeId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Notice notice = new Notice(new Store(), "공지 제목", "공지 내용");
        ReflectionTestUtils.setField(notice, "id", 1L);
        Page<Notice> noticePage = new PageImpl<>(List.of(notice), pageRequest, 1);

        given(storeRepository.existsById(storeId)).willReturn(true);
        given(noticeRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageRequest)).willReturn(noticePage);

        // When
        Page<NoticeResponseDto> notices = noticeService.getStoreNotices(storeId, pageRequest);

        // Then
        assertThat(notices).isNotNull();
        assertThat(notices.getTotalElements()).isEqualTo(1);
        assertThat(notices.getContent().get(0).getTitle()).isEqualTo("공지 제목");
    }

    @Test
    void 없는_가게의_공지_조회시_예외처리() {
        // Given
        Long storeId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(storeRepository.existsById(storeId)).willReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> noticeService.getStoreNotices(storeId, pageRequest));
        assertThat(exception.getMessage()).isEqualTo("가게 확인 불가: " + storeId);
    }

    @Test
    void 공지_수정_테스트() {
        // Given
        Notice notice = new Notice(new Store(), "공지 제목", "공지 내용");
        ReflectionTestUtils.setField(notice, "id", 1L);

        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));

        // When
        Long updateNoticeId = noticeService.updateNotice(notice.getId(), "변경된 제목", "변경된 내용");

        // Then
        assertThat(updateNoticeId).isEqualTo(notice.getId());
        assertThat(notice.getTitle()).isEqualTo("변경된 제목");
        assertThat(notice.getContents()).isEqualTo("변경된 내용");
    }

    @Test
    void 없는_공지는_수정할수_없음() {
        // Given
        Long noticeId = 1L;

        given(noticeRepository.findById(noticeId)).willReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> noticeService.updateNotice(noticeId, "변경된 제목", "변경된 내용"));
        assertThat(exception.getMessage()).isEqualTo("공지 확인 불가: " + noticeId);
    }

    @Test
    void 공지_삭제_테스트() {
        // Given
        Notice notice = new Notice(new Store(), "공지 제목", "공지 내용");
        ReflectionTestUtils.setField(notice, "id", 1L);

        given(noticeRepository.findById(notice.getId())).willReturn(Optional.of(notice));
        willDoNothing().given(noticeRepository).delete(notice);

        // When
        noticeService.deleteNotice(notice.getId(), 1L);

        // Then
        verify(noticeRepository, times(1)).delete(notice);
    }

    @Test
    void 없는_공지는_삭제할수_없음() {
        // Given
        Long noticeId = 1L;
        given(noticeRepository.findById(noticeId)).willReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> noticeService.deleteNotice(noticeId, 1L));
        assertThat(exception.getMessage()).isEqualTo("공지 확인 불가: " + noticeId);
        verify(noticeRepository, never()).delete(any(Notice.class));
    }
}