package com.example.messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class Chat  extends AppCompatActivity {

    public ArrayList<HashMap<Object, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            EditText messageText = findViewById(R.id.messageText);
            String id = extras.getString("contactId");
            messageText.setText(id);
            Log.d("mytag", id);
        }

        messages = new ArrayList<HashMap<Object, String>>();
        HashMap<Object,String> newMessageOne = new HashMap<Object,String>();
        newMessageOne.put("text", "abcdefghjkl");
        newMessageOne.put("sender", "admin");
        newMessageOne.put("isSender", "true");
        messages.add(newMessageOne);
        newMessageOne = new HashMap<Object,String>();
        newMessageOne.put("text", "abcdefghjkl");
        newMessageOne.put("sender", "admin");
        newMessageOne.put("isSender", "true");
        messages.add(newMessageOne);
        newMessageOne = new HashMap<Object,String>();
        newMessageOne.put("text", "abcdefghjkl");
        newMessageOne.put("sender", "admin");
        newMessageOne.put("isSender", "true");
        messages.add(newMessageOne);

        for(HashMap<Object, String> message : messages){
            Button messageBtn = new Button(Chat.this);
            messageBtn.setText(message.get("text"));
            LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
            layoutOfMessages.addView(messageBtn);
        }

        Button sendMsgBtn = findViewById(R.id.sendMsgBtn);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<Object,String> newMessage = new HashMap<Object,String>();
                newMessage.put("text", "abcdefghjkl");
                newMessage.put("sender", "admin");
                newMessage.put("isSender", "true");
                messages.add(newMessage);
            }
        });

    }
}
