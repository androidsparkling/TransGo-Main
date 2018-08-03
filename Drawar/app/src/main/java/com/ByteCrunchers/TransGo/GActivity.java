package com.ByteCrunchers.TransGo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yalantis.guillotine.animation.GuillotineAnimation;
//import com.yalantis.guillotine.sample.R;


import butterknife.ButterKnife;
import butterknife.BindView;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;


public class GActivity extends AppCompatActivity {
    private static final long RIPPLE_DURATION = 250;


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
   protected FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;
    GuillotineAnimation.GuillotineBuilder builder;
    TextView textView;

    TextView sttTextView, stsTextView, chatTextView, ocrTextView, aboutTextView,settingsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        ButterKnife.bind(this);

        //startService(new Intent(this, ClipboardMonitorService.class));
        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //textView = (TextView) findViewById(R.id.profileID);
        //textView.setText("asdf");
        //textView.setOnTouchListener(new CustomTouchListener());
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        //textView = (TextView)guillotineMenu.findViewById(R.id.profileID);
        sttTextView = (TextView)guillotineMenu.findViewById(R.id.sttTextView);
        stsTextView = (TextView)guillotineMenu.findViewById(R.id.stsTextView);
        chatTextView = (TextView)guillotineMenu.findViewById(R.id.chatTextView);
        ocrTextView = (TextView)guillotineMenu.findViewById(R.id.ocrTextView);
        aboutTextView= (TextView)guillotineMenu.findViewById(R.id.aboutTextView);
        settingsTextView= (TextView)guillotineMenu.findViewById(R.id.settingsTextView);

        sttTextView.setOnTouchListener(new CustomTouchListener());
        stsTextView.setOnTouchListener(new CustomTouchListener());
        chatTextView.setOnTouchListener(new CustomTouchListener());
        ocrTextView.setOnTouchListener(new CustomTouchListener());
        aboutTextView.setOnTouchListener(new CustomTouchListener());
        settingsTextView.setOnTouchListener(new CustomTouchListener());

        builder=new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger);
        builder.setStartDelay(RIPPLE_DURATION);
        builder.setActionBarViewForAnimation(toolbar);
        builder.setClosedOnStart(true);
        builder.build();
//        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
//                .setStartDelay(RIPPLE_DURATION)
//                .setActionBarViewForAnimation(toolbar)
//                .setClosedOnStart(true)
//                .build();
    }
    public class CustomTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    // Action you you want on finger down.
                    //Toast.makeText(this,"onTouch",Toast.LENGTH_LONG).show();
                {

                    final int id=view.getId();

                    builder.build().close();
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                //Intent i=new Intent(GActivity.this,SpeechToSpeech.class);
                                if (id == R.id.sttTextView) {
                                    Intent i=new Intent(GActivity.this,SpeechToText.class);
                                    //sttTextView.setTextColor(Color.parseColor("#30d1d5"));
                                    startActivity(i);

                                }
                                else if (id == R.id.stsTextView) {
                                    Intent i2=new Intent(GActivity.this,SpeechToSpeech.class);
                                    //stsTextView.setTextColor(Color.parseColor("#30d1d5"));
                                    startActivity(i2);
                                    //Toast.makeText(GActivity.this, "Action clicked", Toast.LENGTH_LONG).show();

                                }
                                else if (id == R.id.chatTextView) {

                                    Intent i3=new Intent(GActivity.this,Login.class);

                                    startActivity(i3);

                                }
                                else if (id == R.id.ocrTextView) {

                                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ByteCrunchers.TransGo.RealTimeOCR");
                                    if (launchIntent != null) {
                                        startActivity(launchIntent);//null pointer check in case package name was not found
                                    }

                                }
                                else if (id == R.id.aboutTextView) {

                                    Intent intent;
                                    intent = new Intent(GActivity.this, AboutActivity.class);
                                    intent.putExtra(AboutActivity.REQUESTED_PAGE_KEY, AboutActivity.ABOUT_PAGE);
                                    startActivity(intent);

                                }
                                else if (id == R.id.settingsTextView) {

                                    Intent i4=new Intent(GActivity.this,SettingsActivity.class);

                                    startActivity(i4);

                                }
                            }
                        }, 400);
                    }
                    catch (Exception e)
                    {

                    }

                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    // Action you you want on finger up
                    break;
            }
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.speech) {
            Intent i=new Intent(GActivity.this,SpeechToText.class);
            sttTextView.setTextColor(Color.parseColor("#30d1d5"));
            startActivity(i);
            return true;
        }
        else if (id == R.id.speechtospeech) {
            Intent i=new Intent(GActivity.this,SpeechToSpeech.class);
            stsTextView.setTextColor(Color.parseColor("#30d1d5"));
            startActivity(i);
            //Toast.makeText(GActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.sms) {

            Intent i=new Intent(GActivity.this,Login.class);

            startActivity(i);
            return true;
        }
        else if (id == R.id.Real_Time) {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.ByteCrunchers.TransGo.RealTimeOCR");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
