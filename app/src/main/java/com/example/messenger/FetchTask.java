package com.example.messenger;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

public class FetchTask<ResponseType> extends AsyncTask<String, Integer, ResponseType> {
    @Override
    protected ResponseType doInBackground(String... url) {
        String responseJson = "";
        ResponseType transferJson = null;
        if(url[0].contains("/contacts/list")){
            try {
                responseJson = UrlFetcher.getText(url[0]);
            } catch(Exception e) {

            }
            try {
                JSONObject obj = new JSONObject(responseJson);
                transferJson = (ResponseType) obj.getJSONArray("contacts");
            } catch(JSONException e) {
                Log.d("mytag", "ошибка парсинга json");
            }
        } else if(url[0].contains("/contacts/get")){
            try {
                responseJson = UrlFetcher.getText(url[0]);
                Log.d("mytag", "ответ запроса: " + responseJson);
            } catch(Exception e) {
                Log.d("mytag", "ошибка запроса: " + url + " " + e);
            }
            JSONArray responseContacts = null;
            try {
                JSONObject obj = new JSONObject(responseJson);
                responseContacts = obj.getJSONArray("contacts");
            } catch(JSONException e) {
                Log.d("mytag", "ошибка парсинга json");
            }
        }


        return transferJson;
    }
}
