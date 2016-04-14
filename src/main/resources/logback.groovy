import ch.qos.logback.classic.PatternLayout
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.net.SMTPAppender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy

import static ch.qos.logback.classic.Level.*
import static ch.qos.logback.core.spi.FilterReply.ACCEPT
import static ch.qos.logback.core.spi.FilterReply.DENY

/**
 * @author Gert Leenders
 *
 * Logback configuration.
 */

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
    }
}

appender("FILE", RollingFileAppender) {
    file = "/var/log/zoufzouf/slurper.log"
    filter(LevelFilter) {
        level = INFO
        onMatch = ACCEPT
        onMismatch = DENY
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "/var/log/zoufzouf/slurper.%d{yyyy-MM-dd}.log"
        maxHistory = 30
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
    }
}

appender("WARN-LOG", RollingFileAppender) {
    file = "/var/log/zoufzouf/warn.log"
    filter(LevelFilter) {
        level = WARN
        onMatch = ACCEPT
        onMismatch = DENY
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "/var/log/zoufzouf/warn.%d{yyyy-MM-dd}.log"
        maxHistory = 15
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %level %logger - %msg%n"
    }
}

logger("be.pixxis", WARN, ["WARN-LOG"])
logger("be.pixxis", INFO, ["FILE", "STDOUT"])