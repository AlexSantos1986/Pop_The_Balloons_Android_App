package com.alexsantos.gameappfirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GamePageActivity extends AppCompatActivity {

    private Button mPlayGame;
    private Button mlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_page);

        mPlayGame = (Button) findViewById(R.id.playGameId);
        mlogout = (Button) findViewById(R.id.logoutUserId);


        mPlayGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent playGameIntent = new Intent(GamePageActivity.this, MainActivity.class);
                playGameIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(playGameIntent);
            }
        });

        mlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logoutIntent = new Intent(GamePageActivity.this, LoginActivity.class);
                logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
            }
        });
    }
}
