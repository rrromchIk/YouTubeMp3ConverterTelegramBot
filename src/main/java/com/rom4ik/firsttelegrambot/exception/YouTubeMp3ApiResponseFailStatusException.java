package com.rom4ik.firsttelegrambot.exception;

/**
 * @author rom4ik
 */
public class YouTubeMp3ApiResponseFailStatusException extends RuntimeException {
    public YouTubeMp3ApiResponseFailStatusException() {
    }

    public YouTubeMp3ApiResponseFailStatusException(String message) {
        super(message);
    }
}
