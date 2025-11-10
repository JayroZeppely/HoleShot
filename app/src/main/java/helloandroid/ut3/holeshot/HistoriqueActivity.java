package helloandroid.ut3.holeshot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class HistoriqueActivity extends AppCompatActivity {
    private LinearLayout histoTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_historique);

        Button main = findViewById(R.id.btn_main);
        main.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        histoTab = findViewById(R.id.histo_tab);

        //On veut récupérer les 10 meilleurs scores, tous joueurs confondus
        new Thread(() -> {
            try {
                List<String> top10 = DBConnexion.getTop10();
                runOnUiThread(() -> afficherTop10(top10));
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).start();
    }


    private void afficherTop10 (List < String > top10) {
        int rang = 1;

        for (String infoScore : top10) {
            //Log.d("DEBUG_TOP10", "infoScore = " + infoScore);
            String[] infos = infoScore.split("   ");
            String joueur = infos[0];
            String score = infos.length > 1 ? infos[1] : "0";

            //Pour chaque enregistrement dans la bbbd on recupere le joueur et le score
            TableRow row = new TableRow(this);

            TextView pseudoView = new TextView(this);
            TextView ScoreView = new TextView(this);

            pseudoView.setText(rang + ". \t\t" + joueur);
            ScoreView.setText(score);

            pseudoView.setTextSize(18);
            pseudoView.setPadding(10, 5, 10, 5);
            ScoreView.setTextSize(18);
            ScoreView.setPadding(10, 5, 10, 5);

            TableRow.LayoutParams lp = new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(250, 0, 0, 10);
            ScoreView.setLayoutParams(lp);

            row.addView(pseudoView);
            row.addView(ScoreView);
            histoTab.addView(row);

            rang++;

        }
    }
}