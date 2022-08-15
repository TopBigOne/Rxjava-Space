package com.jar.rxjava2_backpressure;

/**
 * @author : dev
 * @version :
 * @Date :  2022/8/15 19:37
 * @Desc : 被压模式
 */
public enum BackpressureMode {
    /**
     * 即缓存区大小无限制
     */
    BUFFER,
    DROP, LATEST,
}
