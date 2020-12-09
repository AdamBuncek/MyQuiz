package com.example.myquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT_COLOR = "textColor";

    private TextView textHeading;
    private Button start;
    private ImageView point;
    private EditText name;
    private LinearLayout layout;

    public String playerName = "";
    private String background;
    private ColorStateList textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textHeading = findViewById(R.id.textHeading);
        start = findViewById(R.id.startButton);
        point = findViewById(R.id.point);
        name = findViewById(R.id.editTextName);
        layout = findViewById(R.id.mainActivityLayout);
        loadSettings();

        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(textHeading);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(start);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(point);

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(name.length() > 0){
                    playerName = name.getText().toString();
                    startQuiz();
                }
                else {
                    Toast.makeText(MainActivity.this,"Enter your name please.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void startQuiz(){
        Intent intent = new Intent(getApplicationContext(), Topics.class);
        intent.putExtra("name",playerName);
        startActivity(intent);
        finish();
    }

    private void loadSettings(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        background = prefs.getString(KEY_BACKGROUND, "bgimg");
        textColor = ColorStateList.valueOf(prefs.getInt(KEY_TEXT_COLOR, 0xFFFFFF));
        Log.d("shprefs", background + textColor);
        setSettings();
    }

    private void setSettings(){
        if(background.equals("bgimg2")){
            layout.setBackgroundResource(R.drawable.bgimg2);
        }
        else {
            layout.setBackgroundResource(R.drawable.bgimg);
        }

        textHeading.setTextColor(textColor);
        name.setTextColor(textColor);
        name.setHintTextColor(textColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.pause).setVisible(false);
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