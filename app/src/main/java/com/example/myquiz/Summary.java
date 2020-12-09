package com.example.myquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.File;
import java.util.ArrayList;

public class Summary extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_BACKGROUND = "background";
    public static final String KEY_TEXT_COLOR = "textColor";

    private TextView congratsText;
    private Button playAgainButton;
    private ImageView bigPoint;
    private RelativeLayout layout;
    private ListView listView;
    private DBHelper mydb;
    private Player player;

    private String background;
    private ColorStateList textColor;
    private int score;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        congratsText = findViewById(R.id.congratsText);
        playAgainButton = findViewById(R.id.againButton);
        bigPoint = findViewById(R.id.bigPoint);
        listView = (ListView) findViewById(R.id.listView);
        layout = findViewById(R.id.summaryLayout);

        Intent intent = getIntent();
        score = intent.getIntExtra(Quiz.SCORE, 0);
        final String playerName = intent.getStringExtra("name");

        updateMessage(score);
        loadSettings();

        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(congratsText);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(playAgainButton);
        YoYo.with(Techniques.FadeIn).duration(1000).repeat(0).playOn(bigPoint);

        player = new Player();
        player.setName(playerName);
        player.setHighScore(score);
        Log.d("player:", player.getName() + player.getHighScore());

        mydb = new DBHelper(this);
        mydb.insert(player);

        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList = mydb.getPlayerList();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(textColor);
                return view;
            }
        };

        listView.setAdapter(arrayAdapter);

        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), Topics.class);
                intent.putExtra("name", playerName);
                startActivity(intent);
            }
        });
    }

    private void updateMessage(int score){
        Log.d("score", String.valueOf(score));
        congratsText.setText("Congratulations. You've earnd " + score + " points.");
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
            listView.setBackgroundResource(R.drawable.bgimg2);
        }
        else {
            layout.setBackgroundResource(R.drawable.bgimg);
            listView.setBackgroundResource(R.drawable.bgimg);
        }
        congratsText.setTextColor(textColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        menu.findItem(R.id.pause).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.share){

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "My new score is " + score);

            startActivity(Intent.createChooser(intent,"Share via"));
        }
        if (id == R.id.settings){
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivityForResult(intent, 1);
        }
        if (id == R.id.removeAll){
            mydb.RemoveAll();

            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList = mydb.getPlayerList();

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Summary.this,android.R.layout.simple_list_item_1, arrayList) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);
                    tv.setTextColor(textColor);
                    return view;
                }

            };

            listView.setAdapter(arrayAdapter);
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
}