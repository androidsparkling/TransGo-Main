package com.ByteCrunchers.TransGo;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.view.LayoutInflater;

/**
 * Created by Lenovo IP300 on 1/8/2018.
 */

public class SpeechToSpeech extends GActivity {
    private TextView txtSpeechInput,translated;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Spinner spinner,spinner2;
    String[] languages=new String[]{"English","Urdu","French","Arabic","Bulgarian","Dutch","Greek","Indonesian","Italian","Spanish","Chinese","Korean","Latin",
            "German","Persian","Polish","Portuguese","Romanian","Russian","Serbian","Thai","Turkish","Scottish","Japanese"};
    String selectedlanguage, languageCode;
    boolean speech1=false,speech2=false;
    TextToSpeech t1;

    String speechInput="",translatedText="";

    private ListView listView;


    boolean myMessage = true;
    private List<ChatBubble> ChatBubbles;
    private ArrayAdapter<ChatBubble> adapter;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.speech_to_speech);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.speech_to_speech, null, false);
        root.addView(contentView, 0);


        context=this;

        ChatBubbles = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_msg);

        //set ListView adapter first
        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        listView.setAdapter(adapter);

      //  translated=(TextView)findViewById(R.id.translated);
        //txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);



        spinner = (Spinner)findViewById(R.id.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SpeechToSpeech.this,
                android.R.layout.simple_spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner2 = (Spinner)findViewById(R.id.languages2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(SpeechToSpeech.this,
                android.R.layout.simple_spinner_item,languages);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

    }
    public void promptSpeechInput(View v) {
        speech1=true;
        speech2=false;
        myMessage=true;

        Toast.makeText(getApplicationContext(),
                "speak",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        selectedlanguage= spinner.getSelectedItem().toString();



        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectLanguage(selectedlanguage));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void promptSpeechInput2(View v) {
        speech1=false;
        speech2=true;
        myMessage=false;

        Toast.makeText(getApplicationContext(),
                "speak",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        selectedlanguage= spinner2.getSelectedItem().toString();
        //selectLanguage(selectedlanguage);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectLanguage(selectedlanguage));
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                   // txtSpeechInput.setText(result.get(0));
                        speechInput=result.get(0);



                    String sourceCode="";

                    if(speech2)
                    {

                        if (myMessage) {

                            adapter= new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
                            listView.setAdapter(adapter);
                        } if(!myMessage) {
                            adapter= new MessageAdapter(this, R.layout.right_chat_bubble, ChatBubbles);
                            listView.setAdapter(adapter);
                        }
                            //add message to list
                            ChatBubble ChatBubble = new ChatBubble(speechInput, myMessage);
                            ChatBubbles.add(ChatBubble);
                            adapter.notifyDataSetChanged();


                        selectedlanguage= spinner2.getSelectedItem().toString();
                       sourceCode=selectLanguage(selectedlanguage);

                        selectedlanguage= spinner.getSelectedItem().toString();
                        languageCode=selectLanguage(selectedlanguage);

                    }
                    else if(speech1)
                    {

                        if (myMessage) {

                            adapter= new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
                            listView.setAdapter(adapter);
                        } if(!myMessage) {
                            adapter= new MessageAdapter(this, R.layout.right_chat_bubble, ChatBubbles);
                            listView.setAdapter(adapter);
                        }
                        //add message to list
                        ChatBubble ChatBubble = new ChatBubble(speechInput, myMessage);
                        ChatBubbles.add(ChatBubble);
                        adapter.notifyDataSetChanged();


                        selectedlanguage= spinner.getSelectedItem().toString();
                        sourceCode=selectLanguage(selectedlanguage);

                        selectedlanguage= spinner2.getSelectedItem().toString();
                        languageCode=selectLanguage(selectedlanguage);
                    }


                    Context context=this;
                   // String textToBeTranslated = txtSpeechInput.getText().toString().replace("\n"," ");
                    String textToBeTranslated = speechInput.replace("\n"," ");

                    String languagePair = sourceCode+"-"+languageCode;

                    TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
                    AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair); // Returns the translated text as a String


                }
                break;
            }

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

            if (myMessage) {

                adapter= new MessageAdapter(SpeechToSpeech.this, R.layout.left_chat_bubble_translated, ChatBubbles);
                listView.setAdapter(adapter);
            }
            if(!myMessage){
                adapter= new MessageAdapter(SpeechToSpeech.this, R.layout.right_chat_bubble_translated, ChatBubbles);
                listView.setAdapter(adapter);
            }
            //add message to list
            ChatBubble ChatBubble = new ChatBubble(resultString, myMessage);
            ChatBubbles.add(ChatBubble);
            adapter.notifyDataSetChanged();

            listView.setSelection(adapter.getCount() - 1);

            t1=new TextToSpeech(SpeechToSpeech.this, new TextToSpeech.OnInitListener() {


                @Override
                public void onInit(int status) {

                    if(status == TextToSpeech.SUCCESS){
                        t1.setLanguage(new Locale(selectLanguage(resultString)));
                        //t1.setLanguage(result);

                       // t1.speak(translated.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

                        t1.speak(resultString, TextToSpeech.QUEUE_FLUSH, null);

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

}
