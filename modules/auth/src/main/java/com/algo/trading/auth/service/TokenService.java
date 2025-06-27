package com.algo.trading.auth.service;

import com.algo.trading.auth.model.SessionToken;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Two responsibilities<br>
 * • cache today’s {@link SessionToken}<br>
 * • remember every request_token we have already processed (idempotency)
 */
@Service
@Slf4j
public class TokenService {

    private final Cache sessionCache;          // key: "today"
    private final Cache usedReqTokenCache;     // key: request_token string

    public TokenService(CacheManager cm) {
        this.sessionCache = cm.getCache("kiteSession");
        this.usedReqTokenCache = cm.getCache("usedRequestTokens");
    }

    /* ---------- session token (access/public) ---------------- */

    @Cacheable(value = "kiteSession", key = "'today'")
    public SessionToken current() {
        return null;
    }

    @CachePut(value = "kiteSession", key = "'today'")
    public SessionToken save(SessionToken tok) {
        log.info("Session cached for {}", tok.getUsername());
        return tok;
    }

    public void clearSession() {
        sessionCache.evict("today");
    }

    /* ---------- request-token de-duplication ----------------- */

    public boolean isRequestTokenSeen(String reqTok) {
        return usedReqTokenCache.get(reqTok) != null;
    }

    public void markRequestTokenSeen(String reqTok) {
        usedReqTokenCache.put(reqTok, Boolean.TRUE);
    }

    /* ---------- daily reset at 07:10 IST --------------------- */

    @Scheduled(cron = "0 10 7 * * *", zone = "Asia/Kolkata")
    public void resetCaches() {
        log.info("Clearing session & used request-token caches (scheduled)");
        sessionCache.clear();
        usedReqTokenCache.clear();
    }

    /* ensure caches exist even if Caffeine created lazily */
    @PostConstruct
    void init() {
        if (sessionCache == null || usedReqTokenCache == null)
            throw new IllegalStateException("Caches not configured");
    }
}
