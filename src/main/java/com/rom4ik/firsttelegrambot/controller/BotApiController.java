package com.rom4ik.firsttelegrambot.controller;

import com.rom4ik.firsttelegrambot.model.ReceivedUpdate;
import com.rom4ik.firsttelegrambot.service.ReceivedUpdatesService;
import com.rom4ik.firsttelegrambot.service.api.YesNoImageGeneratorService;
import com.rom4ik.firsttelegrambot.service.api.YouTubeMP3DownloadService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author rom4ik
 */
@RestController
@RequestMapping("bot/api/")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BotApiController {
    YesNoImageGeneratorService yesNoImageGeneratorService;
    YouTubeMP3DownloadService youTubeMP3DownloadService;

    @Autowired
    public BotApiController(YesNoImageGeneratorService yesNoImageGeneratorService,
                            YouTubeMP3DownloadService youTubeMP3DownloadService, ReceivedUpdatesService receivedUpdatesService) {
        this.yesNoImageGeneratorService = yesNoImageGeneratorService;
        this.youTubeMP3DownloadService = youTubeMP3DownloadService;
    }

    @GetMapping(value = "/random-yes-no-gif", produces = MediaType.IMAGE_GIF_VALUE)
    public byte[] randomYesNoGif() {
        return yesNoImageGeneratorService.getRandomGif();
    }

    @GetMapping(value = "/video-to-mp3", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] audioByVideoLink(@RequestParam String link) {
        return youTubeMP3DownloadService.getAudioFromVideoUrl(link).getBytes();
    }
}
