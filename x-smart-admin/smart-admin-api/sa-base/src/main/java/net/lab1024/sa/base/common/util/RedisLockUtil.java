package net.lab1024.sa.base.common.util;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.redis.RedisService;

/**
 * @author 孙宇
 * @date 2024/09/03 22:58
 */
@Slf4j
public class RedisLockUtil {
    private static final int MAX_GET_LOCK_COUNT = 50;

    private static final long SLEEP_MILLISECONDS = 200L;

    private static final long LOCK_TIMEOUT = 180000;

    public static boolean tryLock(String lockKey) {
        return tryLock(lockKey, LOCK_TIMEOUT);
    }

    public static boolean tryLock(String lockKey, long timeout) {
        RedisService redisService = SpringBootBeanUtil.getBean(RedisService.class);
        boolean lock = false;
        for (int i = 0; i < MAX_GET_LOCK_COUNT; i++) {
            try {
                lock = redisService.getLock(lockKey, timeout);
                if (lock) {
                    break;
                }
                Thread.sleep(SLEEP_MILLISECONDS);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            }
        }
        return lock;
    }

    public static void unlock(String lockKey) {
        RedisService redisService = SpringBootBeanUtil.getBean(RedisService.class);
        redisService.unLock(lockKey);
    }


}
