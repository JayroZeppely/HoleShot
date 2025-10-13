package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.ModeActivity.TIMER_DUREE;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    int timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        timer = intent.getIntExtra(TIMER_DUREE, 60);


        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // On affiche directement notre GameView, qui contient toute la logique du jeu
        // Note : On pourrait aussi utiliser un layout XML si on voulait superposer des éléments
        setContentView(new GameView(this));
    }
}
