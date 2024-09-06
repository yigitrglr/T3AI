package com.novatronrehberim.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AIServiceTest {

    @Mock
    private AIService aiService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetChatbotResponse() throws Exception {
        String prompt = "Test prompt";
        String systemMessage = "Test system message";
        String conversationHistory = "Test conversation history";
        String expectedResponse = "Mocked response";
        when(aiService.getChatbotResponse(prompt, systemMessage, conversationHistory)).thenReturn(expectedResponse);

        String result = aiService.getChatbotResponse(prompt, systemMessage, conversationHistory);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testConvertToSpecialFormat() {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "System message"))
            .put(new JSONObject().put("role", "human").put("content", "User message"))
            .put(new JSONObject().put("role", "assistant").put("content", "Assistant message"));

        String expectedResult = "<|im_start|>system\nSystem message<|im_end|>\n" +
                                "<|im_start|>human\nUser message<|im_end|>\n" +
                                "<|im_start|>assistant\nAssistant message<|im_end|>\n" +
                                "<|im_start|>assistant\n";

        when(aiService.convertToSpecialFormat(jsonData)).thenReturn(expectedResult);

        String result = aiService.convertToSpecialFormat(jsonData);

        assertNotNull(result);
        assertTrue(result.contains("<|im_start|>system\nSystem message<|im_end|>"));
        assertTrue(result.contains("<|im_start|>human\nUser message<|im_end|>"));
        assertTrue(result.contains("<|im_start|>assistant\nAssistant message<|im_end|>"));
        assertTrue(result.endsWith("<|im_start|>assistant\n"));
    }

    @Test
    void testGetLLMResponse() throws Exception {
        JSONArray jsonData = new JSONArray().put(new JSONObject().put("role", "user").put("content", "Test"));
        String expectedResponse = "Mocked LLM response";
        when(aiService.getLLMResponse(jsonData)).thenReturn(expectedResponse);

        String result = aiService.getLLMResponse(jsonData);
        assertEquals(expectedResponse, result);
    }

    @Test
    void testSendPostRequest() throws Exception {
        String url = "https://api.example.com";
        String requestBody = "Test request body";
        String expectedResponse = "Mocked API response";
        when(aiService.sendPostRequest(url, requestBody)).thenReturn(expectedResponse);

        String result = aiService.sendPostRequest(url, requestBody);
        assertEquals(expectedResponse, result);
    }
}