package com.example.myquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_MUTE = "mute";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT_COLOR = "textColor";
    public static final String KEY_DIFFICULTY = "difficulty";

    private TextView myText;
    private Button img1;
    private Button img2;
    private CheckBox muteCheckBox;
    private Button save;
    private RelativeLayout relativeLayout;
    private RadioGroup radioGroup;
    private RadioButton easy;
    private RadioButton medium;
    private RadioButton hard;

    private String background;
    private boolean mute;
    private int textColor;
    private ColorStateList colorStateList;
    private String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        relativeLayout = findViewById(R.id.settingsRelative);
        myText = findViewById(R.id.textViewSettings);
        img1 = findViewById(R.id.button);
        img2 = findViewById(R.id.button2);
        muteCheckBox = findViewById(R.id.checkBox);
        save = findViewById(R.id.saveButton);
        radioGroup = findViewById(R.id.difficulty);
        easy = findViewById(R.id.easy);
        medium = findViewById(R.id.medium);
        hard = findViewById(R.id.hard);

        loadSettings();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textColor = 0xFFFFFFFF;
                saveSettings();
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
            }
        });
    }

    public void onButton(View view){
        textColor = 0xFFFFFFFF;
        myText.setTextColor(ColorStateList.valueOf(textColor));
        muteCheckBox.setTextColor(textColor);
        easy.setTextColor(textColor);
        medium.setTextColor(textColor);
        hard.setTextColor(textColor);

        if(view == img1){
            relativeLayout.setBackgroundResource(R.drawable.bgimg2);
            background = "bgimg2";
        }
        if (view == img2){
            relativeLayout.setBackgroundResource(R.drawable.bgimg);
            background = "bgimg";
        }
    }

    public void onCheckbox(View view){
        if(muteCheckBox.isChecked()){
            mute = true;
        }
        else{
            mute = false;
        }
    }

    public void checkDifficulty(View view){
        RadioButton rbSelected = findViewById(radioGroup.getCheckedRadioButtonId());
        if(radioGroup.indexOfChild(rbSelected) == 0){
            difficulty = "easy";
        }
        else if(radioGroup.indexOfChild(rbSelected) == 1){
            difficulty = "medium";
        }
        else if(radioGroup.indexOfChild(rbSelected) == 2){
            difficulty = "hard";
        }
    }

    private void saveSettings(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_BACKGROUND, background);
        editor.putBoolean(KEY_MUTE, mute);
        editor.putInt(KEY_TEXT_COLOR, textColor);
        editor.putString(KEY_DIFFICULTY, difficulty);
        editor.apply();
    }

    private void loadSettings(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        background = prefs.getString(KEY_BACKGROUND, "bgimg");
        colorStateList = ColorStateList.valueOf(prefs.getInt(KEY_TEXT_COLOR, 0x000000));
        mute =  prefs.getBoolean(KEY_MUTE, false);
        difficulty = prefs.getString(KEY_DIFFICULTY,"easy");
        setSettings();
    }

    private void setSettings(){
        if(background.equals("bgimg2")){
            relativeLayout.setBackgroundResource(R.drawable.bgimg2);
        }
        else {
            relativeLayout.setBackgroundResource(R.drawable.bgimg);
        }
        myText.setTextColor(colorStateList);
        muteCheckBox.setTextColor(colorStateList);
        easy.setTextColor(colorStateList);
        medium.setTextColor(colorStateList);
        hard.setTextColor(colorStateList);
        if (mute){
            muteCheckBox.setChecked(true);
        }
        else {
            muteCheckBox.setChecked(false);
        }
        switch (difficulty){
            case "easy": radioGroup.check(R.id.easy); break;
            case "medium": radioGroup.check(R.id.medium); break;
            case  "hard": radioGroup.check(R.id.hard); break;
        }
    }
}