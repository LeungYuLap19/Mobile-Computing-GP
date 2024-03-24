package com.mobileComputing.groupProject.services.apis;

import com.mobileComputing.groupProject.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;

public class AuthAPIs {

    private static final String CONNECT_AUTH_URL = "http://localhost:3000/auth";
    private static final String NEW_USER_ROUTER = "/newUser";
    private static final String AUTH_USER_ROUTER = "/authUser";

    public AuthAPIs() {}

    private User makeAuthRequest(String router, String requestBody) throws IOException, JSONException {
        URL url = new URL(CONNECT_AUTH_URL + router);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
        dos.writeBytes(requestBody);
        dos.flush();
        dos.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        JSONObject jsonResponse = new JSONObject(response.toString());
        String message = jsonResponse.getString("message");
        JSONObject userObject = jsonResponse.getJSONObject("user");
        String responseUsername = userObject.getString("username");
        String responseEmail = userObject.getString("email");

        return new User(responseEmail, responseUsername);
    }

    public User newUser(String username, String email, String password) throws IOException, JSONException {
        String requestBody =
                "{" +
                        "\"username\":\"" + username + "\"," +
                        "\"email\":\"" + email + "\"," +
                        "\"password\":\"" + password + "\"" +
                        "}";

        return makeAuthRequest(NEW_USER_ROUTER, requestBody);
    }

    public User authUser(String email, String password) throws IOException, JSONException {
        String requestBody =
                "{" +
                        "\"email\":\"" + email + "\"," +
                        "\"password\":\"" + password + "\"" +
                        "}";

        return makeAuthRequest(AUTH_USER_ROUTER, requestBody);
    }
}
