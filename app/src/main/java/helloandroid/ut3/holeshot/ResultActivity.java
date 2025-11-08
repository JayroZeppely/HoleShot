package helloandroid.ut3.holeshot;

import static helloandroid.ut3.holeshot.MainActivity.KEY_GAMES_PLAYED;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {

    TextView textFinish;
    TextView textScore;
    Button btnQuit;
    Button btnRestart;
    EditText pseudoInput;
    int score;


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
        textScore = findViewById(R.id.text_score);
        btnQuit = findViewById(R.id.btn_quit);
        btnRestart = findViewById(R.id.btn_restart);
        pseudoInput = findViewById(R.id.pseudo_input);
        score = getIntent().getIntExtra("score", 0);

        textScore.setText("Score : "+ score);

        pseudoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isPseudoEmpty = s.toString().trim().isEmpty();
                btnQuit.setEnabled(!isPseudoEmpty);
                btnRestart.setEnabled(!isPseudoEmpty);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScore();
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveScore();
                Intent intent = new Intent(ResultActivity.this, ModeActivity.class);
                startActivity(intent);
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                saveScore();
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

    //Envoi du score à la bdd
    private void saveScore(){
        String pseudo = pseudoInput.getText().toString().trim();
        if(pseudo.isEmpty()){
            return;
        }

        new Thread(() -> {
            try {
                DBConnexion.insertScore(pseudo, score);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }
}