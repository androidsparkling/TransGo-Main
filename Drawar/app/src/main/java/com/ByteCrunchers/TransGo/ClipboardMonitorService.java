package com.ByteCrunchers.TransGo;

/**
 * Created by Lenovo IP300 on 1/23/2018.
 */

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;

/**
 * Monitors the {@link ClipboardManager} for changes and logs the text to a file.
 */
public class ClipboardMonitorService extends Service {
    private static final String TAG = "ClipboardManager";
    private static final String FILENAME = "clipboard-history.txt";

    private File mHistoryFile;
    private ExecutorService mThreadPool = Executors.newSingleThreadExecutor();
    private ClipboardManager mClipboardManager;
    public String copiedtext;
    @Override
    public void onCreate() {
        super.onCreate();

        ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipBoard.addPrimaryClipChangedListener(new ClipboardListener());
        // TODO: Show an ongoing notification when this service is running.

        mClipboardManager =
                (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(
                mOnPrimaryClipChangedListener);

       // Toast.makeText(getApplicationContext(), mClipboardManager.toString(),  Toast.LENGTH_LONG).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
         super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private ClipboardManager.OnPrimaryClipChangedListener mOnPrimaryClipChangedListener =
            new ClipboardManager.OnPrimaryClipChangedListener() {
                @Override
                public void onPrimaryClipChanged() {
                    Log.d(TAG, "onPrimaryClipChanged");
                    ClipData clip = mClipboardManager.getPrimaryClip();
                    mThreadPool.execute(new WriteHistoryRunnable(
                            clip.getItemAt(0).getText()));
                    Toast.makeText(getApplicationContext(), clip.toString(),
                            Toast.LENGTH_SHORT).show();
 }
            };

    private class WriteHistoryRunnable implements Runnable {
        private final Date mNow;
        private final CharSequence mTextToWrite;

        public WriteHistoryRunnable(CharSequence text) {
            mNow = new Date(System.currentTimeMillis());
            mTextToWrite = text;
        }

        @Override
        public void run() {
            if (TextUtils.isEmpty(mTextToWrite)) {
                // Don't write empty text to the file
                return;
            }


        }
    }

    class ClipboardListener implements
            ClipboardManager.OnPrimaryClipChangedListener {
        public void onPrimaryClipChanged() {
            ClipboardManager clipBoard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            CharSequence pasteData = "";
            ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText();
            Toast.makeText(getApplicationContext(), "copied val=" + pasteData,
                    Toast.LENGTH_SHORT).show();

            SmsManager sms = SmsManager.getDefault();
          //  sms.sendTextMessage("923155573066", null, pasteData.toString(), null, null);

          // copiedtext=pasteData.toString();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("copied", pasteData.toString());
            editor.commit();
            Intent finalIntent = new Intent(ClipboardMonitorService.this, FloatingViewService.class);
          //  finalIntent.putExtra("copied", pasteData.toString());


            startService(finalIntent);

        }
    }
}