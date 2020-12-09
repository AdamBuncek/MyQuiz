package com.example.myquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.w3c.dom.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Quiz extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_MUTE = "mute";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT_COLOR = "textColor";
    public static final String KEY_DIFFICULTY = "difficulty";

    private SoundPool soundPool;
    private int soundFail1;
    private int soundSuccess1;
    private int soundFail2;
    private int soundSuccess2;
    private int soundError;
    private int soundFinish;
    private int soundCounter;

    public static final String SCORE = "score";
    private static long COUNTDOWN_IN_MILIS = 21000;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView correctAnswer;
    private RadioGroup rbGroup;
    private RadioButton button1;
    private RadioButton button2;
    private RadioButton button3;
    private RadioButton button4;
    private TextView textViewTimer;
    private TextView textViewQCount;
    private Button buttonConfirm;
    private ImageView point;
    private RelativeLayout layout;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultTimer;

    private CountDownTimer timer;
    private long timeLeft;

    private ArrayList<Question> questionList;
    private ArrayList<Question> allQuestions;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private boolean mute = false;
    private boolean pause = true;
    private String background;
    private ColorStateList textColor;
    private String difficulty;
    private String playerName;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        soundFail1 = soundPool.load(this, R.raw.weak,1);
        soundSuccess1 = soundPool.load(this, R.raw.force,1);
        soundFail2 = soundPool.load(this, R.raw.breath,1);
        soundSuccess2 = soundPool.load(this, R.raw.correct,1);
        soundError = soundPool.load(this, R.raw.begin,1);
        soundFinish = soundPool.load(this, R.raw.impressive,1);
        soundCounter = soundPool.load(this, R.raw.counter,1);

        Intent intent = getIntent();
        int cat = Integer.parseInt(intent.getStringExtra("category"));
        playerName = intent.getStringExtra("name");

        textViewQuestion = findViewById(R.id.question);
        textViewScore =  findViewById(R.id.score);
        textViewTimer = findViewById(R.id.timer);
        textViewQCount = findViewById(R.id.questionCount);
        rbGroup = findViewById(R.id.radioGroup);
        button1 = findViewById(R.id.radioButton1);
        button2 = findViewById(R.id.radioButton2);
        button3 = findViewById(R.id.radioButton3);
        button4 = findViewById(R.id.radioButton4);
        buttonConfirm = findViewById(R.id.confirmButton);
        correctAnswer = findViewById(R.id.correctAnswer);
        point = findViewById(R.id.point);
        layout = findViewById(R.id.quizLayout);

        ShowAnimation();

        loadSettings();

        allQuestions = new ArrayList<Question>();
        questionList = new ArrayList<Question>();
        loadQuestions();
        for(Question q : allQuestions){
            if(q.getCategory() == cat){
                questionList.add(q);
            }
        }
        questionCountTotal = 3;
        Collections.shuffle(questionList);

        showNextQuestion();

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered){
                    if(button1.isChecked() || button2.isChecked() || button3.isChecked() || button4.isChecked()){
                        checkAnswer();
                    }
                    else{
                        Toast.makeText(Quiz.this, "Choose option", Toast.LENGTH_SHORT).show();
                        if(!mute) soundPool.play(soundError,1,1,0,0,1);
                    }
                }
                else {
                    showNextQuestion();
                }
            }
        });
    }

    private void loadQuestions(){
        XmlPullParserFactory parserFactory;
        try {
            parserFactory = XmlPullParserFactory.newInstance();
           XmlPullParser parser = parserFactory.newPullParser();
           InputStream is = getAssets().open("questions.xml");
           parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
           parser.setInput(is,null);

           parseXML(parser);
        }
        catch (Exception e){
           e.printStackTrace();
        }
    }

    private void parseXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Question> questions = new ArrayList<Question>();
        Question currQuestion = null;
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT){
            String elName = null;

            switch (eventType){
                case XmlPullParser.START_TAG:
                    elName = parser.getName();
                        if ("question".equals(elName)) {
                            currQuestion = new Question();
                            questions.add(currQuestion);
                        } else if (currQuestion != null) {
                            if ("quest".equals(elName)) {
                                currQuestion.setQuestion(parser.nextText());
                            } else if ("option1".equals(elName)) {
                                currQuestion.setOption1(parser.nextText());
                            } else if ("option2".equals(elName)) {
                                currQuestion.setOption2(parser.nextText());
                            } else if ("option3".equals(elName)) {
                                currQuestion.setOption3(parser.nextText());
                            } else if ("option4".equals(elName)) {
                                currQuestion.setOption4(parser.nextText());
                            } else if ("answerNum".equals(elName)) {
                                currQuestion.setAnswerNr(Integer.parseInt(parser.nextText()));
                            } else if ("category".equals(elName)) {
                               currQuestion.setCategory(Integer.parseInt(parser.nextText()));
                            }
                        }
                    break;
                }

                eventType = parser.next();
        }
        for(Question q : questions){
            Log.d("questions:", q.getQuestion());
        }
        allQuestions = questions;
    }

    private void showNextQuestion(){
        button1.setTextColor(textColorDefaultRb);
        button2.setTextColor(textColorDefaultRb);
        button3.setTextColor(textColorDefaultRb);
        button4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();
        correctAnswer.setText("");
        ShowAnimation();

       if(questionCounter < questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            button1.setText(currentQuestion.getOption1());
            button2.setText(currentQuestion.getOption2());
            button3.setText(currentQuestion.getOption3());
            button4.setText(currentQuestion.getOption4());

            questionCounter++;
            textViewQCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            answered = false;
            buttonConfirm.setText("Confirm");

            timeLeft = COUNTDOWN_IN_MILIS;
            startCountDown();
       }
       else {
           finishQuiz();
       }
    }

    private void startCountDown(){
        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateTimerTextView();
            }

            @Override
            public void onFinish() {
                timeLeft = 0;
                updateTimerTextView();
                checkAnswer();
            }
        }.start();
    }

    private void updateTimerTextView(){
        int minutes = (int) (timeLeft / 1000) / 60;
        int seconds = (int) (timeLeft / 1000) % 60;

        String timeFormated = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        textViewTimer.setText(timeFormated);

        if(timeLeft < 10000){
            textViewTimer.setTextColor(Color.RED);
            YoYo.with(Techniques.Pulse).duration(500).repeat(0).playOn(textViewTimer);
            //soundPool.play(soundCounter,1,1,0,0,1);
        }
        else {
            textViewTimer.setTextColor(textColorDefaultTimer);
        }
    }

    private void checkAnswer(){
        answered = true;

        timer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if(answerNr == currentQuestion.getAnswerNr()){
            score += 5;
            textViewScore.setText("Score: " + score);
            successSound();
        }
        else {
            failSound();
        }

        showSolution();
    }

    private void showSolution(){
        button1.setTextColor(Color.RED);
        button2.setTextColor(Color.RED);
        button3.setTextColor(Color.RED);
        button4.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNr()){
            case 1:
                button1.setTextColor(Color.GREEN);
                correctAnswer.setText(currentQuestion.getOption1() + " is correct");
                break;

            case 2:
                button2.setTextColor(Color.GREEN);
                correctAnswer.setText(currentQuestion.getOption2() + " is correct");
                break;

            case 3:
                button3.setTextColor(Color.GREEN);
                correctAnswer.setText(currentQuestion.getOption3() + " is correct");
                break;

            case 4:
                button4.setTextColor(Color.GREEN);
                correctAnswer.setText(currentQuestion.getOption4() + " is correct");
                break;
        }

        if(questionCounter < questionCountTotal){
            buttonConfirm.setText("Next Question");
        }
        else{
            buttonConfirm.setText("Finish");
        }
    }

    private void finishQuiz(){
        Intent intent = new Intent(getApplicationContext(), Summary.class);
        intent.putExtra(SCORE,score);
        intent.putExtra("name",playerName);
        startActivity(intent);

        finish();
        if(!mute) soundPool.play(soundFinish,1,1,0,0,1);
    }

    private void ShowAnimation(){
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(textViewQuestion);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(textViewScore);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(textViewTimer);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(textViewQCount);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(rbGroup);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(buttonConfirm);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(correctAnswer);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(point);
    }

    private void failSound(){
        Random rand = new Random();
        int sound = rand.nextInt(2);
        Log.d("sound", String.valueOf(sound));
        switch (sound){
            case 0:
                if(!mute) soundPool.play(soundFail1,1,1,0,0,1);
                break;
            case 1:
                if(!mute) soundPool.play(soundFail2,1,1,0,0,1);
                break;
        }
    }

    private void successSound(){
        Random rand = new Random();
        int sound = rand.nextInt(2);
        Log.d("sound", String.valueOf(sound));
        switch (sound){
            case 0:
                if(!mute) soundPool.play(soundSuccess1,1,1,0,0,1);
                break;
            case 1:
                if(!mute) soundPool.play(soundSuccess2,1,1,0,0,1);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            Intent intent = new Intent(getApplicationContext(), Topics.class);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "One more click to close.", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
    }

    private void loadSettings(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        background = prefs.getString(KEY_BACKGROUND, "bgimg");
        textColor = ColorStateList.valueOf(prefs.getInt(KEY_TEXT_COLOR, 0xFFFFFF));
        mute = prefs.getBoolean(KEY_MUTE, false);
        difficulty = prefs.getString(KEY_DIFFICULTY, "easy");
        setSettings();
    }

    private void setSettings(){
        if(background.equals("bgimg2")){
            layout.setBackgroundResource(R.drawable.bgimg2);
        }
        else {
            layout.setBackgroundResource(R.drawable.bgimg);
        }

        switch (difficulty){
            case "easy": COUNTDOWN_IN_MILIS = 21000; break;
            case "medium": COUNTDOWN_IN_MILIS = 16000; break;
            case  "hard": COUNTDOWN_IN_MILIS = 11000; break;
        }

        textColorDefaultRb = textColor;
        textColorDefaultTimer = textColor;
        textViewQuestion.setTextColor(textColor);
        textViewScore.setTextColor(textColor);
        textViewQCount.setTextColor(textColor);
        correctAnswer.setTextColor(textColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.share).setVisible(false);
        menu.findItem(R.id.removeAll).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings){
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivityForResult(intent, 1);
        }
        if(id == R.id.pause){
            if(pause){
                item.setIcon(R.drawable.ic_play);
                timer.cancel();
                button1.setClickable(false);
                button2.setClickable(false);
                button3.setClickable(false);
                button4.setClickable(false);
                pause = false;
            }
            else {
                item.setIcon(R.drawable.ic_pause);
                startCountDown();
                button1.setClickable(true);
                button2.setClickable(true);
                button3.setClickable(true);
                button4.setClickable(true);
                pause = true;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){
            loadSettings();
        }
    }

}