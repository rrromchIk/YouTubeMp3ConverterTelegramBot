package com.rom4ik.firsttelegrambot.bot;

import com.rom4ik.firsttelegrambot.config.BotConfig;
import com.rom4ik.firsttelegrambot.exception.InvalidVideoUrlException;
import com.rom4ik.firsttelegrambot.exception.YouTubeMp3ApiResponseFailStatusException;
import com.rom4ik.firsttelegrambot.model.Audio;
import com.rom4ik.firsttelegrambot.model.BotResponse;
import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import com.rom4ik.firsttelegrambot.service.BotResponsesService;
import com.rom4ik.firsttelegrambot.service.ReceivedUpdatesService;
import com.rom4ik.firsttelegrambot.service.api.YouTubeMP3DownloadService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.Date;

/**
 * @author rom4ik
 */
@Log4j2
@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MyTelegramBot extends TelegramLongPollingBot {
    BotConfig botConfig;
    YouTubeMP3DownloadService youTubeMP3DownloadService;
    RestTemplate restTemplate;
    ReceivedUpdatesService receivedUpdatesService;
    BotResponsesService botResponsesService;


    @Autowired
    public MyTelegramBot(BotConfig botConfig, YouTubeMP3DownloadService youTubeMP3DownloadService,
                         ReceivedUpdatesService receivedUpdatesService, BotResponsesService botResponsesService,
                         RestTemplate restTemplate) {
        super(botConfig.getToken());
        this.botConfig = botConfig;
        this.youTubeMP3DownloadService = youTubeMP3DownloadService;
        this.restTemplate = restTemplate;
        this.receivedUpdatesService = receivedUpdatesService;
        this.botResponsesService = botResponsesService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();
            String message = update.getMessage().getText();

            log.debug("Update received. Chat id: {}, userName: {}, message: {}", chatId, userName, message);
            receivedUpdatesService.saveUpdate(new ReceivedUpdate(userName, chatId, message, new Date(), new Date()));

            String replyMessage;
            if(message.equals("/start")) {
                replyMessage = "Hello, " + userName +", send me link)";
                sendMessage(chatId, replyMessage);
            } else {
                try {
                    Audio audio = youTubeMP3DownloadService.getAudioFromVideoUrl(message);
                    sendAudio(chatId, audio);
                    replyMessage = "Sending audio: " + audio.getName();
                } catch (InvalidVideoUrlException e) {
                    log.error(e.getMessage());
                    replyMessage = "Invalid video URL. Please provide valid YouTube video URL :)";
                    sendMessage(chatId, replyMessage);
                } catch (YouTubeMp3ApiResponseFailStatusException e) {
                    log.error(e.getMessage());
                    replyMessage = e.getMessage();
                    sendMessage(chatId, replyMessage);
                }
            }

            botResponsesService.saveBotResponse(new BotResponse(userName, chatId, replyMessage, new Date(), new Date()));
        }
    }

    private void sendAudio(Long chatId, Audio audio) {
        SendAudio sendAudio = SendAudio.builder()
                .chatId(chatId)
                .audio(new InputFile(new ByteArrayInputStream(audio.getBytes()), audio.getName()))
                .build();

        try {
            log.debug("Sending audio to chatId: {}", chatId.toString());
            execute(sendAudio);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            sendMessage(chatId, e.getMessage());
        }
    }

    private void sendGif(Long chatId, String gifUrl) {
        String url = String.format("https://api.telegram.org/" +
                "bot%s/sendVideo?chat_id=%s&video=%s", botConfig.getToken(), chatId.toString(), gifUrl);
        log.info("Sending video to chatId: {} by URL: {}", chatId.toString(), url);
        String response = restTemplate.getForObject(url, String.class);
        log.debug("Response -> {}", response);
    }

    private void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
        try {
            log.debug("Sending message to chatId: {}, message: {}", chatId.toString(), message);
            this.sendApiMethod(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }
}
