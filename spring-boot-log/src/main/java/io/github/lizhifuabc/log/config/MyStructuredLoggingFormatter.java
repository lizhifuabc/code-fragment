package io.github.lizhifuabc.log.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.boot.logging.structured.StructuredLogFormatter;

/**
 * 自定义日志格式
 *
 * @author lizhifu
 * @since 2025/3/4
 */
public class MyStructuredLoggingFormatter implements StructuredLogFormatter<ILoggingEvent> {

    @Override
    public String format(ILoggingEvent event) {
        return "time=" + event.getTimeStamp() + ",level=" + event.getLevel() + ",message=" + event.getMessage() + "\n";
    }
}