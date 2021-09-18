package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.core.view.MarginLayoutParamsCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public SQLiteDatabase db;
    public ArrayList<HashMap<String, Object>> contacts = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        Cursor currentSocket = db.rawQuery("Select * from sockets", null);
        currentSocket.moveToFirst();

        Button info = findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Info.class);
                intent.putExtra("contactId", currentSocket.getString(4));
                intent.putExtra("otherContactId", currentSocket.getString(4));
                MainActivity.this.startActivity(intent);
            }
        });

        String url = "https://messengerserv.herokuapp.com/contacts/list";
        JSONArray responseJson = null;
        try {
            responseJson = new FetchTask<JSONArray>().execute(url).get();
            for (int i = 0; i < responseJson.length(); i++){

                String avatar = responseJson.getJSONObject(i).getString("avatar");
                String name = responseJson.getJSONObject(i).getString("name");
                String id = responseJson.getJSONObject(i).getString("_id");

                ImageButton currentContactAvatar = new ImageButton(MainActivity.this);
                currentContactAvatar.setLayoutParams(new ConstraintLayout.LayoutParams(115, 115));

                try {
                    Bitmap uploadedImg = new FetchTask<Bitmap>(currentContactAvatar).execute("https://opalescent-soapy-baseball.glitch.me/contacts/getavatar/?contactid=1&path=abc").get();
                    currentContactAvatar.setImageBitmap(uploadedImg);
                } catch (Exception e) {

                }
                TextView currentContactName = new TextView(MainActivity.this);
                currentContactName.setText(name.toString());
                LinearLayout layoutOfContacts = findViewById(R.id.layoutOfContacts);
                LinearLayout contactLayout = new LinearLayout(MainActivity.this);
                ConstraintLayout.LayoutParams contactLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                contactLayoutParams.setMargins(5,75,5, 75);
                contactLayout.setLayoutParams(contactLayoutParams);
                layoutOfContacts.addView(contactLayout);
                contactLayout.setOrientation(LinearLayout.HORIZONTAL);
                contactLayout.addView(currentContactAvatar);
                contactLayout.addView(currentContactName);
                contactLayout.setContentDescription(id.toString());
                contactLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Chat.class);
                        intent.putExtra("otherContactId", contactLayout.getContentDescription());
                        intent.putExtra("contactId", currentSocket.getString(4));
                        MainActivity.this.startActivity(intent);
                    }
                });
                currentContactAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Info.class);
                        intent.putExtra("otherContactId", contactLayout.getContentDescription());
                        intent.putExtra("contactId", currentSocket.getString(4));

                        MainActivity.this.startActivity(intent);
                    }
                });

            }

        } catch(Exception e) {
            Log.d("mytag", "ошибка запроса: " + url + " " + e);
        }

        Button addContactBtn = findViewById(R.id.addContactBtn);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Search.class);
                intent.putExtra("contactId", currentSocket.getString(3));
                MainActivity.this.startActivity(intent);
            }
        });
    }
}