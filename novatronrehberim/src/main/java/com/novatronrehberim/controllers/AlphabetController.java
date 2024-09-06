package com.novatronrehberim.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.novatronrehberim.services.AIService;
import com.novatronrehberim.services.TextToSpeech;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AlphabetController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/alphabet.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        AIService ai = new AIService();
        TextToSpeech voice = new TextToSpeech();
        
        // Path where the audio file will be stored temporarily
        String audioFilePath = System.getProperty("java.io.tmpdir") + "/output.mpeg";
        File audioFile = new File(audioFilePath);

        try {
            // Get the letter from the form data using getParameter
            String letter = request.getParameter("letter");

            if (letter == null || letter.isEmpty()) {
                throw new Exception("No letter provided");
            }

            // Get AI response based on the letter
            String aiResponse = ai.getChatbotResponse(letter, "user", "alfabe");

            // Set AI response as an attribute for JSP
            request.setAttribute("aiResponse", aiResponse);

            // Generate speech from the AI response
            

            // Check if the audio file was successfully created
            if (audioFile.exists()) {
                response.setContentType("audio/mpeg");
                try (FileInputStream fis = new FileInputStream(audioFile);
                     OutputStream os = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    os.flush();
                }
            }

            // Forward the request and response to the JSP page
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/alphabet.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, send an error message to the JSP
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/views/error.jsp");
            dispatcher.forward(request, response);
        }
    }
}

