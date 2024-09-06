package com.novatronrehberim;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.time.Instant;

public class LLM {
    private static final Scanner scanner = new Scanner(System.in);
    // Seçenek sistemi
    public static void main(String[] args) {
        try {
            while (true) {
                int choice = getUserChoice();
                if (choice == 1) {
                    runChatbot();
                } else if (choice == 2) {
                    runPhysicsTest();
                } else if (choice == 3) {
                    System.out.println("Fizik çalışma uygulamasını kullandığınız için teşekkür ederiz!");
                    break;
                } else {
                    System.out.println("Geçersiz seçenek. Lütfen tekrar deneyin.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public static int getUserChoice() {
        System.out.println("\nYKS Çalışma Programına Hoş geldiniz!");
        System.out.println("1. ChatBot olarak kullanın");
        System.out.println("2. YKS Ders Programı");
        System.out.println("3. Çıkış");
        System.out.print("Seçeneklerden birini seçin (1, 2, veya 3): ");
        return scanner.nextInt();
    }

    private static String sendPostRequest(String urlString, String payload) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Türkçeden dolayı UTF-8 charset
        try (OutputStream os = conn.getOutputStream()) {
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
    // ChatBot
    public static void runChatbot() {
        scanner.nextLine();
        String userId = generateUserId(); // Implement this method to generate a unique user ID
        long startTime = System.currentTimeMillis();
        
        while (true) {
            String question = getUserInput("Sorunuzu girin: (veya ':q' ile ana menüye dönebilirsiniz): ");
            if (question.equalsIgnoreCase(":q")) {
                break;
            }

            String response = getChatbotResponse(question);
            System.out.println("Chatbot: " + response);

            // Log the interaction
            InteractionLogger.UserFeedback feedback = getUserFeedback();
            InteractionLogger.FeedbackMetadata metadata = getFeedbackMetadata(startTime);
            JSONObject log = InteractionLogger.logInteraction(userId, question, response, feedback, metadata);
            
            // Here you would typically send this log to a database or file
            System.out.println("Interaction logged: " + log.toString(2));
        }
    }

    public static String getUserInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    // Fizik testi
    public static void runPhysicsTest() {
        String prompt = "Lise yks fizik konularından 21 adet 4 şıklı test sorusu hazırla. Soruların seçeneklerini yukarıdan aşağıya olacak şekilde göstermelisin. Mutlaka bütün soruları olması gerektiği gibi göster";
        String testQuestions = getPhysicsTest(prompt);
        System.out.println("Fizik Testi:\n" + testQuestions);

        // Soruları hazırlama
        String[] questions = testQuestions.split("\n\n");
        String[] correctAnswers = new String[21];
        for (int i = 0; i < questions.length; i++) {
            String question = questions[i];
            int answerIndex = question.lastIndexOf("Doğru Cevap:");
            if (answerIndex != -1) {
                correctAnswers[i] = question.substring(answerIndex + 13, answerIndex + 14);
            }
        }

        // Skor sistemi
        int score = 0;
        for (int i = 0; i < 21; i++) {
            String answer = getUserInput("Soru " + (i + 1) + " için cevabınızı girin (A, B, C, veya D): ");
            String question = questions[i];
            AnswerResult result = checkAnswer(question, answer);
            if (result.isCorrect) {
                System.out.println("Doğru!");
                score++;
            } else {
                System.out.println("Yanlış. Doğru cevap: " + result.correctAnswer);
            }
        }

        System.out.println("Skorunuz 21 üzerinden: " + score);
    }
    // Soruların doğru veya yanlış olmasını kontrol etme
    private static class AnswerResult {
        boolean isCorrect;
        String correctAnswer;

        AnswerResult(boolean isCorrect, String correctAnswer) {
            this.isCorrect = isCorrect;
            this.correctAnswer = correctAnswer;
        }
    }
    // Soru kontrolunde LLM kısmı
    public static AnswerResult checkAnswer(String question, String userAnswer) {
        String prompt = "Aşağıdaki soruyu ve kullanıcının cevabını kontrol et. Cevap doğru mu? " +
                        "Eğer yanlışsa, doğru cevabı da belirt. Cevabını şu formatta ver: 'Doğru/Yanlış, (Doğru Cevap)'\n\n" +
                        "Soru:\n" + question + "\n\n" +
                        "Kullanıcının cevabı: " + userAnswer;

        String response = getChatbotResponse(prompt);
        String[] parts = response.split(",");
        boolean isCorrect = parts[0].trim().equalsIgnoreCase("Doğru");
        String correctAnswer = parts.length > 1 ? parts[1].trim() : userAnswer;

        return new AnswerResult(isCorrect, correctAnswer);
    }
    
    // Cevaplar için
    public static String getChatbotResponse(String question) {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject()
                .put("role", "system")
                .put("content", "Sen bir fizik öğretmeni asistansın ve sana verilen talimatlar doğrultusunda en iyi cevabı üreteceksin. Türkçe cevap vereceksin."))
            .put(new JSONObject()
                .put("role", "user")
                .put("content", question));

        return getLLMResponse(jsonData);
    }
    // Sorular için
    private static String getPhysicsTest(String prompt) {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject()
                .put("role", "system")
                .put("content", "Sen bir lise fizik öğretmeni asistansın. Öğrencilere yönelik sorular hazırlayacaksın."))
            .put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));

        return getLLMResponse(jsonData);
    }
    // LLM Ayarları
    public static String getLLMResponse(JSONArray jsonData) {
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
        } catch (Exception e) {
            e.printStackTrace();
            return "İsteğinizi gerçekleştirmeye çalışırken bir sorun oluştu.";
        }
    }

    public static String convertToSpecialFormat(JSONArray jsonData) {
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

    private static String generateUserId() {
        // Implement a method to generate or retrieve a unique user ID
        return "user_" + System.currentTimeMillis();
    }

    private static InteractionLogger.UserFeedback getUserFeedback() {
        // Implement a method to get user feedback
        System.out.print("Rate the response (like/dislike): ");
        String rating = scanner.nextLine();
        System.out.print("Any additional feedback? ");
        String feedbackText = scanner.nextLine();
        
        InteractionLogger.UserFeedback feedback = new InteractionLogger.UserFeedback();
        feedback.setRating(rating);
        feedback.setFeedbackText(feedbackText);
        // You can also ask for preferred_response if the user dislikes the answer
        return feedback;
    }

    private static InteractionLogger.FeedbackMetadata getFeedbackMetadata(long startTime) {
        // Implement a method to get feedback metadata
        InteractionLogger.FeedbackMetadata metadata = new InteractionLogger.FeedbackMetadata();
        metadata.setDevice("desktop"); // Or get this from system properties
        metadata.setLocation("unknown"); // You might want to implement location detection
        metadata.setSessionDuration((System.currentTimeMillis() - startTime) / 1000); // Duration in seconds
        return metadata;
    }
}