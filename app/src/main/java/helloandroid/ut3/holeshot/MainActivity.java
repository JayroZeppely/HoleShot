package helloandroid.ut3.holeshot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_GAMES_PLAYED = "total_games_played";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On charge le layout du menu
        setContentView(R.layout.activity_main);

        // Récupération des vues (bouton et label)
        TextView gamesPlayedLabel = findViewById(R.id.games_played_label);
        Button startGameButton = findViewById(R.id.start_game_button);
        Button btnHisto = findViewById(R.id.btn_histo);


        // --- GESTION DU COMPTEUR DE PARTIES AVEC SHARED PREFERENCES ---
        SharedPreferences sharedPref = getSharedPreferences("HoleShotPrefs", Context.MODE_PRIVATE);

        int gamesPlayed = sharedPref.getInt(KEY_GAMES_PLAYED, 0);
        Log.d("MainActivity", "Nombre de parties déjà jouées : " + gamesPlayed);

        // Mettre à jour le texte du label
        String labelText = "Parties jouées : " + gamesPlayed;
        gamesPlayedLabel.setText(labelText);

        // --- GESTION DU CLIC SUR LE BOUTON ---
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Incrémenter le compteur UNIQUEMENT quand on lance une partie
                int newGamesPlayed = gamesPlayed + 1;

                // Sauvegarder la nouvelle valeur
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(KEY_GAMES_PLAYED, newGamesPlayed);
                editor.apply();

                // Lancer la GameActivity
                Intent intent = new Intent(MainActivity.this, ModeActivity.class);
                startActivity(intent);
            }
        });

        btnHisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer la GameActivity
                Intent intent = new Intent(MainActivity.this, HistoriqueActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Optionnel : Mettre à jour le compteur au cas où on revient sur le menu
        // après avoir fini une partie.
        SharedPreferences sharedPref = getSharedPreferences("HoleShotPrefs", Context.MODE_PRIVATE);
        int gamesPlayed = sharedPref.getInt(KEY_GAMES_PLAYED, 0);
        TextView gamesPlayedLabel = findViewById(R.id.games_played_label);
        String labelText = "Parties jouées : " + gamesPlayed;
        gamesPlayedLabel.setText(labelText);
    }
}
