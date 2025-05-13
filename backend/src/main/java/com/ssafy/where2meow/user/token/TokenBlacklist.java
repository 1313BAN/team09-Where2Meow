package com.ssafy.where2meow.user.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 로그아웃된 토큰을 관리하는 블랙리스트
 * 메모리에 저장하므로 서버 재시작 시 초기화됨
 * 운영 환경에서는 Redis 등을 사용하여 구현하는 것이 좋음
 */
@Component
@Slf4j
public class TokenBlacklist {

    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenBlacklist() {
        // 만료된 토큰을 정기적으로 제거하는 작업 스케줄링 (1시간마다)
        scheduler.scheduleAtFixedRate(
                this::removeExpiredTokens,
                1, 1, TimeUnit.HOURS
        );
    }

    /**
     * 토큰을 블랙리스트에 추가(메모리에 추가)
     * @param token JWT 토큰
     * @param expirationTime 토큰 만료 시간 (밀리초 단위 타임스탬프)
     */
    public void addToBlacklist(String token, long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     * @param token JWT 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    /**
     * 만료된 토큰을 블랙리스트에서 제거
     */
    private void removeExpiredTokens() {
        long currentTimeMillis = System.currentTimeMillis();
        int beforeSize = blacklistedTokens.size();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < currentTimeMillis);
        int afterSize = blacklistedTokens.size();
        
        if (beforeSize > afterSize) {
            log.info("[블랙리스트 정리] 만료된 토큰 {}개 제거, 현재 {}개 남음", beforeSize - afterSize, afterSize);
        }
    }
}