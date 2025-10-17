package helloandroid.ut3.holeshot;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ModeActivity extends AppCompatActivity {
    public static final String TIMER_DUREE = "duree";
    public static final String MODE_ENDLESS = "mode";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // --- Configuration du mode plein écran ---
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mode);

        Button btn30 = findViewById(R.id.btn_30);
        Button btn45 = findViewById(R.id.btn_45);
        Button btn60 = findViewById(R.id.btn_60);
        Button btnEndless = findViewById(R.id.btn_endless);


        btn30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer la GameActivity
                Intent intent = new Intent(ModeActivity.this, GameActivity.class);
                intent.putExtra(TIMER_DUREE, 30);
                startActivity(intent);
            }
        });

        btn45.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer la GameActivity
                Intent intent = new Intent(ModeActivity.this, GameActivity.class);
                intent.putExtra(TIMER_DUREE, 45);
                startActivity(intent);
            }
        });

        btn60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer la GameActivity
                Intent intent = new Intent(ModeActivity.this, GameActivity.class);
                intent.putExtra(TIMER_DUREE, 60);
                startActivity(intent);
            }
        });

        btnEndless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer la GameActivity
                Intent intent = new Intent(ModeActivity.this, GameActivity.class);
                intent.putExtra(TIMER_DUREE, 5);
                intent.putExtra(MODE_ENDLESS, true);
                startActivity(intent);
            }
        });
    }
}