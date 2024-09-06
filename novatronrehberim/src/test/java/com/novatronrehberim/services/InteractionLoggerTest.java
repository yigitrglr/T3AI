package com.novatronrehberim.services;

import org.json.JSONObject;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class InteractionLoggerTest {

    private String userId;
    private String inputPrompt;
    private String response;
    private InteractionLogger.UserFeedback userFeedback;
    private InteractionLogger.FeedbackMetadata metadata;

    @BeforeEach
    public void setUp() {
        userId = "testUser";
        inputPrompt = "Test question";
        response = "Test response";
        userFeedback = new InteractionLogger.UserFeedback();
        userFeedback.setRating("like");
        userFeedback.setFeedbackText("Great response!");
        metadata = new InteractionLogger.FeedbackMetadata();
        metadata.setDevice("desktop");
        metadata.setLocation("test location");
        metadata.setSessionDuration(1000);
    }

    @Test
    public void testLogInteraction() {
        JSONObject log = InteractionLogger.logInteraction(userId, inputPrompt, response, userFeedback, metadata);

        assertNotNull(log);
        assertEquals(userId, log.getString("user_id"));
        assertEquals(inputPrompt, log.getJSONObject("content_generated").getString("input_prompt"));
        assertEquals(response, log.getJSONObject("content_generated").getString("response"));
        assertEquals("like", log.getJSONObject("user_feedback").getString("rating"));
        assertEquals("Great response!", log.getJSONObject("user_feedback").getString("feedback_text"));
        assertEquals("desktop", log.getJSONObject("feedback_metadata").getString("device"));
        assertEquals("test location", log.getJSONObject("feedback_metadata").getString("location"));
        assertEquals(1000, log.getJSONObject("feedback_metadata").getLong("session_duration"));
    }

    @Test
    public void testWriteLogToFile(@TempDir Path tempDir) throws Exception {
        Path logFile = tempDir.resolve("test_log.json");
        InteractionLogger.setLogFile(logFile.toString());

        JSONObject log1 = InteractionLogger.logInteraction(userId, inputPrompt, response, userFeedback, metadata);
        JSONObject log2 = InteractionLogger.logInteraction(userId + "2", inputPrompt + "2", response + "2", userFeedback, metadata);

        String fileContent = Files.readString(logFile);
        JSONArray logsArray = new JSONArray(fileContent);

        assertEquals(2, logsArray.length(), "File should contain 2 log entries");
        
        JSONObject actualLog1 = logsArray.getJSONObject(0);
        JSONObject actualLog2 = logsArray.getJSONObject(1);

        assertTrue(compareJSONObjects(log1, actualLog1), "First log entry should match");
        assertTrue(compareJSONObjects(log2, actualLog2), "Second log entry should match");
    }

    private boolean compareJSONObjects(JSONObject expected, JSONObject actual) {
        return expected.similar(actual);
    }

    @Test
    public void testUserFeedback() {
        InteractionLogger.UserFeedback feedback = new InteractionLogger.UserFeedback();
        feedback.setRating("dislike");
        feedback.setFeedbackText("Could be better");
        feedback.setPreferredResponse("Preferred response");

        assertEquals("dislike", feedback.getRating());
        assertEquals("Could be better", feedback.getFeedbackText());
        assertEquals("Preferred response", feedback.getPreferredResponse());
    }

    @Test
    public void testFeedbackMetadata() {
        InteractionLogger.FeedbackMetadata metadata = new InteractionLogger.FeedbackMetadata();
        metadata.setDevice("mobile");
        metadata.setLocation("New York");
        metadata.setSessionDuration(2000);

        assertEquals("mobile", metadata.getDevice());
        assertEquals("New York", metadata.getLocation());
        assertEquals(2000, metadata.getSessionDuration());
    }
}
