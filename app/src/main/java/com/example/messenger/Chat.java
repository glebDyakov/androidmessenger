package com.example.messenger;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Chat  extends AppCompatActivity {

    public MediaPlayer audio;
    public String contactId = "";
    public String otherContactId = "";
    public SQLiteDatabase db;
    public ArrayList<HashMap<Object, String>> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        audio = MediaPlayer.create(getApplicationContext(), R.raw.sendmessagesnd);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            EditText messageText = findViewById(R.id.messageText);
            contactId = extras.getString("contactId");
            otherContactId = extras.getString("otherContactId");

            @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

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

            Log.d("mytag", "otherContactId: " + otherContactId);
        }

        try {
            String url = "https://messengerserv.herokuapp.com/contacts/get/?id=" + contactId;

            JSONArray messagesJson = new FetchTask<JSONObject>().execute(url).get().getJSONArray("messages");

            for(int i = 0; i < messagesJson.length(); i++){

                String text = messagesJson.getJSONObject(i).getString("message");
                String messageId = messagesJson.getJSONObject(i).getString("id");
                String otherMessageId = messagesJson.getJSONObject(i).getString("otherMessageId");

                if(otherMessageId.toString().contains(otherContactId)){
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

                    String url = "https://opalescent-soapy-baseball.glitch.me/contacts/messages/add/?contactid=" + contactId + "&othercontactid=" + otherContactId + "&message=" + messageText.getText().toString();

                    JSONObject responseJson = new FetchTask<JSONObject>().execute(url).get();
                    if(responseJson.getString("status").contains("OK")) {
                        Log.d("mytag", "cообщение отправлено");
                    } else {
                        Log.d("mytag", "cообщение не отправлено");
                    }
                } catch (Exception e){
                    Log.d("mytag", "contactId: " + contactId +  ", otherContactId: " + otherContactId);
                }


                Button messageBtn = new Button(Chat.this);
                LinearLayout contactMessage = new LinearLayout(Chat.this);

                messageBtn.setText(messageText.getText().toString());

                LinearLayout layoutOfMessages = findViewById(R.id.layoutOfMessages);
                layoutOfMessages.addView(contactMessage);
                contactMessage.addView(messageBtn);

                contactMessage.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

                audio.start();

                Intent intent = new Intent(Chat.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(Chat.this, 0, intent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "CONTACTIO_CHANNEL_ID")
                        .setSmallIcon(R.drawable.barcode)
                        .setContentTitle(contactId)
                        .setContentText(messageText.getText().toString())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    channel = new NotificationChannel("CONTACTIO_CHANNEL_ID", "channel for transfer messages between sockets", importance);
                    channel.setDescription("transfer messages between sockets");
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify((int) ((new Date(System.currentTimeMillis()).getTime() / 1000L) % Integer.MAX_VALUE) /* ID of notification */, builder.build());
                }

                messageText.setText("");

            }
        });

    }
}
