package com.shoppingbyear;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity {

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private Button button;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpeningRing();



        button = (Button)findViewById(R.id.btn);
        button.setOnClickListener(new ButtonClickListener());
    }

    void OpeningRing()
    {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {


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
                        textToSpeech.speak(getString(R.string.opening_prompt), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        });
    }

    class ButtonClickListener implements View.OnClickListener
    {
        public void onClick(View view)
        {
            if (view.getId() == R.id.btn)
            {
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
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("MainActivity.java", "You said: " + result.get(0));

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
}
