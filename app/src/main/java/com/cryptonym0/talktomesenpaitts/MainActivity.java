package com.cryptonym0.talktomesenpaitts;

import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    //Create variables
    public static int TTS_DATA_CHECK = 1;
    public static int VOICE_RECOGNITION = 2;
    private TextToSpeech tts;
    public String demotext = "This is a test of the text-to-speech engine in Android.";
    EditText txtinput;
    ArrayList<String> arrl;
    ArrayAdapter<String> adapter;
    Button buttonSR, buttonTTS, buttonCLEAR;
    ImageView ai;
    int img1, img2;
    Handler mHandler = new Handler();
    private boolean doneTalking = true;


    private int mInterval = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Declare inputs and views


        //Create array and array adapter
        arrl = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, arrl);

        txtinput = (EditText) findViewById(R.id.editText1);
        buttonSR = (Button) findViewById(R.id.buttonSR);
        buttonTTS = (Button) findViewById(R.id.buttonTTS);
        buttonCLEAR = (Button) findViewById(R.id.clearBTN);


        buttonSR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testSR();
            }
        });
        buttonTTS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTTS();
            }
        });
        buttonCLEAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtinput.setText("");
            }
        });

        img1 = getResources().getIdentifier("char_a_win", "drawable", getPackageName());
        img2 = getResources().getIdentifier("char_a_neutral", "drawable", getPackageName());
        ai = (ImageView) findViewById(R.id.ai);

    }//end main

    //tes the SR
    public void testSR() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Just speak normally into your phone");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        //try
        try {
            startActivityForResult(intent, VOICE_RECOGNITION);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //TODO!! Toast here
        }
    }

    //Test the TTS
    public void testTTS() {
        demotext = txtinput.getText().toString();
        if (demotext.isEmpty()) {
            Log.i("SpeechDemo", "## ERROR 02: Field is Empty");
            demotext = "The field is empty. Type something first!";
        }

        Intent intent = new Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, TTS_DATA_CHECK);

        //TODO! Start toggle face thread here
        Toast.makeText(getBaseContext(), "Testing Text to Speech", Toast.LENGTH_SHORT).show();
    }

    //On activity result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get my thread
//        TODO!! Fix the thread
//        final ThreadMe gf = new ThreadMe();

        if (requestCode == TTS_DATA_CHECK) {
            Log.i("SpeechDemo", "## INFO 01: RequestCode TTS_DATA_CHECK = " + requestCode);
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                Log.i("SpeechDemo", "## INFO 03: CHECK_VOICE_DATA_PASS");
                tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int arg0) {
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onDone(String utteranceId) {
                                doneTalking = false;
                                // Log.d("MainActivity", "TTS finished");
                            }

                            @Override
                            public void onError(String utteranceId) {
                            }

                            @Override
                            public void onStart(String utteranceId) {
                            }
                        });


                        if (tts.isLanguageAvailable(Locale.US) >= 0) {
                            tts.setLanguage(Locale.ENGLISH);
                            tts.setPitch(4.0f);
                            tts.setSpeechRate(0.8f);
//                            gf.execute(); TODO!! 2


//                                repeatMyToggle.run();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ttsGreater21(demotext);
                            } else {
                                ttsUnder20(demotext);
                            }

                        }
                    }

                });


            } else {
                Log.i("SpeechDemo", "## INFO 04: CHECK_VOICE_DATA_FAILED, resultCode = " + resultCode);
                Intent installVoice = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installVoice);

            }


        } else if (requestCode == VOICE_RECOGNITION) {
            Log.i("SpeechDemo", "## INFO 02: RequestCode VOICE_RECOGNITION = " + requestCode);
            if (resultCode == RESULT_OK) {

                List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                for (int i = 0; i < results.size(); i++) {
                    final String result = results.get(i);
                    Log.i("SpeechDemo", "## INFO 05: Result: " + result);
                    txtinput.setText(result);
                    testTTS();
                }
            }
        } else {
            Log.i("SpeechDemo", "## ERROR 01: Unexpected RequestCode = " + requestCode);
        }
    }

    //Deprication?
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    //Set target API
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    //On pause
    @Override
    protected void onPause() {
        super.onPause();
        if (tts != null) tts.shutdown();
        stopMe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMe();
    }

    public boolean stopMe() {
        return doneTalking = true;
    }

    public void toggleFace() {
        ai.setImageResource(img1);
        ai.postDelayed(new Runnable() {
            @Override
            public void run() {
                ai.setImageResource(img2);
            }
        }, 200); // 4000ms delay
    }

//    public void startRepeatingTask() {
//        repeatMyToggle.run();
//    }
//
//    public void startMe() {
//        doneTalking = false;
//    }
//
//    //male face toggle runnable
//    Runnable repeatMyToggle = new Runnable() {
//        @Override
//        public void run() {
//            while (!doneTalking) {
//                try {
//                    toggleFace();
//                } finally {
//                    // 100% guarantee that this always happens, even if
//                    // your update method throws an exception
//                    mHandler.postDelayed(repeatMyToggle, mInterval);
//                }
//            }
//
//        }
//
//    };

}
    /*
    * Toggle face in thread so.....we don't lock the interface
    * 2/30/2017 never forgetti :'(
    * */
//    public class ThreadMe extends AsyncTask<Integer, String, Integer> {
//        Handler ha = new Handler();
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            img1            = getResources().getIdentifier("char_a_win", "drawable", getPackageName());
//            img2            = getResources().getIdentifier("char_a_neutral", "drawable", getPackageName());
//            ai              = (ImageView)findViewById(R.id.ai);
//        }
//
//        @Override
//        protected Integer doInBackground(Integer... in) {
//            Log.d("Thread is Starting", "");
//            //TODO!! Begin thread tasks
//            startRepeatingTask();
//            return null;
//
//        }
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//            //TODO!! change stuff
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            stopMe();
//            //TODO! set face back to normal
//
//        }
//
//        public void toggleFace(){
//            try {
//                ai.setImageResource(img1);
//                ai.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        ai.setImageResource(img2);
//                    }
//                }, 200); // 4000ms delay
//            }finally{
//                Log.d("FACE TOGGLED", "");
//            }
//        }
//
//        public void startRepeatingTask() {
//            repeatMyToggle.run();
//        }
//        public boolean stopMe(){
//            return doneTalking = true;
//        }
//        public void startMe(){
//            doneTalking = false;
//        }
//
//        //male face toggle runnable
//        Runnable repeatMyToggle = new Runnable() {
//
//            @Override
//            public void run() {
//                while(!doneTalking) {
//                    try {
//                        Log.d("REPEAT is Starting", "Itteraitng");
//                        toggleFace();
//                    } finally {
//                        // 100% guarantee that this always happens, even if
//                        // your update method throws an exception
//                        mHandler.postDelayed(repeatMyToggle, mInterval);
//                    }
//                }
//
//            }
//
//        };
//
//
//        }
//    }//End Thread

