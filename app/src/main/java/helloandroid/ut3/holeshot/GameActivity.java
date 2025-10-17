package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.ModeActivity.TIMER_DUREE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    int timer;
    private Handler gameHandler;

    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameHandler = new Handler(Looper.getMainLooper());
        Intent intent = getIntent();
        timer = intent.getIntExtra(TIMER_DUREE, 60);

        gameHandler.postDelayed(setTimer,1000);


        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // On affiche directement notre GameView, qui contient toute la logique du jeu
        // Note : On pourrait aussi utiliser un layout XML si on voulait superposer des éléments
        setContentView(R.layout.activity_game);
        timeText = findViewById(R.id.textTimer);
        timeText.setText(String.valueOf(timer));
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
}
