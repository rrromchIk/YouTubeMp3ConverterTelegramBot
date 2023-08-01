package com.rom4ik.firsttelegrambot.exception;

/**
 * @author rom4ik
 */
public class InvalidVideoUrlException extends RuntimeException {
    public InvalidVideoUrlException() {
    }

    public InvalidVideoUrlException(String message) {
        super(message);
    }
}
