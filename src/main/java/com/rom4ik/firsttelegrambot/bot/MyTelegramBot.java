package com.rom4ik.firsttelegrambot.bot;

import com.rom4ik.firsttelegrambot.config.BotConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;


/**
 * @author rom4ik
 */
@Log4j2
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MyTelegramBot extends TelegramLongPollingBot {
    BotConfig botConfig;
    TelegramBotFacade telegramBotFacade;

    // To avoid BeanCurrentlyInCreationException because of circular dependency
    // between TelegramBotFacade and MyTelegramBot

    @Autowired
    public MyTelegramBot(BotConfig botConfig, @Lazy TelegramBotFacade telegramBotFacade) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.telegramBotFacade = telegramBotFacade;
    }

    @Override
    public void onUpdateReceived(Update update) {
        telegramBotFacade.handleUpdate(update);
    }


    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
