package com.rom4ik.firsttelegrambot.service;

import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import com.rom4ik.firsttelegrambot.repo.ReceivedUpdatesRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author rom4ik
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReceivedUpdatesService {
    ReceivedUpdatesRepository receivedUpdatesRepository;

    @Autowired
    public ReceivedUpdatesService(ReceivedUpdatesRepository receivedUpdatesRepository) {
        this.receivedUpdatesRepository = receivedUpdatesRepository;
    }

    @Transactional
    public void saveUpdate(ReceivedUpdate receivedUpdate) {
        receivedUpdatesRepository.save(receivedUpdate);
    }

    public List<ReceivedUpdate> findAll() {
        return receivedUpdatesRepository.findAll();
    }

    public List<ReceivedUpdate> findByDate(Date date) {
        return receivedUpdatesRepository.findByReceivingDate(date);
    }
}
