package com.aachol;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aachol.R;

public class LawsActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LawsActivity.this,MainActivity.class));
        LawsActivity.this.finish();
    }    private MediaPlayer mediaPlayer;    private Button playPauseButton;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);setContentView(R.layout.activity_laws);
        playPauseButton = findViewById(R.id.button);
        mediaPlayer = MediaPlayer.create(this, R.raw.musical);
        mediaPlayer.setLooping(true);   playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();playPauseButton.setText("Play");
                } else {
                    mediaPlayer.start();playPauseButton.setText("Pause");
                }           }        });
        RecyclerView recyclerView = findViewById(R.id.recycleLaws);
        String[] laws = new String[]{"The Prohibition of Child Marriage Act, 2006","Special Marriage Act, 1954","Dowry Prohibition Act, 1961","Indian Divorce Act, 1969","Maternity Benefit Act,1861","Medical Termination of Pregnancy Act,1971","Sexual Harassment of Women at Workplace (Prevention, Prohibition and Redress) Act, 2013","Indecent Representation of Women(Prevention) Act,1986","National Commission for Women Act, 1990","Equal Remuneration Act, 1976"};

        MyAdapter adapter = new MyAdapter(this, laws, position -> {
            Intent intent = new Intent(LawsActivity.this,LawDisplayerActivity.class);
            intent.putExtra("position",position);
            startActivity(intent);        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.backBtn).setOnClickListener(view -> {
            startActivity(new Intent(LawsActivity.this,MainActivity.class));
            LawsActivity.this.finish();
        });
    }    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }    }  }

