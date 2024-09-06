package com.novatronrehberim.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.json.JSONArray;
import org.json.JSONObject;
import com.novatronrehberim.LLM;

class LLMTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testGetUserChoice() {
        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        int result = LLM.getUserChoice();
        
        assertEquals(2, result);
        assertTrue(outContent.toString().contains("YKS Çalışma Programına Hoş geldiniz!"));
    }

    @Test
    void testConvertToSpecialFormat() {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "System message"))
            .put(new JSONObject().put("role", "user").put("content", "User message"))
            .put(new JSONObject().put("role", "assistant").put("content", "Assistant message"));

        String result = LLM.convertToSpecialFormat(jsonData);

        assertTrue(result.contains("<|im_start|>system\nSystem message<|im_end|>"));
        assertTrue(result.contains("<|im_start|>human\nUser message<|im_end|>"));
        assertTrue(result.contains("<|im_start|>assistant\nAssistant message<|im_end|>"));
        assertTrue(result.endsWith("<|im_start|>assistant\n"));
    }

    @Test
    void testGetChatbotResponse() throws Exception {
        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.getLLMResponse(any())).thenReturn("Mocked response");
            mockedLLM.when(() -> LLM.getChatbotResponse(anyString())).thenCallRealMethod();
            
            String result = LLM.getChatbotResponse("Test question");
            
            assertEquals("Mocked response", result);
            mockedLLM.verify(() -> LLM.getLLMResponse(any()));
        }
    }

    @Test
    void testRunChatbot() throws Exception {
        ByteArrayInputStream inContent = new ByteArrayInputStream("Test question\n:q\n".getBytes());
        System.setIn(inContent);

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.getChatbotResponse(anyString())).thenReturn("Mocked response");
            mockedLLM.when(LLM::generateUserId).thenReturn("test_user_id");
            mockedLLM.when(() -> LLM.getUserInput(anyString())).thenCallRealMethod();
            mockedLLM.when(LLM::getUserFeedback).thenReturn(new InteractionLogger.UserFeedback());
            mockedLLM.when(LLM::runChatbot).thenCallRealMethod();
            
            LLM.runChatbot();

            String output = outContent.toString();
            assertTrue(output.contains("Chatbot: Mocked response"));
            assertTrue(output.contains("Interaction logged:"));
        }
    }

    @Test
    void testRunPhysicsTest() throws Exception {
        ByteArrayInputStream inContent = new ByteArrayInputStream("A\nB\nC\nD\n".repeat(21).getBytes());
        System.setIn(inContent);

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.getPhysicsTest(anyString())).thenReturn("Question 1\nA) Option A\nB) Option B\nC) Option C\nD) Option D\nDoğru Cevap: A\n\n".repeat(21));
            mockedLLM.when(() -> LLM.getUserInput(anyString())).thenCallRealMethod();
            mockedLLM.when(() -> LLM.checkAnswer(anyString(), anyString())).thenReturn(new LLM.AnswerResult(true, "A"));
            mockedLLM.when(LLM::runPhysicsTest).thenCallRealMethod();
            
            LLM.runPhysicsTest();

            String output = outContent.toString();
            assertTrue(output.contains("Fizik Testi:"));
            assertTrue(output.contains("Doğru!"));
            assertTrue(output.contains("Skorunuz 21 üzerinden: 21"));
        }
    }

    @Test
    void testCheckAnswer() {
        String question = "Test question\nA) Option A\nB) Option B\nC) Option C\nD) Option D\nDoğru Cevap: B";
        String userAnswer = "B";

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.getChatbotResponse(anyString())).thenReturn("Doğru, B");
            mockedLLM.when(() -> LLM.checkAnswer(anyString(), anyString())).thenCallRealMethod();
            
            LLM.AnswerResult result = LLM.checkAnswer(question, userAnswer);

            assertTrue(result.isCorrect);
            assertEquals("B", result.correctAnswer);
        }
    }

    @Test
    void testGetLLMResponse() throws Exception {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "You are a helpful assistant."))
            .put(new JSONObject().put("role", "user").put("content", "Hello, how are you?"));

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.convertToSpecialFormat(any())).thenCallRealMethod();
            mockedLLM.when(() -> LLM.sendPostRequest(anyString(), anyString())).thenReturn("{\"choices\":[{\"text\":\"I'm doing well, thank you for asking!\"}]}");
            mockedLLM.when(() -> LLM.getLLMResponse(any(JSONArray.class))).thenCallRealMethod();
            
            String response = LLM.getLLMResponse(jsonData);

            assertEquals("I'm doing well, thank you for asking!", response);
        }
    }

    @Test
    void testSendPostRequest() throws Exception {
        String url = "https://example.com/api";
        String payload = "{\"key\":\"value\"}";
        String expectedResponse = "{\"result\":\"success\"}";

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(() -> LLM.sendPostRequest(url, payload)).thenReturn(expectedResponse);
            
            String response = LLM.sendPostRequest(url, payload);

            assertEquals(expectedResponse, response);
        }
    }
}
