package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Register  extends AppCompatActivity {

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sockets (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phone TEXT, avatar TEXT);");
        if((int) DatabaseUtils.queryNumEntries(db, "sockets") >= 1) {
            Intent intent = new Intent(Register.this, MainActivity.class);
            Register.this.startActivity(intent);
        }

        Button registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputPhone = findViewById(R.id.inputPhone);
                db.execSQL("INSERT INTO \"sockets\"(name, phone, avatar) VALUES (\"" + inputPhone.getText().toString() + "\", \"" + inputPhone.getText().toString() + "\", \"" + "empty" + "\");");
                Intent intent = new Intent(Register.this, MainActivity.class);
                Register.this.startActivity(intent);
            }
        });
    }
}
