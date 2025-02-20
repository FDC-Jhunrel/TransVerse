package com.example.speechtranslation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.speechtranslation.Activities.Login;
import com.example.speechtranslation.AuthenticationTokens.TokenAuthentication;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;


public class  MainActivity extends AppCompatActivity {

    private ImageButton btnSpeak, btnSpeaker;
    private Button btnTranslate;
    private TextView tvTranslatedText, tvSignout, tvTranslation;
    private TextInputEditText etSTT;
//    private static final String TRANSLATE_API_URL = "https://translation.googleapis.com/language/translate/v2?";
//    private static final String TEXT_TO_SPEECH_URL = "https://texttospeech.googleapis.com/v1/text:synthesize?";
//    private static final String KEY = "key=AIzaSyBHyupTlNppWfDqgceWb7ynXrhMpDXlq-4";
//    private static final String API_URL = TEXT_TO_SPEECH_URL+KEY;
    private Spinner spSource, spTarget;
    private String  sourceLang[] = {"English","Japanese","Korean","Taiwanese","Vietnamese","Filipino" };
    private String targetLang[] = {"Japanese","Korean","Taiwanese","Vietnamese","Filipino","English"};

    private static final int REQUEST_PERMISSION_CODE = 1;
    public String selectedSource, selectedTarget;

    private Handler handler;
    private Runnable runnable;
    private String[] strings = {"TransVerse", "トランスバース", "횡축", "Ngang", "橫"} ;
    private int currentIndex = 0;
    private MediaPlayer mediaPlayer;
    private TokenAuthentication auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etSTT = findViewById(R.id.etSTT);
        btnSpeak = findViewById(R.id.btnSpeak);
        tvTranslatedText = findViewById(R.id.tvTranslatedText);
        btnSpeaker = findViewById(R.id.btnSpeaker);
        btnTranslate = findViewById(R.id.btnTranslate);
        spSource = findViewById(R.id.spinnerSource);
        spTarget = findViewById(R.id.spinnerTarget);
        tvSignout = findViewById(R.id.tvSignout);
        tvTranslation = findViewById(R.id.tvTranslation);
        btnTranslate.setBackgroundColor(getResources().getColor(R.color.black));
        textAnim();

         auth = new TokenAuthentication();

//      Set spinner values for source language
        ArrayAdapter<String> adapterSource = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sourceLang);
        spSource.setAdapter(adapterSource);
        spSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 selectedSource = parent.getItemAtPosition(position).toString();
                switch (selectedSource){
                    case "Japanese" :
                        selectedSource = "ja";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                    case "Korean" :
                        selectedSource = "ko";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                    case "Taiwanese" :
                        selectedSource = "zh-TW";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                    case "Vietnamese" :
                        selectedSource = "vi";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                    case "Filipino" :
                        selectedSource = "fil";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                    case "English":
                        selectedSource = "en";
                        Toast.makeText(MainActivity.this, "source language: " +selectedSource,Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//      Set spinner values for target language
        ArrayAdapter<String> adapterTarget = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, targetLang);
        spTarget.setAdapter(adapterTarget);
        spTarget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTarget = parent.getItemAtPosition(position).toString();
                switch (selectedTarget){
                    case "English" :
                        selectedTarget = "en";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();
                        break;
                    case "Korean" :
                        selectedTarget = "ko";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();
                        break;
                    case "Taiwanese" :
                        selectedTarget = "zh-TW";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();
                        break;
                    case "Vietnamese" :
                        selectedTarget = "vi";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();
                        break;
                    case "Filipino" :
                        selectedTarget = "fil";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        selectedTarget = "ja";
                        Toast.makeText(MainActivity.this, "target language: " +selectedTarget,Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

       btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translateNow();
            }
        });

//       btnSpeaker.setVisibility(View.GONE);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  String toSpeech = etSTT.getText().toString();
                  TTS();
            }
        });

        tvSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void speak() {
        String extraLanguage = spSource.getSelectedItem().toString();
        switch (extraLanguage) {
            case "English":
                extraLanguage = "en-US";
                break;
            case "Korean":
                extraLanguage = "ko-KR";
                break;
            case "Taiwanese":
                extraLanguage = "cmn-TW";
                break;
            case "Vietnamese":
                extraLanguage = "vi-VN";
                break;
            case "Filipino":
                extraLanguage = "fil-PH";
                break;
            case "Japanese":
                extraLanguage = "ja-JP";
                break;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, extraLanguage);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start speaking");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            String speechToText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            etSTT.setText(speechToText);
        }else{
            Toast.makeText(MainActivity.this, "Sever error", Toast.LENGTH_SHORT).show();
        }
    }
    private void translateNow(){
        String sourceText = etSTT.getText().toString();
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = auth.getSuffixTranslateApiURl()
                + "&source=" +selectedSource
                + "&target=" + selectedTarget
                + "&q=" +sourceText;

//      Request a string response from the provided URL.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject data = response.getJSONObject("data");
                            String translatedText = data.getJSONArray("translations")
                                    .getJSONObject(0)
                                    .getString("translatedText");
                            tvTranslatedText.setText(translatedText);
                        } catch (JSONException e) {
                            tvTranslatedText.setText(e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("TranslateAPI", "Volley Error: " + error.toString());
            }
        });
//      Add the request to the RequestQueue.
        queue.add(request);
    }
    private  void textAnim(){
        // Initialize the handler and runnable
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // Update the text
                tvTranslation.setText(strings[currentIndex]);

                // Increment the index
                currentIndex = (currentIndex + 1) % strings.length;

                // Delay for 1 second and then repeat
                handler.postDelayed(this, 1000);
            }
        };

        // Start the periodic update
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Stop the periodic update when the activity is destroyed
        handler.removeCallbacks(runnable);

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void TTS(){
        // Text to be converted to speech
        String text = tvTranslatedText.getText().toString();
        String langCode= spTarget.getSelectedItem().toString();

        switch (langCode){
            case "English" :
                langCode = "en-US";
                break;
            case "Korean" :
                langCode = "ko-KR";
                break;
            case "Taiwanese" :
                langCode = "cmn-TW";
                break;
            case "Vietnamese" :
                langCode = "vi-VN";
                break;
            case "Filipino" :
                langCode = "fil-PH";
                break;
            default:
                langCode = "ja-JP";
        }

        // Request JSON payload
        JSONObject jsonPayload = new JSONObject();
        try {
            jsonPayload.put("input", new JSONObject().put("text", text));
            jsonPayload.put("voice", new JSONObject().put("languageCode", langCode));
            jsonPayload.put("audioConfig", new JSONObject().put("audioEncoding", "MP3"));
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        // Create a Volley request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                auth.getFinalTextToSpeechUrl(),
                jsonPayload,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Extract the audio content from the response
                            String audioContent = response.getString("audioContent");

                            // Convert the base64-encoded audio content to bytes
                            byte[] audioBytes = android.util.Base64.decode(audioContent, android.util.Base64.DEFAULT);

                            // Create a temporary file to store the audio content
                            java.io.File tempFile = java.io.File.createTempFile("tempAudio", "mp3", getCacheDir());
                            tempFile.deleteOnExit();

                            // Write the audio content to the temporary file
                            java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
                            fos.write(audioBytes);
                            fos.close();

                            // Initialize a media player with the temporary audio file
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(tempFile.getAbsolutePath());
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                        } catch (JSONException | IOException e) {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // Add the request to the Volley request queue
        Volley.newRequestQueue(MainActivity.this).add(request);

    }

}