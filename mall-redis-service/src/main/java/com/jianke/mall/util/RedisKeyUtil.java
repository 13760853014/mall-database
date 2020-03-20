package com.jianke.mall.util;

/**
 * 促销缓存key工具类
 */
public class RedisKeyUtil {

    public static final String PROMO_SERVICE_PREFIX = "promo-service";
    public static final String DELIMITER = ":";

    private RedisKeyUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @return
     */
    public static String getStudentKey(String name) {
        return new StringBuilder().append(PROMO_SERVICE_PREFIX).append(DELIMITER)
                .append("redis:student")
                .append(DELIMITER)
                .append(name)
                .toString().toLowerCase();
    }
}
