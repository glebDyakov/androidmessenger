package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.core.view.MarginLayoutParamsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public ArrayList<HashMap<String, Object>> contacts = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashMap<String, Object> contactOne = new HashMap<String, Object>();
        contactOne.put("id", "1");
        contactOne.put("name", "Роман Сакутин");
        contactOne.put("lastMessage", "Хэй, мен...");
        contactOne.put("avatar", "five");
        contacts.add(contactOne);
        HashMap<String, Object> contactTwo = new HashMap<String, Object>();
        contactTwo.put("id", "2");
        contactTwo.put("name", "Арт Аласки");
        contactTwo.put("lastMessage", "Доброго времени уток");
        contactTwo.put("avatar", "cross");
        contacts.add(contactTwo);
        HashMap<String, Object> contactThree = new HashMap<String, Object>();
        contactThree.put("id", "3");
        contactThree.put("name", "Флатинго");
        contactThree.put("lastMessage", "Привет гей девелоперы");
        contactThree.put("avatar", "camera");
        contacts.add(contactThree);

        for(HashMap<String, Object> contact : contacts){

            ImageButton currentContactAvatar = new ImageButton(MainActivity.this);
//            currentContactAvatar.setMaxWidth(35);
//            currentContactAvatar.setMaxHeight(35);
            currentContactAvatar.setLayoutParams(new ConstraintLayout.LayoutParams(115, 115));
            if(contact.get("avatar").toString().contains("five")){
                currentContactAvatar.setImageResource(R.drawable.five);
            } else if(contact.get("avatar").toString().contains("barcode")){
                currentContactAvatar.setImageResource(R.drawable.barcode);
            } else if(contact.get("avatar").toString().contains("magnet")){
                currentContactAvatar.setImageResource(R.drawable.magnet);
            } else if(contact.get("avatar").toString().contains("camera")){
                currentContactAvatar.setImageResource(R.drawable.camera);
            } else if(contact.get("avatar").toString().contains("cross")){
                currentContactAvatar.setImageResource(R.drawable.cross);
            }

            TextView currentContactName = new TextView(MainActivity.this);
            currentContactName.setText(contact.get("name").toString());
            TextView currentContactLastMessage = new TextView(MainActivity.this);
            currentContactLastMessage.setText(contact.get("lastMessage").toString());

            LinearLayout layoutOfContacts = findViewById(R.id.layoutOfContacts);
            LinearLayout contactLayout = new LinearLayout(MainActivity.this);

            ConstraintLayout.LayoutParams contactLayoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            contactLayoutParams.setMargins(5,75,5, 75);
            contactLayout.setLayoutParams(contactLayoutParams);

            layoutOfContacts.addView(contactLayout);
            contactLayout.setOrientation(LinearLayout.HORIZONTAL);
            contactLayout.addView(currentContactAvatar);
            contactLayout.addView(currentContactName);
            contactLayout.addView(currentContactLastMessage);

            contactLayout.setContentDescription(contact.get("id").toString());
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