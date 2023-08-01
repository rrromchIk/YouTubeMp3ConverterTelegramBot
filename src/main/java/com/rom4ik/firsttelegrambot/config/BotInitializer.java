package com.rom4ik.firsttelegrambot.config;

import com.rom4ik.firsttelegrambot.bot.MyTelegramBot;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * @author rom4ik
 */
@Component
@Log4j2
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BotInitializer {
    MyTelegramBot telegramBot;

    @Autowired
    public BotInitializer(MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
            log.debug("Registered bot {}", MyTelegramBot.class);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
