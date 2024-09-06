package com.novatronrehberim;

import java.time.Instant;
import java.util.UUID;
import org.json.JSONObject;

public class InteractionLogger {

    public static JSONObject logInteraction(String userId, String inputPrompt, String response, 
                                            UserFeedback userFeedback, FeedbackMetadata metadata) {
        JSONObject log = new JSONObject();

        // 1. interaction_id
        log.put("interaction_id", UUID.randomUUID().toString());

        // 2. user_id
        log.put("user_id", userId);

        // 3. timestamp
        log.put("timestamp", Instant.now().toString());

        // 4. content_generated
        JSONObject contentGenerated = new JSONObject();
        contentGenerated.put("input_prompt", inputPrompt);
        contentGenerated.put("response", response);
        log.put("content_generated", contentGenerated);

        // 5. user_feedback
        if (userFeedback != null) {
            JSONObject feedback = new JSONObject();
            feedback.put("rating", userFeedback.getRating());
            feedback.put("feedback_text", userFeedback.getFeedbackText());
            feedback.put("preferred_response", userFeedback.getPreferredResponse());
            log.put("user_feedback", feedback);
        }

        // 6. feedback_metadata
        if (metadata != null) {
            JSONObject feedbackMetadata = new JSONObject();
            feedbackMetadata.put("device", metadata.getDevice());
            feedbackMetadata.put("location", metadata.getLocation());
            feedbackMetadata.put("session_duration", metadata.getSessionDuration());
            log.put("feedback_metadata", feedbackMetadata);
        }

        return log;
    }

    // Helper classes
    public static class UserFeedback {
        private String rating;
        private String feedbackText;
        private String preferredResponse; // Add this field

        public void setRating(String rating) {
            this.rating = rating;
        }

        public void setFeedbackText(String feedbackText) {
            this.feedbackText = feedbackText;
        }

        public String getPreferredResponse() {
            return preferredResponse;
        }

        public void setPreferredResponse(String preferredResponse) {
            this.preferredResponse = preferredResponse;
        }

        public String getFeedbackText() {
            return feedbackText;
        }

        public String getRating() {
            return rating;
        }

        // Add getters if needed
    }

    public static class FeedbackMetadata {
        private String device;
        private String location;
        private long sessionDuration;

        public void setDevice(String device) {
            this.device = device;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setSessionDuration(long sessionDuration) {
            this.sessionDuration = sessionDuration;
        }

        public String getDevice() {
            return device;
        }

        public String getLocation() {
            return location;
        }

        public long getSessionDuration() {
            return sessionDuration;
        }
    }
}