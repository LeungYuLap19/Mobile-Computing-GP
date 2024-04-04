package com.mobileComputing.groupProject.activities;

import static com.mobileComputing.groupProject.services.firebase.MessagingService.ACTION_UPDATE_CHAT_PAGE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobileComputing.groupProject.R;

public class MainChatActivity extends AppCompatActivity {

    Button sendButton;
    EditText messageEditText;
    LinearLayout chatLayout;

    // Broadcast receiver to update chat page
    private BroadcastReceiver updateChatPageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            String senderName = intent.getStringExtra("senderName");

            // Update the chat page with the received message and sender's name
            updateChatPage(message, senderName);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_chat);

        sendButton = findViewById(R.id.sendButton);
        messageEditText = findViewById(R.id.messageEditText);
        chatLayout = findViewById(R.id.chatLayout);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                // Send the message to the recipient using the desired method
                // (e.g., network protocol, API call, etc.)

                // Broadcast the sent message
                Intent intent = new Intent(ACTION_UPDATE_CHAT_PAGE);
                intent.putExtra("message", message);
                intent.putExtra("senderName", "You");
                sendBroadcast(intent);

                // Clear the EditText after sending the message
                messageEditText.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the broadcast receiver to receive updates
        registerReceiver(updateChatPageReceiver, new IntentFilter(ACTION_UPDATE_CHAT_PAGE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the broadcast receiver
        unregisterReceiver(updateChatPageReceiver);
    }

    private void updateChatPage(String message, String senderName) {
        TextView messageTextView = new TextView(this);
        messageTextView.setText(senderName + ": " + message);
        chatLayout.addView(messageTextView);
    }

    // ... other methods and logic for your chat page

}