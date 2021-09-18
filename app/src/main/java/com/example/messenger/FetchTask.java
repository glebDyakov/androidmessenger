package com.example.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class FetchTask<ResponseType> extends AsyncTask<String, Integer, ResponseType> {

    public ImageView bmImage;

    public FetchTask() {

    }

    public FetchTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

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
        } else if(url[0].contains("/contacts/getavatar")){
            try {

//                transferJson = (ResponseType) Uri.parse("https://opalescent-soapy-baseball.glitch.me/contacts/getavatar/?contactid=1&path=abc");

                InputStream in = new java.net.URL(url[0]).openStream();
                transferJson = (ResponseType) BitmapFactory.decodeStream(in);

            } catch(Exception e) {
                Log.d("mytag", "ошибка запроса: " + e);
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

//                JSONObject obj = new JSONObject(responseJson);
//                responseContacts = obj.getJSONArray("contacts");

                JSONObject obj = new JSONObject(responseJson);
                transferJson = (ResponseType) obj.getJSONObject("contact");

            } catch(JSONException e) {
                Log.d("mytag", "ошибка парсинга json");
            }
        } else if(url[0].contains("/contacts/create")){
            try {
                responseJson = UrlFetcher.getText(url[0]);
            } catch(Exception e) {
                Log.d("mytag", "ошибка запроса: " + url + " " + e);
            }
            JSONArray responseContacts = null;
            try {
//                JSONObject obj = new JSONObject(responseJson);
//                transferJson = (ResponseType) obj.getString("status");
                transferJson = (ResponseType) new JSONObject(responseJson);
            } catch(JSONException e) {
                Log.d("mytag", "ошибка парсинга json");
            }

        } else if(url[0].contains("/contacts/messages/add")) {
            try {
                responseJson = UrlFetcher.getText(url[0]);
                transferJson = (ResponseType) new JSONObject(responseJson);
            } catch (Exception e) {
                Log.d("mytag", "ошибка запроса: " + url + " " + e);
            }
            try {
                transferJson = (ResponseType) new JSONObject(responseJson);
            } catch (JSONException e) {
                Log.d("mytag", "ошибка парсинга json, url: " + url[0]);
            }
        } else if(url[0].contains("/contacts/get")) {
            try {
                responseJson = UrlFetcher.getText(url[0]);
            } catch (Exception e) {
                Log.d("mytag", "ошибка запроса: " + url + " " + e);
            }
            try {
                JSONObject currentContact = new JSONObject(responseJson).getJSONObject("contact");
                transferJson = (ResponseType) currentContact.getJSONArray("messages");
            } catch (JSONException e) {
                Log.d("mytag", "ошибка парсинга json, url: " + url[0]);
            }
        }

        return transferJson;
    }

    protected void onPostExecute(ResponseType result) {
        if(result instanceof Bitmap){
            bmImage.setImageBitmap((Bitmap) result);
        }
    }

}
