package com.partnerizt.ai.elevenlabs;

public interface ElevenLabsService {
    /**
     * Synthesizes audio speech from text using the configured companion voice or default voice.
     *
     * @param text The text commentary to speak
     * @param companion The companion identifier (BIRDO, FLORA, ATLAS, MUNCH, NOVA)
     * @return Raw audio/mpeg MP3 byte array
     */
    byte[] generateSpeech(String text, String companion);
}
