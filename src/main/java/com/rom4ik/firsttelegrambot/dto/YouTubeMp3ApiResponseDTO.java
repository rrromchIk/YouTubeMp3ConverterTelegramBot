package com.rom4ik.firsttelegrambot.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author rom4ik
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YouTubeMp3ApiResponseDTO {
    String link;
    String title;
    Integer duration;
    String status;
    String msg;
    Integer progress;
}
