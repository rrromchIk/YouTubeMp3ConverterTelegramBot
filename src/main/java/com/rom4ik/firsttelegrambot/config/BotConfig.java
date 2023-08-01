package com.rom4ik.firsttelegrambot.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rom4ik
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = "telegram.bot")
public class BotConfig {
    String name;
    String token;
}
