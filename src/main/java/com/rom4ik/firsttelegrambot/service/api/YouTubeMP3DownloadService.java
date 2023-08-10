package com.rom4ik.firsttelegrambot.service.api;

import com.rom4ik.firsttelegrambot.dto.YouTubeMp3ApiResponseDTO;
import com.rom4ik.firsttelegrambot.exception.InvalidVideoUrlException;
import com.rom4ik.firsttelegrambot.exception.YouTubeMp3ApiResponseFailStatusException;
import com.rom4ik.firsttelegrambot.model.Audio;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rom4ik
 */
@Service
@Log4j2
@PropertySource("classpath:application.properties")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YouTubeMP3DownloadService {
    @Value("${api.youTubeMp3.url}")
    String youTubeMp3ApiUrl;
    @Value("${api.youTubeMp3.key}")
    String youTubeMp3ApiKey;
    @Value("${api.youTubeMp3.host}")
    String youTubeMp3ApiHost;
    final RestTemplate restTemplate;

    @Autowired
    public YouTubeMP3DownloadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Audio getAudioFromVideoUrl(String youTubeVideoUrl) {
        log.debug("Provided YouTube video link: {}", youTubeVideoUrl);

        String videoId = getVideoIdFromUrl(youTubeVideoUrl);
        log.debug("Video id: {}", videoId);

        YouTubeMp3ApiResponseDTO youTubeMp3ApiResponseDTO = getAudioDTOFromVideoId(videoId);
        return getAudioFromAudioDTO(youTubeMp3ApiResponseDTO);
    }

    private YouTubeMp3ApiResponseDTO getAudioDTOFromVideoId(String videoId) {
        ResponseEntity<YouTubeMp3ApiResponseDTO> responseEntity = sendRequestToYouTubeMp3Api(videoId);
        YouTubeMp3ApiResponseDTO youTubeMp3ApiResponseDTO = responseEntity.getBody();

        switch (youTubeMp3ApiResponseDTO.getStatus()) {
            case "fail" -> throw new YouTubeMp3ApiResponseFailStatusException(youTubeMp3ApiResponseDTO.getMsg());
            case "processing" -> youTubeMp3ApiResponseDTO = getAudioDTOFromVideoId(videoId);
        }

        return youTubeMp3ApiResponseDTO;
    }

    private ResponseEntity<YouTubeMp3ApiResponseDTO> sendRequestToYouTubeMp3Api(String videoId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Key", youTubeMp3ApiKey);
        headers.set("X-RapidAPI-Host", youTubeMp3ApiHost);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String url = String.format("%s?id=%s", youTubeMp3ApiUrl, videoId);

        log.debug("GET request to YouTubeMp3API: {}", url);
        ResponseEntity<YouTubeMp3ApiResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                YouTubeMp3ApiResponseDTO.class);

        log.debug("Response from YouTubeMp3API: {}", response.getBody());
        return response;
    }

    private Audio getAudioFromAudioDTO(YouTubeMp3ApiResponseDTO youTubeMp3ApiResponseDTO) {
        log.debug("GET request to download audio: {}", youTubeMp3ApiResponseDTO.getLink());

        ResponseEntity<byte[]> respEnt = restTemplate.getForEntity(youTubeMp3ApiResponseDTO.getLink(), byte[].class);
        MediaType mediaType = respEnt.getHeaders().getContentType();

        log.debug("Response status -> {}", respEnt.getStatusCode().toString());
        log.debug("Audio type in the response: {}", mediaType.toString());

        return new Audio(youTubeMp3ApiResponseDTO.getTitle(), respEnt.getBody());
    }



    private String getVideoIdFromUrl(String videoUrl) {
        String videoId;

        // The pattern to match YouTube video IDs in different URL formats
        Pattern pattern = Pattern.compile(
                "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})"
        );

        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find()) {
            videoId = matcher.group(1);
        } else {
            throw new InvalidVideoUrlException("Bad video url. No video id found");
        }

        return videoId;
    }
}
