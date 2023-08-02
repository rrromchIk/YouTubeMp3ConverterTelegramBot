package com.rom4ik.firsttelegrambot.controller;

import com.rom4ik.firsttelegrambot.model.BotResponse;
import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import com.rom4ik.firsttelegrambot.service.BotResponsesService;
import com.rom4ik.firsttelegrambot.service.ReceivedUpdatesService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rom4ik
 */
@RestController
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/bot/analytics")
public class BotAnalyticsController {
    ReceivedUpdatesService receivedUpdatesService;
    BotResponsesService botResponsesService;

    @Autowired
    public BotAnalyticsController(ReceivedUpdatesService receivedUpdatesService, BotResponsesService botResponsesService) {
        this.receivedUpdatesService = receivedUpdatesService;
        this.botResponsesService = botResponsesService;
    }

    @GetMapping(value = "/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ReceivedUpdate> getAnalytics() {
        return receivedUpdatesService.findAll();
    }

    @GetMapping(value = "/responses", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<BotResponse> getAllBotResponses() {
        return botResponsesService.findAllBotResponses();
    }
}
