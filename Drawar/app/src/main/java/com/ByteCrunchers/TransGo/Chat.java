package com.ByteCrunchers.TransGo;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;



    Spinner spinner,spinner2;
    String[] languages=new String[]{"English","Urdu","French","Arabic","Bulgarian","Dutch","Greek","Indonesian","Italian","Spanish","Chinese","Korean","Latin",
            "German","Persian","Polish","Portuguese","Romanian","Russian","Serbian","Thai","Turkish","Scottish","Japanese"};
    String selectedlanguage, languageCode;
    boolean speech1=false,speech2=false;
    TextToSpeech t1;

    String speechInput="",translatedText="";
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        /*LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_chat, null, false);
        root.addView(contentView, 0);*/


        spinner = (Spinner)findViewById(R.id.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Chat.this,
                android.R.layout.simple_spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);
        sendButton = (ImageView)findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://my-fyp-890f3.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://my-fyp-890f3.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                 message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You:-\n" + message, 1);
                }
                else{
                    selectedlanguage= spinner.getSelectedItem().toString();
                    languageCode=selectLanguage(selectedlanguage);

                    TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(getApplicationContext());
                    AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(message,languageCode); // Returns the translated text as a String


                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }



    public class TranslatorBackgroundTask extends AsyncTask<String, Void, String> {
        //Declare Context
        Context ctx;
        String resultString;
        //Set Context
        TranslatorBackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            //String variables
            String textToBeTranslated = params[0];
            String languagePair = params[1];


            String jsonString;

            try {
                //Set up the translation call URL
                String yandexKey = "trnsl.1.1.20171226T121459Z.08a8183263f03572.8c5cff0151c8ebcd9370fe18f8fd51b474f9b3bc";
                String yandexUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + yandexKey
                        + "&text=" + textToBeTranslated + "&lang=" + languagePair;
                URL yandexTranslateURL = new URL(yandexUrl);

                //Set Http Conncection, Input Stream, and Buffered Reader
                HttpURLConnection httpJsonConnection = (HttpURLConnection) yandexTranslateURL.openConnection();
                InputStream inputStream = httpJsonConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                //Set string builder and insert retrieved JSON result into it
                StringBuilder jsonStringBuilder = new StringBuilder();
                while ((jsonString = bufferedReader.readLine()) != null) {
                    jsonStringBuilder.append(jsonString + "\n");
                }

                //Close and disconnect
                bufferedReader.close();
                inputStream.close();
                httpJsonConnection.disconnect();

                //Making result human readable
                resultString = jsonStringBuilder.toString().trim();
                //Getting the characters between [ and ]
                resultString = resultString.substring(resultString.indexOf('[')+1);
                resultString = resultString.substring(0,resultString.indexOf("]"));
                //Getting the characters between " and "
                resultString = resultString.substring(resultString.indexOf("\"")+1);
                resultString = resultString.substring(0,resultString.indexOf("\""));

                // TranslationCls translationCls=new TranslationCls();
                //translationCls.result=resultString;


                Log.d("Translation Result:", resultString);
                return jsonStringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {



//            translated.setText(resultString);
            translatedText=resultString;
            message=resultString;
            addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
            t1=new TextToSpeech(Chat.this, new TextToSpeech.OnInitListener() {


                @Override
                public void onInit(int status) {

                    if(status == TextToSpeech.SUCCESS){
                        t1.setLanguage(new Locale(selectLanguage(resultString)));
                        //t1.setLanguage(result);

                        // t1.speak(translated.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                     //   t1.speak(resultString, TextToSpeech.QUEUE_FLUSH, null);

                    }

                }
            });
            //myMessage=false;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public String selectLanguage(String language)
    {
        if(language.equals("Urdu"))
        {
            languageCode="ur";
        }
        else if(language.equals("English"))
        {
            languageCode="en";
        }
        else if(language.equals("French"))
        {
            languageCode="fr";
        }
        else if(language.equals("Arabic"))
        {
            languageCode="ar";
        }
        else if(language.equals("Bulgarian"))
        {
            languageCode="bg";
        }
        else if(language.equals("Dutch"))
        {
            languageCode="nl";
        }
        else if(language.equals("Greek"))
        {
            languageCode="el";
        }
        else if(language.equals("Indonesian"))
        {
            languageCode="id";
        }
        else if(language.equals("Italian"))
        {
            languageCode="it";
        }
        else if(language.equals("Spanish"))
        {
            languageCode="es";
        }
        else if(language.equals("Chinese"))
        {
            languageCode="zh";
        }
        else if(language.equals("Korean"))
        {
            languageCode="ko";
        }
        else if(language.equals("Latin"))
        {
            languageCode="la";
        }
        else if(language.equals("German"))
        {
            languageCode="de";
        }
        else if(language.equals("Persian"))
        {
            languageCode="fa";
        }
        else if(language.equals("Polish"))
        {
            languageCode="pl";
        }
        else if(language.equals("Portuguese"))
        {
            languageCode="pt";
        }
        else if(language.equals("Romanian"))
        {
            languageCode="ro";
        }
        else if(language.equals("Russian"))
        {
            languageCode="ru";
        }
        else if(language.equals("Serbian"))
        {
            languageCode="sr";
        }
        else if(language.equals("Thai"))
        {
            languageCode="th";
        }
        else if(language.equals("Turkish"))
        {
            languageCode="tr";
        }

        else if(language.equals("Scottish"))
        {
            languageCode="gd";
        }
        else if(language.equals("Japanese"))
        {
            languageCode="ja";
        }
        return languageCode;
    }

}