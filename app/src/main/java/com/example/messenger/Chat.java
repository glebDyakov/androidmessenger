package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Chat  extends AppCompatActivity {

    public String contactId = "";
    public String otherContactId = "";

    public SQLiteDatabase db;
    public ArrayList<HashMap<Object, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            EditText messageText = findViewById(R.id.messageText);
            contactId = extras.getString("contactId");
            otherContactId = extras.getString("otherContactId");

            @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

//            Cursor otherscontacts = db.rawQuery("Select * from otherscontacts where id = " + id, null);
            Cursor otherscontacts = db.rawQuery("Select * from otherscontacts", null);

            otherscontacts.moveToFirst();

            boolean notInsertContact = false;
            if(DatabaseUtils.queryNumEntries(db, "otherscontacts") >= 1) {
                for (int othersContactsIdx = 0; othersContactsIdx < DatabaseUtils.queryNumEntries(db, "otherscontacts"); othersContactsIdx++) {
                    if (String.valueOf(otherscontacts.getInt(0)).contains(otherContactId)) {
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
                db.execSQL("INSERT INTO \"otherscontacts\"(name, phone, avatar, id) VALUES (\"" + otherContactId + "\", \"" + otherContactId + "\", \"" + "empty" + "\", \"" + otherContactId + "\");");
            }

//            if(otherscontacts == null) {
//                db.execSQL("INSERT INTO \"otherscontacts\"(name, phone, avatar) VALUES (\"" + id + "\", \"" + id + "\", \"" + "empty" + "\");");
//                Log.d("mytag", "Этот контакт ещё не прикреплён");
//            } else if(otherscontacts != null){
//                Log.d("mytag", "Этот контакт уже прикреплён");
//            }

            messageText.setText(contactId);
            Log.d("mytag", "otherContactId: " + otherContactId);
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

//        for(HashMap<Object, String> message : messages){
//            Button messageBtn = new Button(Chat.this);
//            LinearLayout contactMessage = new LinearLayout(Chat.this);
//            messageBtn.setText(message.get("text"));
//            LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
//            layoutOfMessages.addView(contactMessage);
//            contactMessage.addView(messageBtn);
//            if(message.get("isSender").toString().contains("true")){
//                contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//            } else if(!message.get("isSender").toString().contains("true")){
//                contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//            }
//        }

        try {
            String url = "https://messengerserv.herokuapp.com/contacts/get/?id=" + contactId;

//            JSONObject responseJson = new FetchTask<JSONObject>().execute(url).get();

//            JSONArray messagesJson = responseJson.getJSONArray("messages");
//            JSONArray messagesJson = new FetchTask<JSONArray>().execute(url, "messages").get();
            JSONArray messagesJson = new FetchTask<JSONArray>().execute(url).get();

            for(int i = 0; i < messagesJson.length(); i++){

                String text = messagesJson.getJSONObject(i).getString("message");
                String messageId = messagesJson.getJSONObject(i).getString("id");

                Button messageBtn = new Button(Chat.this);
                LinearLayout contactMessage = new LinearLayout(Chat.this);
                messageBtn.setText(text);
                LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
                layoutOfMessages.addView(contactMessage);
                contactMessage.addView(messageBtn);
                if(contactId.contains(messageId)){
                    contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else if(!contactId.contains(messageId)){
                    contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }
            }

        } catch (Exception e) {
            Log.d("mytag", "contactId: " + contactId +  ", otherContactId: " + otherContactId);
        }

        Button sendMsgBtn = findViewById(R.id.sendMsgBtn);
        sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText messageText = findViewById(R.id.messageText);

                try {
                    String url = "https://messengerserv.herokuapp.com/contacts/messages/add/?contactid=" + contactId + "&othercontactid=" + otherContactId + "&message=" + messageText.getText().toString();
                    JSONObject responseJson = new FetchTask<JSONObject>().execute(url).get();
                    if(responseJson.getString("status").contains("OK")) {
                        Log.d("mytag", "cообщение отправлено");
                    } else {
                        Log.d("mytag", "cообщение не отправлено");
                    }
                } catch (Exception e){
                    Log.d("mytag", "contactId: " + contactId +  ", otherContactId: " + otherContactId);
                }


                HashMap<Object,String> newMessage = new HashMap<Object,String>();
                newMessage.put("text", messageText.getText().toString());
                newMessage.put("sender", "admin");
                newMessage.put("isSender", "true");
                messages.add(newMessage);

                Button messageBtn = new Button(Chat.this);
                LinearLayout contactMessage = new LinearLayout(Chat.this);
                messageBtn.setText(messages.get(messages.toArray().length - 1).get("text"));
                LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
                layoutOfMessages.addView(contactMessage);
                contactMessage.addView(messageBtn);
                if(messages.get(messages.toArray().length - 1).get("isSender").toString().contains("true")){
                    contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else if(!messages.get(messages.toArray().length - 1).get("isSender").toString().contains("true")){
                    contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                }

//                Uri myUri = nMediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                ; // initialize Uri here
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                mediaPlayer.setAudioAttributes(
//                        new AudioAttributes.Builder()
//                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                .setUsage(AudioAttributes.USAGE_MEDIA)
//                                .build()
//                );
//                mediaPlayer.setDataSource(getApplicationContext(), myUri);
//                mediaPlayer.prepare();
//                mediaPlayer.start();

//                MediaPlayer playr = MediaPlayer.create(this,R.raw.showme);


            }
        });

    }
}
