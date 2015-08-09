package com.shoppingbyear;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import android.support.v7.app.ActionBarActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    TextToSpeech tts;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private Button button;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(this, this);

        OpeningRing();



        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new ButtonClickListener());
    }
    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            //isTTSready = true;
            int result = tts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    | result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "Language is missing",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    void OpeningRing()
    {

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {


            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    int result = textToSpeech.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TextToSpeech", "This Language is not supported");
                    }
                    else
                    {
                        Log.d("TextToSpeech","Success");
                        tts.speak("Hello. What kind of clothes are you looking for?", TextToSpeech.QUEUE_ADD, null, null);

                    }
                }
            }
        });
    }

    class ButtonClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {

            if (view.getId() == R.id.btn) {
                Log.d("MainActivity.java", "Button was clicked!");

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        getString(R.string.speech_prompt));



                try
                {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                }
                catch (ActivityNotFoundException a)
                {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.speech_not_supported),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && null != data)
                {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //result.get(0) is what has been said
                    Log.d("MainActivity.java", "You said: " + result.get(0));
                    new Thread(new Runnable() {
                        public void run() {
                            getMaciesClothes(result.get(0));

                        }
                    }).start();

                    //next step
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public static void getMaciesClothes(String said){

        //Map<String, String> headers = new HashMap<>();
        //headers.put("", "atthack2015");

        android.util.Log.v("Status ", "Trying ");
        //String dataUrl = "http://api.macys.com/v4/catalog/product/2298660";

        //    connection.setRequestProperty("accept", "application/json");
        //    connection.setRequestProperty("x-macys-webservice-client-id", "atthack2015");
        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object

        //String url ="http://api.macys.com/v4/catalog/product/2298660";
        String url ="http://api.macys.com/v4/catalog/search?searchphrase="+said;
        HttpGet httpget = new HttpGet(url);
        httpget.setHeader("accept", "application/json");
        httpget.setHeader("x-macys-webservice-client-id", "atthack2015");
      //  httpget.setHeader("search phrase", "yellow");

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda",response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);
                // now you have the string representation of the HTML request
                instream.close();
                Log.d("hii", result);
            }


        } catch (Exception e) {}

    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
