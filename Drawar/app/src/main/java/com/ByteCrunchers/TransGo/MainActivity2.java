package com.ByteCrunchers.TransGo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.mindorks.paracamera.Camera;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MainActivity2 extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TessBaseAPI mTess;
    String datapath = "";
    private static int RESULT_LOAD_IMG = 1;
    Bitmap selectedImage;
    ImageView imageView;
    TextView OCRTextView;
    String OCRresult;
    Camera camera;
    int t=0,a=0;
    LinearLayout cam,gallery;
    FloatingActionButton fab;
    boolean fabExpanded =false;

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, ClipboardMonitorService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }

        cam=(LinearLayout)findViewById(R.id.layoutCamera);
        gallery=(LinearLayout)findViewById(R.id.layoutGallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fabu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
                if (fabExpanded == true){
                    closeSubMenusFab();
                } else {
                    openSubMenusFab();
                }
            }
        });
        closeSubMenusFab();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("me_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        imageView=(ImageView)findViewById(R.id.imageVie);




    }

    private void closeSubMenusFab(){
        cam.setVisibility(View.INVISIBLE);
        gallery.setVisibility(View.INVISIBLE);

        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = false;
    }


    private void openSubMenusFab(){
       cam.setVisibility(View.VISIBLE);
        gallery.setVisibility(View.VISIBLE);

        fab.setImageResource(R.drawable.ic_close_black_24dp);
        fabExpanded = true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.speech) {
            Intent i=new Intent(MainActivity2.this,SpeechToText.class);
            startActivity(i);

        }
        else if (id == R.id.speechtospeech) {
            Intent i=new Intent(MainActivity2.this,SpeechToSpeech.class);
            startActivity(i);

        }
        else if (id == R.id.sms) {

            Intent i=new Intent(MainActivity2.this,Login.class);

            startActivity(i);

        }

        else if (id == R.id.Real_Time) {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("edu.sfsu.cs.orange.ocr");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }

        }
        else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void processImage(View view){


        new CameraExtractOp().execute();



    }

  /*  public void translate()
    {
        String text = "";
String ocrdTextView;

        if (OCRresult!="")
        {

            ocrdTextView=OCRresult;

            text = ocrdTextView;
String selectedLanguage=


            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("https://translate.yandex.net/api/v1.5/tr/translate?key=trnsl.1.1.20180102T204517Z.04fa0d17c710294a.a073b91196a242b91ee168e0bf70a0b472aec91a;lang=" +
                        "ur" + "&text=" + text + "&format=plain");

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                jObj = new JSONObject(json);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }




    }
*/
    public JSONObject getJSONFromUrl(String text) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://translate.yandex.net/api/v1.5/tr/translate?key=trnsl.1.1.20180102T204517Z.04fa0d17c710294a.a073b91196a242b91ee168e0bf70a0b472aec91a;lang=" +
                    "ur" + "&text=" + text + "&format=plain");

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }


    public void refresh()
    {
        cam.setVisibility(View.INVISIBLE);
        gallery.setVisibility(View.INVISIBLE);

        fab.setImageResource(R.drawable.ic_add_black_24dp);
        fabExpanded = false;
    }
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);



        if(reqCode == Camera.REQUEST_TAKE_PHOTO && t==1){
            Bitmap bitmap = camera.getCameraBitmap();
            t=0;
            if(bitmap != null) {
                imageView.setImageBitmap(bitmap);
                selectedImage=bitmap;
            }else{
                Toast.makeText(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT).show();
            }
        }

        if(a==1 && resultCode==RESULT_OK) {
            a=0;
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
                // image = BitmapFactory.decodeResource(getResources(), selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity2.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }

        if (reqCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(reqCode, resultCode, data);
        }
    }
    public void openCamera(View v)
    {

        try {
            t=1;
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openGallery(View v)
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        a=1;
    }
    private void checkFile(File dir) {

    }

    private void copyFiles() {
        try {
            String filepath = datapath + "/tessdata/eng.traineddata";
            //String filepath = datapath + "/tessdata/urd.traineddata";
            AssetManager assetManager = getAssets();

            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            //InputStream instream = assetManager.open("tessdata/urd.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }


            outstream.flush();
            outstream.close();
            instream.close();

            File file = new File(filepath);
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class CameraExtractOp extends AsyncTask<Void, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity2.this, "Extraction Strated", Toast.LENGTH_SHORT).show();
            //recognizedText = "Unable to Extract";

             progressDialog = new ProgressDialog(MainActivity2.this);
            progressDialog.setMessage("Extracting Text..."); // Setting Message
            progressDialog.setTitle("OCR"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            new Thread(new Runnable() {
                public void run() {


                }
            }).start();
        }

        @Override
        protected String doInBackground(Void... voids) {

            String language = "eng";
            //String language = "urd";
            datapath = getFilesDir()+ "/tesseract/";
            mTess = new TessBaseAPI();

           // checkFile(new File(datapath + "tessdata/"));
            File dir=new File(datapath + "tessdata/");
            if (!dir.exists()&& dir.mkdirs()){
               // copyFiles();
            }
            if(dir.exists()) {
                String datafilepath = datapath+ "/tessdata/eng.traineddata";
                //String datafilepath = datapath+ "/tessdata/urd.traineddata";
                File datafile = new File(datafilepath);

                if (!datafile.exists()) {
                    copyFiles();
                }
            }


            mTess.init(datapath, language);



                    try {
                        OCRresult = null;
                        mTess.setImage(selectedImage);
                        OCRresult = mTess.getUTF8Text();
                        //OCRTextView = (TextView) findViewById(R.id.OCRTextView);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }





            return OCRresult;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progressDialog.dismiss();
           /* JSONObject json =getJSONFromUrl(OCRresult);
            JSONArray user = null;
            try {
                user = json.getJSONArray("text");

            JSONObject c = user.getJSONObject(0);

            // Storing  JSON item in a Variable
            String id = c.getString("text");
                OCRTextView.setText(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
       //

            Intent finalIntent = new Intent(MainActivity2.this, TranslationCls.class);
            finalIntent.putExtra("extracted", OCRresult);
            startActivity(finalIntent);
           // OCRTextView.setText(OCRresult);
           // refresh();

        }

    }

    private void initializeView() {


    }


}
