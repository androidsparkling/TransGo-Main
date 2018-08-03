package com.ByteCrunchers.TransGo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    Spinner spinner;
    private TextView txtSpeechInput,translated;
    String[] languages=new String[]{"Urdu","French","Arabic","Bulgarian","Dutch","Greek","Indonesian","Italian","Spanish","Chinese","Korean","Latin",
            "German","Persian","Polish","Portuguese","Romanian","Russian","Serbian","Thai","Turkish","Scottish","Japanese"};
    String selectedlanguage, languageCode;
     Context context;
    String copiesText;
    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent)
    {
       // String copiesText= (String) intent.getExtras().get("copied");
    // copiesText=intent.getStringExtra("copied");
      //  txtSpeechInput.setText(copiesText);
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode


        translated=(TextView)mFloatingView.findViewById(R.id.translated);
        txtSpeechInput = (TextView) mFloatingView.findViewById(R.id.txtSpeechInput);

       // Intent intent=Intent.getIntent();
        //String s=getIntent().getExtras().getString("extracted");
        txtSpeechInput.setText(pref.getString("copied", null));

        spinner = (Spinner)mFloatingView.findViewById(R.id.languages);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(FloatingViewService.this,
                android.R.layout.simple_spinner_item,languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);
        //The root element of the expanded view layout
        final View expandedView = mFloatingView.findViewById(R.id.expanded_container);


        //Set the close button
        ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
        closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close the service and remove the from from the window
                stopSelf();
            }
        });

         context=this;
        Button translate = (Button) mFloatingView.findViewById(R.id.button);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // txtSpeechInput.setText(copiesText);
                selectedlanguage= spinner.getSelectedItem().toString();
                selectLanguage(selectedlanguage);


                String textToBeTranslated = txtSpeechInput.getText().toString().replace("\n"," ");
                String languagePair = "en-"+languageCode;

                TranslatorBackgroundTask translatorBackgroundTask= new TranslatorBackgroundTask(context);
                AsyncTask<String, Void, String> translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair); // Returns the translated text as a String

            }
        });





        //Set the close button
        ImageView closeButton = (ImageView) mFloatingView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
            }
        });

        //Open the application on thi button click
        ImageView openButton = (ImageView) mFloatingView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open the application  click.
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //close the service and remove view from the view hierarchy
                stopSelf();
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);
                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
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
