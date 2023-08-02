package com.rom4ik.firsttelegrambot.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author rom4ik
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "response")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "to_user")
    String toUser;

    @Column(name = "to_chat_id")
    Long toChatId;

    @Column(name = "message")
    String message;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    Date responseDate;

    @Column(name = "time")
    @Temporal(TemporalType.TIME)
    Date responseTime;

    public BotResponse(String toUser, Long toChatId, String message, Date responseDate, Date responseTime) {
        this.toUser = toUser;
        this.toChatId = toChatId;
        this.message = message;
        this.responseDate = responseDate;
        this.responseTime = responseTime;
    }
}
