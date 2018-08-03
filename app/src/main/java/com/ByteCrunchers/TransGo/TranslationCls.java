package com.ByteCrunchers.TransGo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telecom.GatewayInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Lenovo IP300 on 1/6/2018.
 */

public class TranslationCls extends GActivity {
    String extracted=null;
    EditText t;
    TextView translated;
    String result=null;
    Spinner spinner;
    String[] languages=new String[]{"Urdu","French","Arabic","Bulgarian","Dutch","Greek","Indonesian","Italian","Spanish","Chinese","Korean","Latin",
    "German","Persian","Polish","Portuguese","Romanian","Russian","Serbian","Thai","Turkish","Scottish","Japanese"};
    String selectedlanguage, languageCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.translation);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.translation, null, false);
        root.addView(contentView, 0);

        extracted=getIntent().getExtras().getString("extracted");


        t=(EditText) findViewById(R.id.extr);
        t.setText(extracted);
        translated=(TextView)findViewById(R.id.translated);

        spinner = (Spinner)findViewById(R.id.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TranslationCls.this,
                android.R.layout.simple_spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
      //  spinner.setOnItemSelectedListener(this);

    }

    public void selectLanguage(String language)
    {
        if(language.equals("Urdu"))
        {
            languageCode="ur";
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
    }
    public void translate(View v) {

    //new SaveTheFeed().execute();
    selectedlanguage= spinner.getSelectedItem().toString();
    selectLanguage(selectedlanguage);

    Context context=this;
    String textToBeTranslated = t.getText().toString().replace("\n"," ");
    String languagePair = "en-"+languageCode;

    TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
    AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair); // Returns the translated text as a String



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
            translated.setText(resultString);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }




}
