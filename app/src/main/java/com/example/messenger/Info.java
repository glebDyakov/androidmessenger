package com.example.messenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Info  extends AppCompatActivity {

    public SQLiteDatabase db;
    public String otherContactId;
    public String contactId;

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

            contactId = extras.getString("contactId");
            otherContactId = extras.getString("otherContactId");

            Log.d("mytag", "otherContactId: " + otherContactId + " , cotactId: " + contactId);

            @SuppressLint("WrongConstant") SQLiteDatabase db = openOrCreateDatabase("contactio.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
//            Cursor selectedContact = db.rawQuery("Select * from sockets where _id = \"1\"", null);
//            selectedContact.moveToFirst();
//            String imgSrc = selectedContact.getString(3);
//            Log.d("mytag", imgSrc);
            ImageView avatar = findViewById(R.id.avatar);
            try {
                String url = "https://messengerserv.herokuapp.com/contacts/get/?id=" + otherContactId;
                JSONObject responseJson = new FetchTask<JSONObject>().execute(url).get();
                TextView contactName = findViewById(R.id.contactName);
                contactName.setText(responseJson.getString("name"));

                Bitmap uploadedImg = new FetchTask<Bitmap>(avatar).execute("https://opalescent-soapy-baseball.glitch.me/contacts/getavatar/?contactid=1&path=abc").get();
                avatar.setImageBitmap(uploadedImg);

            } catch (Exception e){

            }

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

//            TextView contactName = findViewById(R.id.contactName);

//            contactName.setText(extras.getString("contactName").toString());

//            String id = extras.getString("contactId");
//            Log.d("mytag", id);

            ImageButton imgPickBtn = findViewById(R.id.imgPickBtn);
            if(!(otherContactId.contains(contactId))) {
                imgPickBtn.setVisibility(View.INVISIBLE);
            } else {
                imgPickBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
//                        intent.setType("file/*");
//                        startActivityForResult(intent, 8778);
//                        startActivityForResult(intent, TFRequestCodes.GALLERY);

                        db.close();

                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


                    }
                });
            }

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String Fpath = data.getDataString();
        Log.d("mytag", "Путь до картинки: " + Fpath + ", requestCode: " + requestCode + ", resultCode: " + resultCode);

        Path pathToAvatar = Paths.get(Fpath);

//        File newAvatar = new File(Fpath);
//        Path pathToAvatar = newAvatar.toPath();

        byte[] byteData = new byte[0];
        try {

//            FileUtils
            byteData = Files.readAllBytes(pathToAvatar);
//            byteData = Files.readAllBytes(Fpath);

//            ByteArrayOutputStream ous = null;
//            InputStream ios = null;
//            byte[] buffer = new byte[4096];
//            ous = new ByteArrayOutputStream();
//            ios = new FileInputStream(newAvatar);
//            int read = 0;
//            while ((read = ios.read(buffer)) != -1) {
//                ous.write(buffer, 0, read);
//            }

            JSONObject responseJson = null;
            String url = "https://opalescent-soapy-baseball.glitch.me/contacts/upload";
//            try {
//                responseJson = new FetchTask<JSONObject>(byteData, contactId).execute(url).get();
//                Log.d("mytag", "status при отправке картинки: " + responseJson.getString("status"));
//            } catch (Exception e){
//                Log.d("mytag", "Ошибка парсинга json при отправке картинки");
//            }
        } catch (IOException e) {
            Log.d("mytag", "Ошибка чтения байтов при отправке картинки");
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

}
