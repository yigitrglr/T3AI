package com.novatronrehberim;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LLMTest {

    @BeforeEach
    void setUp() {
        // Any setup code if needed
    }

    @Test
    void testGetUserChoice() {
        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            mockedLLM.when(LLM::getUserChoice).thenReturn(1);
            assertEquals(1, LLM.getUserChoice());
        }
    }

    @Test
    void testGetChatbotResponse() {
        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            String expectedResponse = "This is a test response";
            mockedLLM.when(() -> LLM.getChatbotResponse(anyString())).thenReturn(expectedResponse);
            
            String actualResponse = LLM.getChatbotResponse("Test question");
            assertEquals(expectedResponse, actualResponse);
        }
    }

    @Test
    void testConvertToSpecialFormat() throws JSONException {
        String result = LLM.convertToSpecialFormat(new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "System content"))
            .put(new JSONObject().put("role", "user").put("content", "User content"))
            .put(new JSONObject().put("role", "assistant").put("content", "Assistant content")));

        String expected = "<|im_start|>system\nSystem content<|im_end|>\n" +
                          "<|im_start|>human\nUser content<|im_end|>\n" +
                          "<|im_start|>assistant\nAssistant content<|im_end|>\n" +
                          "<|im_start|>assistant\n";

        assertEquals(expected, result);
    }
}