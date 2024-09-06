package com.novatronrehberim.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.novatronrehberim.models.AIPrompt;

public class AIService { 
    public String sendPostRequest(String urlString, String payload) throws Exception{
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try(OutputStream os = conn.getOutputStream()){
            byte[] input = payload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    public String getChatbotResponse(String question, String role, String category) {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject()
                .put("role", "system")
                .put("content", "Sen bir ilkokul öğretmenisin. Çocuklara; sayıları, matematiği ve alfabeyi onların anlayacağı ve seveceği şekilde  anlatan bir yapay zekâ modelisin. Zor konuları hikâyeleştirirek öğrenimi kolaylaştırırsın."))
            .put(new JSONObject()
                .put("role", "assistant")
                .put("content", String.format("Şu an öğrencilere anlatacağın konu %s üzerine olacaktır.", category)))
            .put(new JSONObject()
                .put("role", "user")
                .put("content", String.format("%s konusunu ilk okul çocuklarının anlayabileceği şekilde hikâyeleştir.", question)));
            return getLLMResponse(jsonData);
    }

    public  String getLLMResponse(JSONArray jsonData) {
        try {
            String specialFormatOutput = convertToSpecialFormat(jsonData);

            JSONObject payload = new JSONObject()
                .put("model", "/home/ubuntu/hackathon_model_2/")
                .put("prompt", specialFormatOutput)
                .put("temperature", 0.01) 
                .put("top_p", 0.95) // pref 0.8
                .put("max_tokens", 2048) // default: 1024
                .put("repetition_penalty", 1.1) // pref 1.2
                .put("stop_token_ids", new JSONArray().put(128001).put(128009))
                .put("skip_special_tokens", true);

            String response = sendPostRequest("https://inference2.t3ai.org/v1/completions", payload.toString());
            JSONObject jsonResponse = new JSONObject(response);
            return jsonResponse.getJSONArray("choices").getJSONObject(0).getString("text");
        }
    }

    public  String convertToSpecialFormat(JSONArray jsonData) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < jsonData.length(); i++) {
            JSONObject entry = jsonData.getJSONObject(i);
            String role = entry.getString("role");
            String content = entry.getString("content");
    
            if (role.equals("system")) {
                output.append(String.format("<|im_start|>system\n%s<|im_end|>\n", content));
            } else if (role.equals("user")) {
                output.append(String.format("<|im_start|>human\n%s<|im_end|>\n", content));
            } else if (role.equals("assistant")) {
                output.append(String.format("<|im_start|>assistant\n%s<|im_end|>\n", content));
            }
        }
        output.append("<|im_start|>assistant\n");
        return output.toString();
    }
}
