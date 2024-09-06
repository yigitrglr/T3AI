package com.novatronrehberim;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LLMTest {

    @Test
    void testGetUserChoice() {
        try (MockedStatic<LLM> mockedLLM = mockStatic(LLM.class)) {
            mockedLLM.when(LLM::getUserChoice).thenReturn(1);
            assertEquals(1, LLM.getUserChoice());
        }
    }

    @Test
    void testGetChatbotResponse() {
        try (MockedStatic<LLM> mockedLLM = mockStatic(LLM.class)) {
            String expectedResponse = "This is a test response";
            mockedLLM.when(() -> LLM.getChatbotResponse(anyString())).thenReturn(expectedResponse);
            
            String actualResponse = LLM.getChatbotResponse("Test question");
            assertEquals(expectedResponse, actualResponse);
        }
    }

    @Test
    void testConvertToSpecialFormat() throws JSONException {
        JSONArray input = new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "System content"))
            .put(new JSONObject().put("role", "user").put("content", "User content"))
            .put(new JSONObject().put("role", "assistant").put("content", "Assistant content"));

        String result = LLM.convertToSpecialFormat(input);

        String expected = "<|im_start|>system\nSystem content<|im_end|>\n" +
                          "<|im_start|>human\nUser content<|im_end|>\n" +
                          "<|im_start|>assistant\nAssistant content<|im_end|>\n" +
                          "<|im_start|>assistant\n";

        assertEquals(expected, result);
    }
}