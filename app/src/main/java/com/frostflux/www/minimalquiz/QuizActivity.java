package com.frostflux.www.minimalquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.widget.Toast;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements RecognitionListener {

    private TextView mScoreView, mQuestion;
    private Button mTrueButton;

    private boolean mAnswer;
    private int mScore = 0;
    private int mQuestionNumber = 0;
    private int pivot = 0;
    public int rotation = 0;

    public List<Integer> arr = new ArrayList<>();

    // Start Speech Recognition
    // Declare
    MediaPlayer player,s_sound;

    // For Speech Synthesizer
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private TextView returnedText;
    private ToggleButton toggleButton;
    private ProgressBar progressBar;
    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private String LOG_TAG = "MainActivity";

    private AudioManager mAudioManager;
    private int mStreamVolume = 0;
    private Handler mHandler = new Handler();

    String answer = "hello";


    Boolean debug   = false;
    Boolean debug_1 = false;
    Boolean debug_1_val = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        TextView mScores = (TextView) findViewById(R.id.scores);
        mScoreView = (TextView) findViewById(R.id.points);
        mQuestion =  (TextView) findViewById(R.id.question);
        mTrueButton= (Button)   findViewById(R.id.trueButton);
        Button mToggle = (Button) findViewById(R.id.toggleButton1);
        Button mPlay = (Button) findViewById(R.id.debug_play);
        Button mStop= (Button)   findViewById(R.id.debug_stop);

        // Make Temporary List and Shuffle it
        List<Integer> arr_temp = new ArrayList<>();
        for(int i=0; i<6; i++)
            arr_temp.add(i+1);
        Collections.shuffle(arr_temp);

        // Put the temporary shuffled list to global list arr
        arr = arr_temp;

        for(int i=0; i<6; i++)
            Log.d("Debug: ", Integer.toString(i) + ":" + arr.get(i).toString());
        Log.d("Debug: ", "======================");

        // Animation Start
        Animation frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        Animation fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);

        // Animation End

        mScores.startAnimation(fromtop);
        mScoreView.startAnimation(fromtop);
        mQuestion.startAnimation(fromtop);
        mTrueButton.startAnimation(fromtop);
        mPlay.startAnimation(frombottom);
        mStop.startAnimation(frombottom);
        mToggle.startAnimation(frombottom);

        rotation++;
        updateQuestion();

        // Logic

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mAnswer == true){
                    mScore++;
                    updateScore(mScore);

                    // Check if last question
                    if(rotation == Database.questions.length){
                        Intent i = new Intent(QuizActivity.this,ResultsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("finalScore",mScore);
                        i.putExtras(bundle);
                        QuizActivity.this.finish();
                        startActivity(i);
                    }
                    else{
                        updateQuestion();
                    }
                    // End Check

                }
                else{
                    // Check if last question
                    if(mQuestionNumber == Database.questions.length){
                        Intent i = new Intent(QuizActivity.this,ResultsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("finalScore",mScore);
                        i.putExtras(bundle);
                        QuizActivity.this.finish();
                        startActivity(i);
                    }
                    else{
                        updateQuestion();
                    }
                    // End Check

                }

            }
        });
        // End Button


        // End Button

        // Start Speech Recognition
        // Start onCreate
        returnedText  = (TextView)     findViewById(R.id.textView1);
        progressBar   = (ProgressBar)  findViewById(R.id.progressBar1);
        toggleButton  = (ToggleButton) findViewById(R.id.toggleButton1);

        // DEBUG INITIALIZE
        Button mdebug_play = (Button) findViewById(R.id.debug_play);

        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                    ActivityCompat.requestPermissions
                            (QuizActivity.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    REQUEST_RECORD_PERMISSION);
                } else {
                    progressBar.setIndeterminate(false);
                    progressBar.setVisibility(View.INVISIBLE);
                    speech.stopListening();
                }
            }
        });

        if(debug) {
            mdebug_play.performClick();
        }
    }

    private void updateQuestion() {
        //mQuestionNumber++;
        mQuestionNumber= arr.get(pivot);
        mQuestion.setText(Database.questions[mQuestionNumber]);
        mAnswer = Database.answers[mQuestionNumber];

        pivot++;

        Log.d("Debug: ",Integer.toString(mQuestionNumber)  );
        Log.d("Debug: ", "======================" + Integer.toString(rotation) + ":" + Integer.toString(Database.questions.length));
        Toast.makeText(this, Database.check[mQuestionNumber], Toast.LENGTH_LONG).show();
        rotation++;

        // Animation

        // Animation Start
        Animation frombottom = AnimationUtils.loadAnimation(this, R.anim.frombottom);
        Animation fromtop = AnimationUtils.loadAnimation(this,R.anim.fromtop);
        Animation fromright = AnimationUtils.loadAnimation(this,R.anim.fromright);
        // Animation End

        TextView mScores = (TextView) findViewById(R.id.scores);
        Button mToggle = (Button) findViewById(R.id.toggleButton1);
        Button mPlay = (Button) findViewById(R.id.debug_play);
        Button mStop= (Button)   findViewById(R.id.debug_stop);

        mScores.startAnimation(fromtop);
        mScoreView.startAnimation(fromtop);
        mQuestion.startAnimation(fromright);
        mToggle.startAnimation(frombottom);
    }

    public void updateScore(int point){
        mScoreView.setText("" + point);
    }

    // Start Speech

    public void play(View v){
        Log.d("Debug: ", String.valueOf(player));
        Log.d("Debug: ", "Player mQuestionNumber: " + String.valueOf(mQuestionNumber));
        if(player == null){

            if(mQuestionNumber==1)
                player = MediaPlayer.create(this,R.raw.song_1);
            else if (mQuestionNumber==2)
                player = MediaPlayer.create(this,R.raw.song_2);
            else if (mQuestionNumber==3)
                player = MediaPlayer.create(this,R.raw.song_3);
            else if (mQuestionNumber==4)
                player = MediaPlayer.create(this,R.raw.song_4);
            else if (mQuestionNumber==5)
                player = MediaPlayer.create(this,R.raw.song_5);
            else if (mQuestionNumber==6)
                player = MediaPlayer.create(this,R.raw.song_6);

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                    toggleButton.performClick();
                }
            });
        }

        player.start();
    }

    public void pause(View v){
        if(player != null){
            player.pause();
        }
    }

    public void stop(View v){
        stopPlayer();
    }

    private void stopPlayer(){
        if(player!=null){
            player.release();
            player=null;
            Toast.makeText(this,"Media Player Released", Toast.LENGTH_SHORT);
        }
    }

    /*
    @Override
    protected void onStop(){
        super.onStop();
        stopPlayer();
    }*/

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speech.startListening(recognizerIntent);

                    // [ A1= Fix: Remove Speech Synthesizer Sound]
                    mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); // getting system volume into var for later un-muting
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0); // setting system volume to zero, muting
                    // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    //         AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                } else {
                    Toast.makeText(QuizActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (speech != null) {
            speech.destroy();
            Log.i(LOG_TAG, "destroy");
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(LOG_TAG, "onBufferReceived: " + buffer);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
        progressBar.setIndeterminate(true);
        toggleButton.setChecked(false);

        // [ A1= Fix: Remove Speech Synthesizer Sound]
        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);



        //mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
        //        AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        returnedText.setText(errorMessage);
        toggleButton.setChecked(false);

        if(debug_1) {
            if(debug_1_val)
                s_sound = MediaPlayer.create(this, R.raw.correct);
            else
                s_sound = MediaPlayer.create(this, R.raw.wrong);

            s_sound.start();
        }
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle arg0) {
        Log.i(LOG_TAG, "onPartialResults");
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onResults(Bundle results) {
        startAudioSound();

        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = matches.get(0);

        if(debug){
            text = "hello";
        }

        //if(answer.equals(text)) {
        if(Database.check[mQuestionNumber].equals(text.toLowerCase())) {
            s_sound = MediaPlayer.create(this, R.raw.correct);
            text += " = Correct!";
            text += Database.check[mQuestionNumber];

        }
        else {
            s_sound = MediaPlayer.create(this, R.raw.wrong);
            text += " = Wrong, Try Again";
            text += Database.check[mQuestionNumber];
        }
        s_sound.start();

        returnedText.setText(text);
    }


    // [ A1= Fix: Remove Speech Synthesizer Sound]
    private void startAudioSound() {
        //  mHandler.postDelayed(() -> {
        //      mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0); // again setting the system volume back to the original, un-mutting
        //  }, 300);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Check your Internet Connection";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

}
