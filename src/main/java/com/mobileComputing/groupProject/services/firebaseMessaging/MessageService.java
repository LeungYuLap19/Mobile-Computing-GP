package com.mobileComputing.groupProject.services.firebaseMessaging;

import android.os.AsyncTask;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MessageService {
    private static final String SERVER_URL = "http://10.0.2.2:3000/fcm-msg/";
    private static final String SEND_MESSAGE = "send-message";
    private static final String SEND_MULTIPLE_MESSAGES = "send-multiple-messages";

    public static void sendMessage(final String title, final String text, final String userId) {
        // Start AsyncTask to send message to server
        new SendMessageTask().execute(title, text, userId);
    }

    public static void sendMultipleMessages(final String title, final String text, final List<String> userIds) {
        // Start AsyncTask to send multiple messages to server
        new SendMultipleMessagesTask().execute(title, text, userIds);
    }

    private static class SendMessageTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String title = params[0];
            String text = params[1];
            String userId = params[2];
            sendMessageToServer(title, text, userId);
            return null;
        }
    }

    private static class SendMultipleMessagesTask extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            String title = (String) params[0];
            String text = (String) params[1];
            List<String> userIds = (List<String>) params[2];
            sendMultipleMessagesToServer(title, text, userIds);
            return null;
        }
    }

    private static void sendMessageToServer(String title, String text, String userId) {
        try {
            Log.d("FCM_message", "Running send message request");

            // Create the HttpURLConnection
            URL url = new URL(SERVER_URL + SEND_MESSAGE);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            String payload = "{\"title\":\"" + title + "\",\"text\":\"" + text + "\",\"userid\":\"" + userId + "\"}";

            // Write the payload to the connection's output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Message sent successfully
                Log.d("FCM_message", "Message sent successfully.");
            } else {
                // Handle error
                Log.d("FCM_message", "Failed to send message. Response code: " + responseCode);
            }

            // Close the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendMultipleMessagesToServer(String title, String text, List<String> userIds) {
        try {
            Log.d("FCM_message", "Running send multiple messages request");

            // Create the HttpURLConnection
            URL url = new URL(SERVER_URL + SEND_MULTIPLE_MESSAGES);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Create JSON payload
            StringBuilder payloadBuilder = new StringBuilder();
            payloadBuilder.append("{\"title\":\"").append(title).append("\",\"text\":\"").append(text).append("\",\"userids\":[");

            for (int i = 0; i < userIds.size(); i++) {
                payloadBuilder.append("\"").append(userIds.get(i)).append("\"");
                if (i < userIds.size() - 1) {
                    payloadBuilder.append(",");
                }
            }

            payloadBuilder.append("]}");

            String payload = payloadBuilder.toString();

            // Write the payload to the connection's output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Messages sent successfully
                Log.d("FCM_message", "Messages sent successfully.");
            } else {
                // Handle error
                Log.d("FCM_message", "Failed to send messages. Response code: " + responseCode);
            }

            // Close the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
