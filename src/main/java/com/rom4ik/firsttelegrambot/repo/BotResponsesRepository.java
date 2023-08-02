package com.rom4ik.firsttelegrambot.repo;

import com.rom4ik.firsttelegrambot.model.BotResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author rom4ik
 */
@Repository
public interface BotResponsesRepository extends JpaRepository<BotResponse, Integer> {
}
