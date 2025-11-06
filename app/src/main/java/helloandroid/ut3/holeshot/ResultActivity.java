package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.MainActivity.KEY_GAMES_PLAYED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    TextView textFinish;
    Button btnQuit;
    Button btnRestart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textFinish = findViewById(R.id.text_finish);
        btnQuit = findViewById(R.id.btn_quit);
        btnRestart = findViewById(R.id.btn_restart);

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultActivity.this, ModeActivity.class);
                startActivity(intent);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Incrémenter le compteur UNIQUEMENT quand on lance une partie
        SharedPreferences sharedPref = getSharedPreferences("HoleShotPrefs", Context.MODE_PRIVATE);

        int gamesPlayed = sharedPref.getInt(KEY_GAMES_PLAYED, 0);

        int newGamesPlayed = gamesPlayed + 1;
        // Sauvegarder la nouvelle valeur
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_GAMES_PLAYED, newGamesPlayed);
        editor.apply();
    }


}