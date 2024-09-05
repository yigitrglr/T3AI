package com.novatronrehberim;

import java.util.Scanner;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.json.JSONObject;
import org.json.JSONArray;

public class App {
    private static final String AI_API_URL = "https://inference.t3ai.org/v1/chat/completions";
    private static final String API_KEY = "t3ai";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // başlangıçta 2 seçenek olsun hem plan oluşturma hem de chatbot
        System.out.println("AI Çalışma Programı Oluşturucusuna Hoşgeldiniz!");
        System.out.print("Çalışmak istediğiniz dersi giriniz: ");
        String subject = scanner.nextLine();

      
        
        System.out.print("Bilgi seviyenizi giriniz (az/orta/yüksek): ");
        String level = scanner.nextLine();
        
        System.out.print("Çalışma programınızın süresini giriniz (kaç haftalık): ");
        int duration = scanner.nextInt();
        
        String prompt = String.format("Lütfen sınıf seviyesinde %s dersi için sınıf %s seviyesinde %d haftalık detaylı bir tablo şeklinde çalışma programı oluşturun. Türkiye Cumhuriyeti Milli Eğitim Bakanlığının eğitim müfredatına göre hazırla." + "Lütfen programı maddeler halinde, açık ve anlaşılır bir şekilde yazın.", subject, level, duration);
               
        
        String studyProgram = getAIResponse(prompt);
        System.out.println("\nİşte kişiselleştirilmiş çalışma programınız:\n");
        System.out.println(studyProgram);
        
        scanner.close();
    }
    
    private static String getAIResponse(String prompt) {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject requestBody = new JSONObject()
            .put("model", "/vllm-workspace/hackathon_model_2")
            .put("messages", new JSONObject[]
                {new JSONObject()
                    .put("role", "user")
                    .put("content", prompt)
                }
            );
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(AI_API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + API_KEY)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();
        
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: İşlem sırasında bir hata oluştu.";
        }
    }
}