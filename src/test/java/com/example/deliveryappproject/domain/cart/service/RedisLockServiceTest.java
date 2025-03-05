package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.repository.RedisLockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RedisLockServiceTest {

    @Mock
    private RedisLockRepository redisLockRepository;

    @InjectMocks
    private RedisLockService redisLockService;

    @Test
    public void 락획득_작업수행_락해제_성공() {

        //given
        Long key = 1L;
        Runnable mockRunnable = mock(Runnable.class);

        //when
        redisLockService.executeWithLock(key,mockRunnable);

        InOrder inOrder = Mockito.inOrder(redisLockRepository, mockRunnable);
        inOrder.verify(redisLockRepository).waitForLock(anyLong());
        inOrder.verify(mockRunnable).run();
        inOrder.verify(redisLockRepository).unLock(key);

    }

    @Test
    public void 락획득_작업수행_예외발생_락해제_성공() {

        //given
        Long key = 1L;
        Runnable mockRunnable = mock(Runnable.class);
        doThrow(new RuntimeException("작업 중 예외 발생")).when(mockRunnable).run();
        //when
        assertThrows(RuntimeException.class, () -> redisLockService.executeWithLock(key, mockRunnable));

        InOrder inOrder = Mockito.inOrder(redisLockRepository, mockRunnable);
        inOrder.verify(redisLockRepository).waitForLock(anyLong());
        inOrder.verify(mockRunnable).run();
        inOrder.verify(redisLockRepository).unLock(key);

    }
}