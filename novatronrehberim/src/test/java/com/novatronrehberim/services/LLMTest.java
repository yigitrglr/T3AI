package com.novatronrehberim.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.json.JSONArray;
import org.json.JSONObject;
import com.novatronrehberim.LLM;

public class LLMTest {

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
    void testConvertToSpecialFormat() {
        JSONArray jsonData = new JSONArray()
            .put(new JSONObject().put("role", "system").put("content", "system"))
            .put(new JSONObject().put("role", "user").put("content", "user"))
            .put(new JSONObject().put("role", "assistant").put("content", "assistant"));

        String result = LLM.convertToSpecialFormat(jsonData);

        assertTrue(result.contains("<|im_start|>system\nsystem<|im_end|>"));
        assertTrue(result.contains("<|im_start|>human\nuser<|im_end|>"));
        assertTrue(result.contains("<|im_start|>assistant\nassistant<|im_end|>"));
        assertTrue(result.endsWith("<|im_start|>assistant\n"));
    }

    @Test
    void testCheckAnswer() {
        String question = "Test question\nA) Option A\nB) Option B\nC) Option C\nD) Option D\nDoğru Cevap: B";
        String userAnswer = "B";

        try (MockedStatic<LLM> mockedLLM = Mockito.mockStatic(LLM.class)) {
            when(LLM.getChatbotResponse(anyString())).thenReturn("Doğru, B");
            mockedLLM.when(() -> LLM.checkAnswer(anyString(), anyString())).thenCallRealMethod();
            
            LLM.AnswerResult result = LLM.checkAnswer(question, userAnswer);

            assertTrue(result.isCorrect);
            assertEquals("B", result.correctAnswer);
        }
    }

}
