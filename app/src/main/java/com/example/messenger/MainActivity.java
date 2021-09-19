package com.example.messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintLayoutStates;
import androidx.core.view.MarginLayoutParamsCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String address;
    private boolean isTimerStarted;
    private boolean isAnswered;
    private String name;
    private SocketServer mServer;
    private SocketClient mTcpClient;

    public SQLiteDatabase db;
    public ArrayList<HashMap<String, Object>> contacts = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServer = new SocketServer(new SocketServer.OnMessageReceived() {
            @Override public void messageReceived(String message, Contact from) {
                parseMessage(message, from);
            }

            @Override public void updatePlayerList(ArrayList connectedUsers) {
                updatePlayer(connectedUsers);
            }
        });
        mServer.start();
        if (mTcpClient == null || !mTcpClient.isConnected() || !mTcpClient.isRunning()) {
            connectToServer();
        }

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

//                intent.putExtra("contactId", currentSocket.getString(3));
                intent.putExtra("contactId", currentSocket.getString(4));

                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void updatePlayer(final ArrayList connectedContacts) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
//                users.setText("");
            }
        });

        if (connectedContacts.size() != 0) {
            if (connectedContacts.size() == 1) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
//                        users.setText(connectedContacts.get(0).getContact().getUsername());
                    }
                });
            } else {
                final StringBuilder builder = new StringBuilder();
//                for (ContactManager contact : connectedContacts) {
//                    builder.append(contact.getContact().getContactname());
//                    builder.append(contact.getContact().getContactname());
//                    builder.append("\n");
//                }

                runOnUiThread(new Runnable() {
                    @Override public void run() {
//                        users.setText(builder.toString());
                    }
                });
            }
        }
    }

    private void parseMessage(final String message, final Contact from) {
        runOnUiThread(new Runnable() {
            @Override public void run() {
                // проверяем, отвечает ли сейчас кто-либо
                if (!TextUtils.isEmpty(name) && isAnswered) {
                    if (!name.equals(from.getContactname())) {
                        new Thread(new Runnable() {
                            @Override public void run() {
                                mServer.sendMessageTo(from.getContactID(), "Constants.ANSWERED");
                            }
                        }).start();
                    }

                    return;
                }

                // проверяем, запущен ли таймер
                if (!isTimerStarted) {
//                    msg.setText("Constants.FALSE_START" + " - " + from.getContactname());
                    new Thread(new Runnable() {
                        @Override public void run() {
                            mServer.sendMessageTo(from.getContactID(), "Constants.FALSE_START");
                        }
                    }).start();

                    return;
                }

                // останавливаем таймер
                isTimerStarted = false;
//                countDownTimer.cancel();
//                msg.setText(message);
//                ibTimer.setEnabled(false);

                // смотрим, какая команда отвечает, и зажигаем соответствующую кнопку
                if (from != null && !isAnswered) {
//                    switch (from.getContactname()) {
//                        case "Команда 1": {
//                            btn_team1.setBackgroundResource(R.drawable.button_pressed);
//                            break;
//                        }
//                        case "Команда 2": {
//                            btn_team2.setBackgroundResource(R.drawable.button_pressed);
//                            break;
//                        }
//                        case "Команда 3": {
//                            btn_team3.setBackgroundResource(R.drawable.button_pressed);
//                            break;
//                        }
//                        case "Команда 4": {
//                            btn_team4.setBackgroundResource(R.drawable.button_pressed);
//                            break;
//                        }
//                    }

                    name = from.getContactname();
                    isAnswered = true; // устанавливаем флаг, сообщающий о том, что в данный момент дается ответ
                    new Thread(new Runnable() {
                        @Override public void run() {
                            mServer.sendMessageTo(from.getContactID(), "Constants.DONE");
                        }
                    }).start();
                }
            }
        });
    }

    private void connectToServer() {
        if (name != null) {
            new Thread(new Runnable() {
                @Override public void run() {
                    mTcpClient = new SocketClient(address, new SocketClient.OnMessageReceived() {
                        @Override public void onConnected() {
                            runOnUiThread(new Runnable() {
                                @Override public void run() {
                                    sendPing(); // начинаем посылать пинги
                                }
                            });
                        }

                        @Override
                        public void messageReceived(final String message) {
                            switch (message) {
                                case "Constants.ANSWERED": {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
//                                            buttonAnswer.setBackgroundResource(R.drawable.button_normal);
                                        }
                                    });
                                    break;
                                }
                                case "Constants.BEEP": {
                                    playBeep();
                                    break;
                                }
                                case "Constants.RESET": {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
//                                            response.setText("");
//                                            buttonAnswer.setBackgroundResource(R.drawable.button_normal);
                                        }
                                    });
                                    break;
                                }
                                case "Constants.FALSE_START": {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
//                                            response.setText("Constants.FALSE_START");
//                                            buttonAnswer.setBackgroundResource(R.drawable.button_normal);
                                        }
                                    });
                                    break;
                                }
                                default: {
                                    runOnUiThread(new Runnable() {
                                        @Override public void run() {
//                                            response.setText(message);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    });
                    mTcpClient.run(name);
                }
            }).start();
        }
    }

    private void sendPing() {
        Runnable runnable = new Runnable() {
            @Override public void run() {
                if (mTcpClient != null) {
                    if (!mTcpClient.isRunning()) {
                        connectToServer();
                    } else {
                        mTcpClient.sendMessage("Constants.PING");
                    }
                }
//                handler.postDelayed(this, 2000);
            }
        };
//        handler.post(runnable);
    }

    private void showPlayerDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Выберите свою команду");
        b.setCancelable(false);
        String[] types = { "Команда 1", "Команда 2", "Команда 3", "Команда 4" };
        b.setItems(types, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                switch (which) {
//                    case 0:
//                        name = "Команда 1";
////                        tv_info.setText(tv_info.getText() + name);
//                        break;
//                    case 1:
//                        name = "Команда 2";
//                        tv_info.setText(tv_info.getText() + name);
//                        break;
//                    case 2:
//                        name = "Команда 3";
//                        tv_info.setText(tv_info.getText() + name);
//                        break;
//                    case 3:
//                        name = "Команда 4";
//                        tv_info.setText(tv_info.getText() + name);
//                        break;
//                }
                showTeamDialog(); // запускаем диалог с вводом IP-адреса сервера
            }
        });

        b.show();
    }

    private void showTeamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = getLayoutInflater().inflate(R.layout.dialog_client, null);
