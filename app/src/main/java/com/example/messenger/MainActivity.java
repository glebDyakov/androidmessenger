package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.core.view.MarginLayoutParamsCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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

//        HashMap<String, Object> contactOne = new HashMap<String, Object>();
//        contactOne.put("id", "1");
//        contactOne.put("name", "Роман Сакутин");
//        contactOne.put("lastMessage", "Хэй, мен...");
//        contactOne.put("avatar", "five");
//        contacts.add(contactOne);
//        HashMap<String, Object> contactTwo = new HashMap<String, Object>();
//        contactTwo.put("id", "2");
//        contactTwo.put("name", "Арт Аласки");
//        contactTwo.put("lastMessage", "Доброго времени уток");
//        contactTwo.put("avatar", "cross");
//        contacts.add(contactTwo);
//        HashMap<String, Object> contactThree = new HashMap<String, Object>();
//        contactThree.put("id", "3");
//        contactThree.put("name", "Флатинго");
//        contactThree.put("lastMessage", "Привет гей девелоперы");
//        contactThree.put("avatar", "camera");
//        contacts.add(contactThree);

//        contacts.clear();
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
                if(avatar.toString().contains("five")){
                    currentContactAvatar.setImageResource(R.drawable.five);
                } else if(avatar.toString().contains("barcode")){
                    currentContactAvatar.setImageResource(R.drawable.barcode);
                } else if(avatar.toString().contains("magnet")){
                    currentContactAvatar.setImageResource(R.drawable.magnet);
                } else if(avatar.toString().contains("camera")){
                    currentContactAvatar.setImageResource(R.drawable.camera);
                } else if(avatar.toString().contains("cross")){
                    currentContactAvatar.setImageResource(R.drawable.cross);
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
                        intent.putExtra("contactId", contactLayout.getContentDescription());
                        MainActivity.this.startActivity(intent);
                    }
                });
                currentContactAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Info.class);
                        intent.putExtra("contactId", contactLayout.getContentDescription());
                        MainActivity.this.startActivity(intent);
                    }
                });

            }

        } catch(Exception e) {
            Log.d("mytag", "ошибка запроса: " + url + " " + e);
        }

//        @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS otherscontacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, avatar TEXT);");
//        Cursor otherscontacts = db.rawQuery("Select * from otherscontacts", null);
//        otherscontacts.moveToFirst();
//        if(DatabaseUtils.queryNumEntries(db, "otherscontacts") >= 1) {
//            for(int otherContactRecordIdx = 0; otherContactRecordIdx < DatabaseUtils.queryNumEntries(db, "otherscontacts"); otherContactRecordIdx++) {
//                ImageButton currentContactAvatar = new ImageButton(MainActivity.this);
//                currentContactAvatar.setLayoutParams(new ConstraintLayout.LayoutParams(115, 115));
//                if (otherscontacts.getString(3).toString().contains("five")) {
//                    currentContactAvatar.setImageResource(R.drawable.five);
//                } else if (otherscontacts.getString(3).toString().contains("barcode")) {
//                    currentContactAvatar.setImageResource(R.drawable.barcode);
//                } else if (otherscontacts.getString(3).toString().contains("magnet")) {
//                    currentContactAvatar.setImageResource(R.drawable.magnet);
//                } else if (otherscontacts.getString(3).toString().contains("camera")) {
//                    currentContactAvatar.setImageResource(R.drawable.camera);
//                } else if (otherscontacts.getString(3).toString().contains("cross")) {
//                    currentContactAvatar.setImageResource(R.drawable.cross);
//                }
//                TextView currentContactName = new TextView(MainActivity.this);
//                currentContactName.setText(otherscontacts.getString(1).toString());
//                TextView currentContactLastMessage = new TextView(MainActivity.this);
//                currentContactLastMessage.setText(otherscontacts.getString(1).toString());
//                LinearLayout layoutOfContacts = findViewById(R.id.layoutOfContacts);
//                LinearLayout contactLayout = new LinearLayout(MainActivity.this);
//                ConstraintLayout.LayoutParams contactLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//                contactLayoutParams.setMargins(5, 75, 5, 75);
//                contactLayout.setLayoutParams(contactLayoutParams);
//                layoutOfContacts.addView(contactLayout);
//                contactLayout.setOrientation(LinearLayout.HORIZONTAL);
//                contactLayout.addView(currentContactAvatar);
//                contactLayout.addView(currentContactName);
//                contactLayout.addView(currentContactLastMessage);
//                contactLayout.setContentDescription(String.valueOf(otherscontacts.getInt(0)));
//                contactLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(MainActivity.this, Chat.class);
//                        intent.putExtra("contactId", contactLayout.getContentDescription());
//                        MainActivity.this.startActivity(intent);
//                    }
//                });
//                currentContactAvatar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(MainActivity.this, Info.class);
//                        intent.putExtra("contactId", contactLayout.getContentDescription());
//                        MainActivity.this.startActivity(intent);
//                    }
//                });
//            }
//        } else if(DatabaseUtils.queryNumEntries(db, "otherscontacts") <= 0){
//            LinearLayout layoutOfContacts = findViewById(R.id.layoutOfContacts);
//            TextView notContacts = new TextView(MainActivity.this);
//            notContacts.setText("Нет контаков");
//            layoutOfContacts.addView(notContacts);
//        }

        Button addContactBtn = findViewById(R.id.addContactBtn);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Search.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}