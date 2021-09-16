package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Info  extends AppCompatActivity {

    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle extras = getIntent().getExtras();
//        if(extras != null) {
//            ImageView avatar = findViewById(R.id.avatar);
//            if(extras.getString("avatar").toString().contains("five")){
//                avatar.setImageResource(R.drawable.five);
//            } else if(extras.getString("avatar").toString().contains("camera")){
//                avatar.setImageResource(R.drawable.camera);
//            } else if(extras.getString("avatar").toString().contains("magnet")){
//                avatar.setImageResource(R.drawable.magnet);
//            } else if(extras.getString("avatar").toString().contains("barcode")){
//                avatar.setImageResource(R.drawable.barcode);
//            } else if(extras.getString("avatar").toString().contains("cross")){
//                avatar.setImageResource(R.drawable.cross);
//            }
//
//            TextView contactName = findViewById(R.id.contactName);
//            contactName.setText(extras.getString("contactName").toString());

        if(extras != null) {
            @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            Cursor selectedContact = db.rawQuery("Select * from sockets where _id = \"1\"", null);
            selectedContact.moveToFirst();
            String imgSrc = selectedContact.getString(3);
            Log.d("mytag", imgSrc);
            ImageView avatar = findViewById(R.id.avatar);
//            if(extras.getString("avatar").toString().contains("five")){
//                avatar.setImageResource(R.drawable.five);
//            } else if(extras.getString("avatar").toString().contains("camera")){
//                avatar.setImageResource(R.drawable.camera);
//            } else if(extras.getString("avatar").toString().contains("magnet")){
//                avatar.setImageResource(R.drawable.magnet);
//            } else if(extras.getString("avatar").toString().contains("barcode")){
//                avatar.setImageResource(R.drawable.barcode);
//            } else if(extras.getString("avatar").toString().contains("cross")){
//                avatar.setImageResource(R.drawable.cross);
//            }
            TextView contactName = findViewById(R.id.contactName);
//            contactName.setText(extras.getString("contactName").toString());
            String id = extras.getString("contactId");
            Log.d("mytag", id);

        }

        ImageButton imgPickBtn = findViewById(R.id.imgPickBtn);
        imgPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 8778);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String Fpath = data.getDataString();
        Log.d("mytag", "Путь до картинки: " + Fpath);
        super.onActivityResult(requestCode, resultCode, data);

    }

}
