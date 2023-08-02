package com.rom4ik.firsttelegrambot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author rom4ik
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "received_update")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReceivedUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "`from`")
    String from;

    @Column(name = "chat_id")
    Long chatId;

    @Column(name = "message")
    String message;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    Date receivingDate;

    @Column(name = "time")
    @Temporal(TemporalType.TIME)
    Date receivingTime;

    public ReceivedUpdate(String from, Long chatId, String message, Date receivingDate, Date receivingTime) {
        this.from = from;
        this.chatId = chatId;
        this.message = message;
        this.receivingDate = receivingDate;
        this.receivingTime = receivingTime;
    }
}
