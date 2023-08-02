package com.rom4ik.firsttelegrambot.service;

import com.rom4ik.firsttelegrambot.model.BotResponse;
import com.rom4ik.firsttelegrambot.repo.BotResponsesRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author rom4ik
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BotResponsesService {
    BotResponsesRepository botResponsesRepository;

    @Autowired
    public BotResponsesService(BotResponsesRepository botResponsesRepository) {
        this.botResponsesRepository = botResponsesRepository;
    }

    @Transactional
    public void saveBotResponse(BotResponse botResponse) {
        botResponsesRepository.save(botResponse);
    }

    public List<BotResponse> findAllBotResponses() {
        return botResponsesRepository.findAll();
    }
}

