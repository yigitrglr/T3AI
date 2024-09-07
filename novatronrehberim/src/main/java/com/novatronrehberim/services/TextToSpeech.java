package com.novatronrehberim.services;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class TextToSpeech {
    private static final String XI_API_KEY = "sk_ce0fbd141ee5fe0cefd38bf3ced2eeaf24a2590e1b27126e";         
    private static final String VOICE_ID = "e3yyhgCMd5vwBY6JdxQC"; // e3yyhgCMd5vwBY6JdxQC

    public void generateSpeech(String textToSpeak, String outputFilePath) {
        try {
            JSONObject requestBody = new JSONObject()
                .put("text", textToSpeak)
                .put("model_id", "eleven_turbo_v2_5")
                .put("voice_settings", new JSONObject()
                    .put("stability", 0.5)
                    .put("similarity_boost", 0.5));

            HttpResponse<byte[]> response = Unirest.post("https://api.elevenlabs.io/v1/text-to-speech/{voice_id}")
                .routeParam("voice_id", VOICE_ID)
                .header("xi-api-key", XI_API_KEY)
                .header("Content-Type", "application/json")
                .header("Accept", "audio/mpeg")
                .body(requestBody.toString())
                .asBytes();

            if (response.isSuccess()) {
                try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                    fos.write(response.getBody());
                }
                System.out.println("TTS şuraya kaydedildi: " + outputFilePath);
            } else {
                System.err.println("TTS oluştururken bir hata meydana geldi: " + response.getStatus() + " " + response.getStatusText());
            }
        } catch (Exception e) {
            System.err.println("TTS oluştururken bir hata meydana geldi: " + e.getMessage());
        }
    }
}
