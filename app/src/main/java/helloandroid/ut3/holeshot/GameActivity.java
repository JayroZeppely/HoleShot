package helloandroid.ut3.holeshot;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // On affiche directement notre GameView, qui contient toute la logique du jeu
        // Note : On pourrait aussi utiliser un layout XML si on voulait superposer des éléments
        setContentView(new GameView(this, null));
    }
}
