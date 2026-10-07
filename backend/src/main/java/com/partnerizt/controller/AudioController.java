package com.partnerizt.controller;

import com.partnerizt.ai.elevenlabs.ElevenLabsService;
import com.partnerizt.dto.SpeakRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audio")
public class AudioController {

    private static final Logger log = LoggerFactory.getLogger(AudioController.class);

    private final ElevenLabsService elevenLabsService;

    public AudioController(ElevenLabsService elevenLabsService) {
        this.elevenLabsService = elevenLabsService;
    }

    @PostMapping(value = "/speak", produces = "audio/mpeg")
    public ResponseEntity<?> speak(@RequestBody SpeakRequest request) {
        if (request == null || request.getText() == null || request.getText().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Text parameter is required");
        }

        try {
            log.info("[AUDIO_CONTROLLER] Speak request received for companion={}", request.getCompanion());
            byte[] audioBytes = elevenLabsService.generateSpeech(request.getText(), request.getCompanion());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"speech.mp3\"");
            headers.setContentLength(audioBytes.length);

            return new ResponseEntity<>(audioBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.warn("[AUDIO_CONTROLLER] Bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            log.warn("[AUDIO_CONTROLLER] Service unavailable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());
        } catch (Exception e) {
            log.error("[AUDIO_CONTROLLER] Speech synthesis failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Audio synthesis failed: " + e.getMessage());
        }
    }
}
