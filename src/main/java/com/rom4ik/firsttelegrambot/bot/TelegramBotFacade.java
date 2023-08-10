package com.rom4ik.firsttelegrambot.bot;

import com.rom4ik.firsttelegrambot.exception.InvalidVideoUrlException;
import com.rom4ik.firsttelegrambot.exception.YouTubeMp3ApiResponseFailStatusException;
import com.rom4ik.firsttelegrambot.model.Audio;
import com.rom4ik.firsttelegrambot.model.BotResponse;
import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import com.rom4ik.firsttelegrambot.service.BotResponsesService;
import com.rom4ik.firsttelegrambot.service.ReceivedUpdatesService;
import com.rom4ik.firsttelegrambot.service.api.YouTubeMP3DownloadService;
import com.vdurmont.emoji.EmojiParser;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author rom4ik
 */
@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramBotFacade {
    YouTubeMP3DownloadService youTubeMP3DownloadService;
    RestTemplate restTemplate;
    ReceivedUpdatesService receivedUpdatesService;
    BotResponsesService botResponsesService;
    MyTelegramBot myTelegramBot;


    public TelegramBotFacade(YouTubeMP3DownloadService youTubeMP3DownloadService, RestTemplate restTemplate,
                             ReceivedUpdatesService receivedUpdatesService, BotResponsesService botResponsesService,
                             MyTelegramBot myTelegramBot) {
        this.youTubeMP3DownloadService = youTubeMP3DownloadService;
        this.restTemplate = restTemplate;
        this.receivedUpdatesService = receivedUpdatesService;
        this.botResponsesService = botResponsesService;
        this.myTelegramBot = myTelegramBot;
    }

    public void handleUpdate(Update update) {
        Message message = update.getMessage();
        if(update.hasMessage() && message.hasText()) {
            Long chatId = message.getChatId();
            String userName = message.getFrom().getUserName();
            String messageText = message.getText();

            log.debug("Update received. Chat id: {}, userName: {}, message: {}", chatId, userName, messageText);

            //TelegramAPI returns Date in UNIX time. (in seconds)
            //But Date wants i milliseconds, therefor we multiply by 1000
            Date messageDate = new Date((long) update.getMessage().getDate() * 1000);
            receivedUpdatesService.saveUpdate(new ReceivedUpdate(userName, chatId, messageText, messageDate));

            String replyMessage = handleInputMessage(message);




            botResponsesService.saveBotResponse(new BotResponse(userName, chatId, replyMessage));
        }
    }

    private String handleInputMessage(Message message) {
        Long chatId = message.getChatId();
        String userName = message.getFrom().getUserName();
        String messageText = message.getText();
        String replyMessage;

        if (messageText.equals("/start")) {
            replyMessage = "Hello, " + userName + ", send me link)";
            sendTextMessage(chatId, replyMessage);
        } else {
            List<Integer> messagesToDelete = new ArrayList<>();

            messagesToDelete.add(
                    sendTextMessage(chatId, "Processing your link. This may take some time)").get()
            );
            messagesToDelete.add(
                    sendTextMessage(chatId, EmojiParser.parseToUnicode(":hourglass_flowing_sand:")).get()
            );
            try {
                Audio audio = youTubeMP3DownloadService.getAudioFromVideoUrl(messageText);
                sendAudioMessage(chatId, audio);
                replyMessage = "Sending audio: " + audio.getName();
            } catch (InvalidVideoUrlException e) {
                log.error(e.getMessage());
                replyMessage = "Invalid video URL. Please provide valid YouTube video URL :)";
                sendTextMessage(chatId, replyMessage);
            } catch (YouTubeMp3ApiResponseFailStatusException e) {
                replyMessage = e.getMessage();

                log.error(replyMessage);
                sendTextMessage(chatId, replyMessage);
            }
            messagesToDelete.forEach(id -> deleteMessage(chatId, id));
        }
        return replyMessage;
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(chatId)
                .messageId(messageId)
                .build();

        try {
            myTelegramBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private Optional<Integer> sendAudioMessage(Long chatId, Audio audio) {
        SendAudio sendAudio = SendAudio.builder()
                .chatId(chatId)
                .audio(new InputFile(new ByteArrayInputStream(audio.getBytes()), audio.getName()))
                .build();
        Optional<Integer> messageId = Optional.empty();
        try {
            log.debug("Sending audio to chatId: {}", chatId.toString());
            messageId = Optional.of(myTelegramBot.execute(sendAudio).getMessageId());
            log.debug("Audio has been sent successfully");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            sendTextMessage(chatId, e.getMessage());
        }

        return messageId;
    }

//    private void sendGif(Long chatId, String gifUrl) {
//        String url = String.format("https://api.telegram.org/" +
//                "bot%s/sendVideo?chat_id=%s&video=%s", botConfig.getToken(), chatId.toString(), gifUrl);
//        log.info("Sending video to chatId: {} by URL: {}", chatId.toString(), url);
//        String response = restTemplate.getForObject(url, String.class);
//        log.debug("Response -> {}", response);
//    }

    private Optional<Integer> sendTextMessage(Long chatId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();

        Optional<Integer> messageId = Optional.empty();
        try {
            log.debug("Sending message to chatId: {}, message: {}", chatId.toString(), message);
            messageId = Optional.of(myTelegramBot.execute(sendMessage).getMessageId());
            log.debug("Message has been sent successfully");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        return messageId;
    }
}