//        builder.setView(view);
//        final EditText et_address = view.findViewById(R.id.addressEditText);
        builder.setCancelable(false);
        builder.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog, int which) {
//                address = et_address.getText().toString();
//                tv_info.setText(tv_info.getText() + " | " + address);
                connectToServer(); // запускаем подключение к серверу
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void playBeep() {
        try {
            MediaPlayer m = new MediaPlayer();

            if (m.isPlaying()) {
                m.stop();
                m.release();
                m = new MediaPlayer();
            }

            AssetFileDescriptor descriptor = getAssets().openFd("signal.mp3");
            m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), 10000);
            descriptor.close();

            m.prepare();
            m.setVolume(1f, 1f);
            m.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override protected void onPause() {
        if (mTcpClient != null) {
            new Thread(new Runnable() {
                @Override public void run() {
                    mTcpClient.stopClient();
                    mTcpClient = null;
                }
            }).start();
        }
        super.onPause();
    }

    private void sendAnswer() {
        final String message = "Отвечает команда: " + name;
        if (mTcpClient != null && mTcpClient.isConnected()) {
            new Thread(new Runnable() {
                @Override public void run() {
                    mTcpClient.sendMessage(message);
                }
            }).start();
//            buttonAnswer.setBackgroundResource(R.drawable.button_pressed);
        }
    }

}