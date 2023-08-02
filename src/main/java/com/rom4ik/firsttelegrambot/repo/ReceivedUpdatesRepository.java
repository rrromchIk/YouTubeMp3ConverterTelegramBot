package com.rom4ik.firsttelegrambot.repo;

import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReceivedUpdatesRepository extends JpaRepository<ReceivedUpdate, Integer> {
    List<ReceivedUpdate> findByReceivingDate(Date date);
}
