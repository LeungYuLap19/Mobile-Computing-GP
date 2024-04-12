package com.mobileComputing.groupProject.services.firebaseMessaging;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TokenManageService {
    private static final String SERVER_URL = "http://10.0.2.2:3000/fcm-token/";
    private static final String STORE_TOKEN = "store-token";
    private static final String REMOVE_TOKEN = "remove-token";

    public static void storeToken(final String userId) {
        // Get the device token asynchronously
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FCM_token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("FCM_token", token);

                        // Start AsyncTask to send token to server
                        new SendTokenTask().execute(userId, token);
                    }
                });
    }

    private static class SendTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String userId = params[0];
            String token = params[1];
            sendTokenToServer(userId, token);
            return null;
        }
    }

    private static void sendTokenToServer(String userId, String token) {
        try {
            Log.d("FCM_toke", "running request");
            String payload = "{\"userid\":\"" + userId + "\",\"token\":\"" + token + "\"}";

            URL url = new URL(SERVER_URL + STORE_TOKEN);

            // Create the HttpURLConnection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Write the payload to the connection's output stream
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Token stored successfully
                Log.d("FCM_token", "Token stored successfully.");
            } else {
                // Handle error
                Log.d("FCM_token", "Failed to store token. Response code: " + responseCode);
            }

            // Close the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // remove tokens

    public static void removeToken(final String userId) {
        FirebaseMessaging.getInstance().deleteToken()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FCM_token", "Failed to delete FCM registration token", task.getException());
                            return;
                        }

                        // Token deleted successfully
                        Log.d("FCM_token", "Token deleted successfully.");

                        // Start AsyncTask to remove token from the server
                        new RemoveTokenTask().execute(userId);
                    }
                });
    }

    private static class RemoveTokenTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            String userId = params[0];
            removeTokenFromServer(userId);
            return null;
        }
    }

    private static void removeTokenFromServer(String userId) {
        try {
            Log.d("FCM_token", "Running remove token request");

            // Create the HttpURLConnection
            URL url = new URL(SERVER_URL + REMOVE_TOKEN);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Write the payload to the connection's output stream
            String payload = "{\"userid\":\"" + userId + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check the response code
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Token removed successfully
                Log.d("FCM_token", "Token removed successfully.");
            } else {
                // Handle error
                Log.d("FCM_token", "Failed to remove token. Response code: " + responseCode);
            }

            // Close the connection
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
