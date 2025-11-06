package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.MainActivity.KEY_GAMES_PLAYED;
import static helloandroid.ut3.holeshot.ModeActivity.MODES;
import static helloandroid.ut3.holeshot.ModeActivity.TIMER_DUREE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameView.GameEventListener, SensorEventListener {
    int timer;
    private Handler gameHandler;
    private TextView timeText;
    private TextView scoreText;
    private int score = 0;
    private int lives = 3; //valeur initiale
    private ImageView[] hearts;

    public boolean isDark = false;

    private String mode;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        timer = intent.getIntExtra(TIMER_DUREE, 5);
        mode = intent.getStringExtra(MODES);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //On fait rien, comme ça on empêche le retour en arrière
            }
        });

        setContentView(R.layout.activity_game);
        timeText = findViewById(R.id.textTimer);
        timeText.setText(String.valueOf(timer));
        scoreText = findViewById(R.id.textScore);
        scoreText.setText("Score : " + score);
        hearts = new ImageView[]{
                findViewById(R.id.life1),
                findViewById(R.id.life2),
                findViewById(R.id.life3)
        };
        setLives();
        gameView = findViewById(R.id.gameView);
        if (gameView != null) {
            gameView.setGameEventListener(this);
            gameView.setMode(mode);
        }
        gameHandler = new Handler(Looper.getMainLooper());
        gameHandler.postDelayed(setTimer,1000);
    }

    private Runnable setTimer = new Runnable() {
        public void run() {
            if (timer > 0) {
                timer--;
                timeText.setText(String.valueOf(timer));
                gameHandler.postDelayed(this, 1000);
            } else {
                gameHandler.removeCallbacks(this);
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        }
    };
    @Override
    public void onGameOver() {
        gameHandler.removeCallbacks(setTimer);
        Intent intent = new Intent(GameActivity.this, ResultActivity.class);
        startActivity(intent);
    }

    @Override
    public void onScoreChanged(int score) {
        this.score = score;
        runOnUiThread(() -> scoreText.setText("Score : " + score));
    }

    @Override
    public void resetTimer() {
        this.timer = 5;
        runOnUiThread(() -> timeText.setText(String.valueOf(timer)));
    }

    //Methodes pour gérer l'affichage des coeurs de vies
    @Override
    public void onLivesChanged(int lives) {
        this.lives = lives;
        runOnUiThread(this::setLives);
    }

    @Override
    public void onLightChanged(boolean isDark) {
        this.isDark = isDark;
        setLives();
    }

    private void setLives() {
        for (int i = 0; i < hearts.length; i++) {
            if(i < lives){
                if(!isDark) {
                    hearts[i].setImageResource(R.drawable.red);
                } else {
                    hearts[i].setImageResource(R.drawable.blue);
                }
            }else{
                hearts[i].setImageResource(R.drawable.black);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameHandler.removeCallbacks(setTimer);
    }
}
