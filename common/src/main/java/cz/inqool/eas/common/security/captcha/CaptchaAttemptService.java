package cz.inqool.eas.common.security.captcha;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.Builder;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class CaptchaAttemptService {
    private final LoadingCache<String, Integer> attemptsCache;

    private final int maxAttempt;

    @Builder
    public CaptchaAttemptService(Integer maxAttempt, Integer duration) {
        Assert.notNull(maxAttempt, "maxAttempt cannot be null");
        Assert.notNull(duration, "duration cannot be null");

        this.maxAttempt = maxAttempt;
        this.attemptsCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {
            @Override
            public Integer load(final String key) {
                return 0;
            }
        });
    }

    public void reCaptchaSucceeded(final String key) {
        attemptsCache.invalidate(key);
    }

    public void reCaptchaFailed(final String key) {
        int attempts = attemptsCache.getUnchecked(key);
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
        return attemptsCache.getUnchecked(key) >= maxAttempt;
    }
}