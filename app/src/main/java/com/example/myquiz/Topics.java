package com.example.myquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class Topics extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT_COLOR = "textColor";

    private TextView headingText;
    private TextView playerText;
    private Button history;
    private Button geography;
    private Button sport;
    private ConstraintLayout layout;

    private String background;
    private ColorStateList textColor;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topics);

        Intent intent = getIntent();
        final String playerName = intent.getStringExtra("name");

        headingText = findViewById(R.id.headingText);
        playerText = findViewById(R.id.playerName);
        history = findViewById(R.id.historyButton);
        geography = findViewById(R.id.geoButton);
        sport = findViewById(R.id.sportButton);
        layout = findViewById(R.id.topicsLayout);

        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(headingText);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(playerText);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(history);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(geography);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(sport);

        playerText.setText("Player: "+playerName);
        loadSettings();

        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Quiz.class);
                intent.putExtra("category","1");
                intent.putExtra("name",playerName);
                startActivity(intent);
                finish();
            }
        });

        geography.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Quiz.class);
                intent.putExtra("category","2");
                intent.putExtra("name",playerName);
                startActivity(intent);
                finish();
            }
        });

        sport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Quiz.class);
                intent.putExtra("category","3");
                intent.putExtra("name",playerName);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            finish();
        }
        else{
            Toast.makeText(this, "One more click to close.", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    private void loadSettings(){
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        background = prefs.getString(KEY_BACKGROUND, "bgimg");
        textColor = ColorStateList.valueOf(prefs.getInt(KEY_TEXT_COLOR, 0xFFFFFF));

        setSettings();
    }

    private void setSettings(){
        if(background.equals("bgimg2")){
            layout.setBackgroundResource(R.drawable.bgimg2);
        }
        else {
            layout.setBackgroundResource(R.drawable.bgimg);
        }
        headingText.setTextColor(textColor);
        playerText.setTextColor(textColor);
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