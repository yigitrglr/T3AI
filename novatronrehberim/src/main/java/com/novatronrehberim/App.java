package com.novatronrehberim;

import java.util.Scanner;
import java.net.URI;
import org.json.JSONObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;



public class App {
    private static final String AI_API_URL = "https://inference2.t3ai.org/v1/completions";
    // private static final String API_KEY = "t3ai";

    /* 
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // başlangıçta 2 seçenek olsun hem plan oluşturma hem de chatbot
        System.out.println("AI Çalışma Programı Oluşturucusuna Hoşgeldiniz!");
        System.out.println("1. Çalışma Programı Oluştur");
        System.out.println("2. AI Chatbot ile Konuş");
        System.out.print("Lütfen bir seçenek girin (1 veya 2): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            createStudyProgram(scanner);
        } else if (choice == 2) {
            chatWithAI(scanner);
        } else {
            System.out.println("Geçersiz seçenek. Lütfen 1 veya 2 girin.");
        }

        scanner.close();
    }
*/
    private static void createStudyProgram(Scanner scanner) {
        System.out.print("Çalışmak istediğiniz dersi giriniz: ");
        String subject = scanner.nextLine();

        System.out.print("Bilgi seviyenizi giriniz (az/orta/yüksek): ");
        String level = scanner.nextLine();

        System.out.print("Çalışma programınızın süresini giriniz (kaç haftalık): ");
        int duration = scanner.nextInt();

        String prompt = String.format(
                "Lütfen %s dersi için sınıf %s seviyesinde %d haftalık detaylı bir tablo şeklinde çalışma programı oluşturun. Türkiye Cumhuriyeti Milli Eğitim Bakanlığının eğitim müfredatına göre hazırla."
                        + "Lütfen programı maddeler halinde, açık ve anlaşılır bir şekilde yazın.",
                subject, level, duration);
        // String prompt = String.format("Sen bir lise fizik öğretmenisin ve
        // öğrencilerin için bir konu anlatacaksın Konumuz şu: Açısal hız ve doğrusal
        // hız arasındaki ilişki. Bu konuyu örneklerle nasıl anlatırsın? ");

        String studyProgram = getAIResponse(prompt, 0.05, 0.8, 1000);
        System.out.println("\nİşte kişiselleştirilmiş çalışma programınız:\n");
        System.out.println(studyProgram);

        scanner.close();
    }

    private static String getAIResponse(String prompt, double temperature, double topP, int maxTokens) {
        HttpClient client = HttpClient.newHttpClient();
        JSONObject requestBody = new JSONObject()
                .put("model", "/home/ubuntu/hackathon_model_2/")
                .put("messages", new JSONObject[] { new JSONObject()
                        .put("role", "user")
                        .put("content", prompt)
                })
                .put("temperature", 0.6) // 0.6
                .put("top_p", 0.85) // 0.85
                .put("max_tokens", 200) // 500
                .put("skip_special_tokens", true) // true
                .put("repetition_penalty", 1.2) // 1.20
                .put("top_k", 32.0); // 34

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AI_API_URL))
                .header("Content-Type", "application/json")
                //.header("Authorization", "Bearer " + API_KEY)
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

    private static void chatWithAI(Scanner scanner) {
        System.out.println("AI Chatbot'a Hoşgeldiniz! Çıkmak için 'quit' yazın.");
        while (true) {
            System.out.print("Siz: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("quit")) {
                System.out.println("Chatbot: Görüşmek üzere!");
                break;
            }

            String response = getAIResponse(userInput, 0.05, 0.8, 1000);
            System.out.println("Chatbot: " + response);
        }
    }
}