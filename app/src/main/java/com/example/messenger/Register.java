package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.json.*;

public class Register  extends AppCompatActivity {

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

//        db.execSQL("CREATE TABLE IF NOT EXISTS sockets (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, avatar TEXT, id TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS sockets (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, avatar TEXT, id TEXT);");

        db.execSQL("CREATE TABLE IF NOT EXISTS otherscontacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, avatar TEXT, id TEXT);");

        if((int) DatabaseUtils.queryNumEntries(db, "sockets") >= 1) {
            Intent intent = new Intent(Register.this, MainActivity.class);
            Register.this.startActivity(intent);
        }

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String url = "https://messengerserv.herokuapp.com/contacts/create?name=flatingo&phone=89254683410";
//                String responseJson = "{ \"status\": \"Error\" }";
//                try {
//                    responseJson = URLConnectionReader.getText(url);
//                } catch(Exception e) {
//                    Log.d("mytag", "ошибка запроса: " + url);
//                }
//                try {
//                    JSONObject obj = new JSONObject(responseJson);
//                    String responseStatus = obj.getString("status");
//                    String responseId = obj.getString("id");
//                    if(responseStatus.contains("OK")){

                    try {
                        EditText inputPhone = findViewById(R.id.inputPhone);
                        String url = "https://messengerserv.herokuapp.com/contacts/create/?name=" + inputPhone.getText() + "&phone=" + inputPhone.getText();
                        JSONObject responseJson = new FetchTask<JSONObject>().execute(url).get();
                        if(responseJson.getString("status").contains("OK")) {

//                            db.execSQL("INSERT INTO \"sockets\"(name, phone, avatar, id) VALUES (\"" + inputPhone.getText().toString() + "\", \"" + inputPhone.getText().toString() + "\", \"" + "empty" + "\", \"" + "responseId" + "\");");
                            db.execSQL("INSERT INTO \"sockets\"(name, phone, avatar, id) VALUES (\"" + inputPhone.getText().toString() + "\", \"" + inputPhone.getText().toString() + "\", \"" + "empty" + "\", \"" + responseJson.getString("id") + "\");");

                            Intent intent = new Intent(Register.this, MainActivity.class);
                            Register.this.startActivity(intent);
                        }
                    } catch(Exception e) {

                    }
//                        Log.d("mytag", "создание контакта в mongodb");
//                    } else if(responseStatus.contains("Error")) {
//                        Log.d("mytag", "ошибка создание контакта");
//                    }
//                } catch(JSONException e) {
//                    Log.d("mytag", "ошибка парсинга json");
//                }
            }
        });
    }
}
