package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.ModeActivity.MODE_ENDLESS;
import static helloandroid.ut3.holeshot.ModeActivity.TIMER_DUREE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity implements GameView.GameEventListener {
    int timer;
    private Handler gameHandler;
    private TextView timeText;
    private TextView scoreText;
    private int score = 0;

    private boolean endless = false;

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        timer = intent.getIntExtra(TIMER_DUREE, 5);
        endless = intent.getBooleanExtra(MODE_ENDLESS, false);

        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //On fait rien, comme ça on empêche le retour en arrière
            }
        });

        // On affiche directement notre GameView, qui contient toute la logique du jeu
        // Note : On pourrait aussi utiliser un layout XML si on voulait superposer des éléments
        setContentView(R.layout.activity_game);
        timeText = findViewById(R.id.textTimer);
        timeText.setText(String.valueOf(timer));
        scoreText = findViewById(R.id.textScore);
        scoreText.setText("Score : " + score);
        gameView = findViewById(R.id.gameView);
        if (gameView != null) {
            gameView.setGameEventListener(this);
            gameView.setEndlessMode(endless);
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
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                startActivity(intent);
            }
        }
    };
    @Override
    public void onGameOver() {
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
}
