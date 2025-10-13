package helloandroid.ut3.holeshot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log; // <-- Assurez-vous que Log est importé
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Clé constante pour le compteur de parties. Utiliser une constante évite les fautes de frappe.
    private static final String KEY_GAMES_PLAYED = "total_games_played";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // --- GESTION DU COMPTEUR DE PARTIES AVEC SHARED PREFERENCES ---

        // 1. Obtenir une instance de SharedPreferences pour cette activité
        // getPreferences est privé à l'activité, ce qui est parfait ici.
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

        // 2. Lire le nombre de parties déjà jouées. Valeur par défaut : 0.
        int gamesPlayed = sharedPref.getInt(KEY_GAMES_PLAYED, 0);

        // 3. Incrémenter le compteur
        gamesPlayed++;

        // 4. Afficher dans les logs pour le débogage
        Log.d("MainActivity", "Cette session est la partie numéro : " + gamesPlayed);

        // 5. Sauvegarder la nouvelle valeur
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(KEY_GAMES_PLAYED, gamesPlayed);
        editor.apply(); // Utiliser apply() pour une sauvegarde asynchrone

        // --- Lancement de la GameView ---
        setContentView(new GameView(this, null));
    }
}
