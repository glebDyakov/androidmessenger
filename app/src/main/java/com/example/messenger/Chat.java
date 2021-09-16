package com.example.messenger;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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

    public SQLiteDatabase db;
    public ArrayList<HashMap<Object, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            EditText messageText = findViewById(R.id.messageText);
            String id = extras.getString("contactId");

            @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

//            Cursor otherscontacts = db.rawQuery("Select * from otherscontacts where id = " + id, null);
            Cursor otherscontacts = db.rawQuery("Select * from otherscontacts", null);

            otherscontacts.moveToFirst();

            boolean notInsertContact = false;
            if(DatabaseUtils.queryNumEntries(db, "otherscontacts") >= 1) {
                for (int othersContactsIdx = 0; othersContactsIdx < DatabaseUtils.queryNumEntries(db, "otherscontacts"); othersContactsIdx++) {
                    if (String.valueOf(otherscontacts.getInt(0)).contains(id)) {
                        notInsertContact = true;
                        Log.d("mytag", "Этот контакт уже прикреплён");
                        break;
                    }
                    if (othersContactsIdx < DatabaseUtils.queryNumEntries(db, "otherscontacts") - 1) {
                        otherscontacts.moveToNext();
                    }
                }
            }

            if(!notInsertContact){
                db.execSQL("INSERT INTO \"otherscontacts\"(name, phone, avatar) VALUES (\"" + id + "\", \"" + id + "\", \"" + "empty" + "\");");
            }

//            if(otherscontacts == null) {
//                db.execSQL("INSERT INTO \"otherscontacts\"(name, phone, avatar) VALUES (\"" + id + "\", \"" + id + "\", \"" + "empty" + "\");");
//                Log.d("mytag", "Этот контакт ещё не прикреплён");
//            } else if(otherscontacts != null){
//                Log.d("mytag", "Этот контакт уже прикреплён");
//            }

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
        newMessageOne.put("isSender", "false");
        messages.add(newMessageOne);
        newMessageOne = new HashMap<Object,String>();
        newMessageOne.put("text", "abcdefghjkl");
        newMessageOne.put("sender", "admin");
        newMessageOne.put("isSender", "true");
        messages.add(newMessageOne);

        for(HashMap<Object, String> message : messages){
            Button messageBtn = new Button(Chat.this);
            LinearLayout contactMessage = new LinearLayout(Chat.this);
            messageBtn.setText(message.get("text"));
            LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
            layoutOfMessages.addView(contactMessage);
            contactMessage.addView(messageBtn);
            if(message.get("isSender").toString().contains("true")){
                contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            } else if(!message.get("isSender").toString().contains("true")){
                contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
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
